import json
import logging
from concurrent.futures import ThreadPoolExecutor, as_completed
from openai import OpenAI
from app.config import settings
from app.models.schemas import TripPlanRequest, TripPlan
from app.services.amap_service import search_poi, get_weather
from app.agents.prompts import (
    ATTRACTION_AGENT_PROMPT,
    HOTEL_AGENT_PROMPT,
    PLANNER_AGENT_PROMPT,
    PLANNER_AGENT_PROMPT_COMPACT,
)

logger = logging.getLogger(__name__)


class TripPlannerAgent:
    def __init__(self):
        if not settings.llm_api_key:
            self.client = None
            self.model = None
            logger.warning("LLM API key not configured. Agent will not work until you set LLM_API_KEY in .env")
        else:
            self.client = OpenAI(
                api_key=settings.llm_api_key,
                base_url=settings.llm_base_url,
            )
            self.model = settings.llm_model

    def _llm_chat(self, system_prompt: str, user_message: str, temperature: float = 0.7, max_tokens: int | None = None) -> str:
        """调用LLM"""
        if not self.client:
            raise RuntimeError("LLM API key not configured. Please set LLM_API_KEY in .env file.")
        kwargs = dict(
            model=self.model,
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_message},
            ],
            temperature=temperature,
        )
        if max_tokens is not None:
            kwargs["max_tokens"] = max_tokens
        kwargs["timeout"] = 60
        resp = self.client.chat.completions.create(**kwargs)
        return resp.choices[0].message.content

    def _fetch_attractions(self, preferences: str, city: str, compact: bool = False) -> str:
        logger.info("Fetching attractions (compact=%s)...", compact)
        offset = 6 if compact else 10
        data = search_poi(keywords=f"{preferences} 景点", city=city, offset=offset)
        if compact and len(data) > 6:
            data = data[:6]
        text = json.dumps(data, ensure_ascii=False)
        return self._llm_chat(
            ATTRACTION_AGENT_PROMPT,
            f"偏好: {preferences}\n城市: {city}\nPOI数据: {text}\n请筛选推荐景点(紧凑JSON数组)"
        )

    def _fetch_weather(self, city: str) -> str:
        """纯代码转换天气数据，无需LLM"""
        logger.info("Transforming weather data...")
        data = get_weather(city)
        casts = data.get("casts", [])
        result = [{
            "date": c.get("date"),
            "day_weather": c.get("dayweather"),
            "night_weather": c.get("nightweather"),
            "day_temp": int(c.get("daytemp", 0)),
            "night_temp": int(c.get("nighttemp", 0)),
            "wind_direction": c.get("daywind"),
            "wind_power": c.get("daypower"),
        } for c in casts]
        return json.dumps(result, ensure_ascii=False)

    def _fetch_backup_poi(self, city: str) -> list[dict]:
        """Fetch raw POI backup pool for fallback when dedup leaves gaps."""
        logger.info("Fetching backup POI pool...")
        return search_poi(keywords="旅游景点", city=city, offset=15)

    def _fetch_hotels(self, accommodation: str, city: str, compact: bool = False) -> str:
        logger.info("Fetching hotels (compact=%s)...", compact)
        offset = 6 if compact else 10
        data = search_poi(keywords=f"{accommodation} 酒店", city=city, offset=offset)
        if compact and len(data) > 6:
            data = data[:6]
        text = json.dumps(data, ensure_ascii=False)
        return self._llm_chat(
            HOTEL_AGENT_PROMPT,
            f"偏好: {accommodation}\n城市: {city}\n酒店数据: {text}\n请筛选推荐酒店(紧凑JSON数组)"
        )

    def plan_trip(self, request: TripPlanRequest) -> TripPlan:
        compact = request.days > 5
        mode = "compact" if compact else "standard"
        logger.info(f"Planning trip for {request.city}, {request.days} days [{mode} mode]")

        # Steps 1-3: Run attractions, weather, hotels, backup POI in parallel
        attraction_response = None
        weather_response = None
        hotel_response = None
        backup_poi = None

        with ThreadPoolExecutor(max_workers=4) as executor:
            futures = {
                executor.submit(self._fetch_attractions, request.preferences, request.city, compact): "attractions",
                executor.submit(self._fetch_weather, request.city): "weather",
                executor.submit(self._fetch_hotels, request.accommodation, request.city, compact): "hotels",
                executor.submit(self._fetch_backup_poi, request.city): "backup_poi",
            }
            for future in as_completed(futures):
                name = futures[future]
                try:
                    result = future.result()
                    if name == "attractions":
                        attraction_response = result
                    elif name == "weather":
                        weather_response = result
                    elif name == "hotels":
                        hotel_response = result
                    elif name == "backup_poi":
                        backup_poi = result
                    logger.info(f"Completed: {name}")
                except Exception as e:
                    logger.error(f"Failed {name}: {e}")
                    raise

        # Parse LLM-recommended attraction pool for first-priority fill
        attraction_pool = self._parse_attraction_pool(attraction_response)

        # Step 4: Generate full trip plan
        logger.info("Step 4: Generating trip plan (%s mode)...", mode)

        if compact:
            prompt = PLANNER_AGENT_PROMPT_COMPACT
            planner_max_tokens = 16384
            planner_query = (f"城市:{request.city} {request.days}日 偏好:{request.preferences} 预算:{request.budget}"
                             f" 交通:{request.transportation} 住宿:{request.accommodation}"
                             f"\n景点:{attraction_response}\n天气:{weather_response}\n酒店:{hotel_response}")
        else:
            prompt = PLANNER_AGENT_PROMPT
            planner_max_tokens = 8192
            planner_query = (f"{request.city} {request.days}日 {request.start_date}-{request.end_date}"
                             f" 偏好:{request.preferences} 预算:{request.budget} 交通:{request.transportation}"
                             f" 住宿:{request.accommodation}"
                             f"\n景点:{attraction_response}\n天气:{weather_response}\n酒店:{hotel_response}")

        planner_response = self._llm_chat(prompt, planner_query, temperature=0.5, max_tokens=planner_max_tokens)

        # Parse response
        logger.info(f"Planner raw response (first 300 chars): {planner_response[:300]}")
        trip_dict = self._parse_json(planner_response)
        trip_dict["city"] = request.city
        trip_dict["start_date"] = request.start_date
        trip_dict["end_date"] = request.end_date

        # Cross-day dedup + fill gaps from backup pools
        trip_dict = self._deduplicate_and_fill(trip_dict, attraction_pool, backup_poi or [])

        return TripPlan(**trip_dict)

    @staticmethod
    def _normalize_location(item: dict) -> dict:
        """Convert flat longitude/latitude fields into nested location object."""
        lng = None
        lat = None
        # Extract from flat fields
        if "longitude" in item:
            lng = item.pop("longitude") or 0
        if "latitude" in item:
            lat = item.pop("latitude") or 0
        # Extract from nested location if present
        if "location" in item and isinstance(item["location"], dict):
            loc = item["location"]
            lng = lng or loc.get("longitude", 0)
            lat = lat or loc.get("latitude", 0)
        # Set default if both missing
        if "location" not in item:
            item["location"] = {
                "longitude": float(lng or 0),
                "latitude": float(lat or 0)
            }
        # Ensure values are floats
        loc = item["location"]
        loc["longitude"] = float(loc.get("longitude", 0))
        loc["latitude"] = float(loc.get("latitude", 0))
        return item

    def _parse_json(self, text: str) -> dict:
        """Robust JSON parsing from LLM response"""
        # Try direct parse
        try:
            data = json.loads(text)
            return self._normalize_plan(data)
        except json.JSONDecodeError:
            pass

        # Try to extract from markdown code blocks
        if "```json" in text:
            start = text.index("```json") + 7
            end = text.index("```", start)
            data = json.loads(text[start:end].strip())
            return self._normalize_plan(data)
        if "```" in text:
            start = text.index("```") + 3
            end = text.index("```", start)
            data = json.loads(text[start:end].strip())
            return self._normalize_plan(data)

        # Try to find JSON object in text
        try:
            start = text.index("{")
            end = text.rindex("}") + 1
            data = json.loads(text[start:end])
            return self._normalize_plan(data)
        except (ValueError, json.JSONDecodeError):
            raise ValueError(f"Failed to parse LLM response as JSON: {text[:500]}...")

    def _normalize_plan(self, data: dict) -> dict:
        """Post-process LLM output to match Pydantic schema (nested location objects)."""
        for day in data.get("days", []):
            if day.get("hotel"):
                self._normalize_location(day["hotel"])
            for attr in day.get("attractions", []):
                self._normalize_location(attr)
        return data

    @staticmethod
    def _parse_attraction_pool(response: str | None) -> list[dict]:
        """Parse LLM attraction response JSON array into list of dicts."""
        if not response:
            return []
        text = response.strip()
        # Try direct parse
        try:
            data = json.loads(text)
            return data if isinstance(data, list) else []
        except json.JSONDecodeError:
            pass
        # Try markdown code block
        for marker in ("```json", "```"):
            if marker in text:
                start = text.index(marker) + len(marker)
                end = text.index("```", start)
                try:
                    data = json.loads(text[start:end].strip())
                    return data if isinstance(data, list) else []
                except json.JSONDecodeError:
                    pass
        # Try find JSON array
        try:
            start = text.index("[")
            end = text.rindex("]") + 1
            data = json.loads(text[start:end])
            return data if isinstance(data, list) else []
        except (ValueError, json.JSONDecodeError):
            return []

    @staticmethod
    def _name_key(name: str) -> str:
        return name.strip().lower()

    def _deduplicate_and_fill(self, data: dict, attraction_pool: list[dict], backup_poi: list[dict]) -> dict:
        """Cross-day dedup + fill short days from backup pools."""
        min_per_day = 2
        seen = set()

        # Pass 1: normalize + collect seen names + cross-day dedup
        for day in data.get("days", []):
            if day.get("hotel"):
                self._normalize_location(day["hotel"])
            deduped = []
            for attr in day.get("attractions", []):
                self._normalize_location(attr)
                key = self._name_key(attr.get("name", ""))
                if key and key not in seen:
                    seen.add(key)
                    deduped.append(attr)
                else:
                    logger.info(f"Dedup removed: {attr.get('name')}")
            day["attractions"] = deduped

        # Pass 2: fill short days from LLM pool first, then backup POI
        for day in data.get("days", []):
            while len(day["attractions"]) < min_per_day:
                sub = self._pick_substitute(seen, attraction_pool, backup_poi)
                if sub is None:
                    break
                seen.add(self._name_key(sub.get("name", "")))
                self._normalize_location(sub)
                day["attractions"].append(sub)

        return data

    @staticmethod
    def _pick_substitute(seen: set, attraction_pool: list[dict], backup_poi: list[dict]) -> dict | None:
        """Pick one unused attraction from pools. LLM pool first, then raw POI."""
        # Try LLM-recommended pool
        for a in (attraction_pool or []):
            key = a.get("name", "").strip().lower()
            if key and key not in seen:
                a["is_substitute"] = True
                return a
        # Try raw backup POI
        for p in (backup_poi or []):
            key = p.get("name", "").strip().lower()
            if key and key not in seen:
                return {
                    "name": p.get("name", ""),
                    "address": p.get("address", ""),
                    "location": {"longitude": p.get("lng", 0), "latitude": p.get("lat", 0)},
                    "visit_duration": 120,
                    "description": "备选推荐景点",
                    "ticket_price": 0,
                    "category": "景点",
                    "is_substitute": True,
                }
        return None
