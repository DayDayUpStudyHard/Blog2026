import json
import logging
from openai import OpenAI
from app.config import settings
from app.models.schemas import TripPlanRequest, TripPlan
from app.services.amap_service import search_poi, get_weather
from app.agents.prompts import (
    ATTRACTION_AGENT_PROMPT,
    WEATHER_AGENT_PROMPT,
    HOTEL_AGENT_PROMPT,
    PLANNER_AGENT_PROMPT,
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

    def _llm_chat(self, system_prompt: str, user_message: str, temperature: float = 0.7) -> str:
        """调用LLM"""
        if not self.client:
            raise RuntimeError("LLM API key not configured. Please set LLM_API_KEY in .env file.")
        resp = self.client.chat.completions.create(
            model=self.model,
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_message},
            ],
            temperature=temperature,
        )
        return resp.choices[0].message.content

    def plan_trip(self, request: TripPlanRequest) -> TripPlan:
        logger.info(f"Planning trip for {request.city}, {request.days} days")

        # Step 1: Search for attractions
        logger.info("Step 1: Searching attractions...")
        attraction_data = search_poi(
            keywords=f"{request.preferences} 景点",
            city=request.city,
        )
        attraction_text = json.dumps(attraction_data, ensure_ascii=False, indent=2)
        attraction_response = self._llm_chat(
            ATTRACTION_AGENT_PROMPT,
            f"用户偏好: {request.preferences}\n城市: {request.city}\n\n景点搜索数据:\n{attraction_text}\n\n请整理出推荐的景点列表(JSON格式)"
        )

        # Step 2: Query weather
        logger.info("Step 2: Querying weather...")
        weather_data = get_weather(request.city)
        weather_text = json.dumps(weather_data, ensure_ascii=False, indent=2)
        weather_response = self._llm_chat(
            WEATHER_AGENT_PROMPT,
            f"城市: {request.city}\n\n天气数据:\n{weather_text}\n\n请整理出天气信息列表(JSON格式)"
        )

        # Step 3: Search hotels
        logger.info("Step 3: Searching hotels...")
        hotel_data = search_poi(
            keywords=f"{request.accommodation} 酒店",
            city=request.city,
        )
        hotel_text = json.dumps(hotel_data, ensure_ascii=False, indent=2)
        hotel_response = self._llm_chat(
            HOTEL_AGENT_PROMPT,
            f"住宿偏好: {request.accommodation}\n城市: {request.city}\n\n酒店搜索数据:\n{hotel_text}\n\n请整理出推荐的酒店列表(JSON格式)"
        )

        # Step 4: Generate full trip plan
        logger.info("Step 4: Generating trip plan...")
        planner_query = f"""
请根据以下信息生成{request.city}的{request.days}日旅行计划:

**用户需求:**
- 目的地: {request.city}
- 日期: {request.start_date} 至 {request.end_date}
- 天数: {request.days}天
- 偏好: {request.preferences}
- 预算: {request.budget}
- 交通方式: {request.transportation}
- 住宿类型: {request.accommodation}

**景点信息:**
{attraction_response}

**天气信息:**
{weather_response}

**酒店信息:**
{hotel_response}

请生成详细的旅行计划JSON。
"""
        planner_response = self._llm_chat(PLANNER_AGENT_PROMPT, planner_query, temperature=0.5)

        # Parse response
        trip_dict = self._parse_json(planner_response)
        trip_dict["city"] = request.city
        trip_dict["start_date"] = request.start_date
        trip_dict["end_date"] = request.end_date

        return TripPlan(**trip_dict)

    def _parse_json(self, text: str) -> dict:
        """Robust JSON parsing from LLM response"""
        # Try direct parse
        try:
            return json.loads(text)
        except json.JSONDecodeError:
            pass

        # Try to extract from markdown code blocks
        if "```json" in text:
            start = text.index("```json") + 7
            end = text.index("```", start)
            return json.loads(text[start:end].strip())
        if "```" in text:
            start = text.index("```") + 3
            end = text.index("```", start)
            return json.loads(text[start:end].strip())

        # Try to find JSON object in text
        try:
            start = text.index("{")
            end = text.rindex("}") + 1
            return json.loads(text[start:end])
        except (ValueError, json.JSONDecodeError):
            raise ValueError(f"Failed to parse LLM response as JSON: {text[:500]}...")
