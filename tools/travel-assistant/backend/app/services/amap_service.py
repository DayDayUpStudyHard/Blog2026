import requests
import logging
from typing import List, Dict, Any
from app.config import settings

logger = logging.getLogger(__name__)

BASE_URL = "https://restapi.amap.com/v3"


def search_poi(keywords: str, city: str, types: str = "") -> List[Dict[str, Any]]:
    """搜索POI（景点、酒店、餐厅等）"""
    params = {
        "key": settings.amap_api_key,
        "keywords": keywords,
        "city": city,
        "offset": 10,
        "output": "json",
    }
    if types:
        params["types"] = types

    try:
        resp = requests.get(f"{BASE_URL}/place/text", params=params, timeout=10)
        data = resp.json()
        if data.get("status") != "1":
            logger.warning(f"Amap POI search failed: {data.get('info')}")
            return []

        pois = data.get("pois", [])
        results = []
        for p in pois:
            loc = p.get("location", "0,0").split(",")
            results.append({
                "name": p.get("name", ""),
                "address": p.get("address", ""),
                "lng": float(loc[0]) if len(loc) >= 2 else 0,
                "lat": float(loc[1]) if len(loc) >= 2 else 0,
                "tel": p.get("tel", ""),
                "biz_ext": p.get("biz_ext", {}),
                "rating": p.get("biz_ext", {}).get("rating") if p.get("biz_ext") else None,
            })
        return results
    except Exception as e:
        logger.error(f"Amap POI search error: {e}")
        return []


def get_weather(city: str) -> Dict[str, Any]:
    """查询天气"""
    params = {
        "key": settings.amap_api_key,
        "city": city,
        "extensions": "all",
        "output": "json",
    }
    try:
        resp = requests.get(f"{BASE_URL}/weather/weatherInfo", params=params, timeout=10)
        data = resp.json()
        if data.get("status") != "1":
            logger.warning(f"Amap weather query failed: {data.get('info')}")
            return {}

        forecasts = data.get("forecasts", [])
        if forecasts:
            return forecasts[0]
        return {}
    except Exception as e:
        logger.error(f"Amap weather error: {e}")
        return {}
