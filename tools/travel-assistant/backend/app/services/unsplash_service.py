import requests
import logging
from typing import List, Dict, Optional
from app.config import settings

logger = logging.getLogger(__name__)


class UnsplashService:
    """Unsplash 图片服务"""

    def __init__(self, access_key: str):
        self.access_key = access_key
        self.base_url = "https://api.unsplash.com"

    def search_photos(self, query: str, per_page: int = 10) -> List[Dict]:
        """搜索图片"""
        try:
            url = f"{self.base_url}/search/photos"
            params = {
                "query": query,
                "per_page": per_page,
                "client_id": self.access_key,
            }
            resp = requests.get(url, params=params, timeout=10)
            resp.raise_for_status()
            data = resp.json()
            results = data.get("results", [])
            photos = []
            for r in results:
                photos.append({
                    "url": r["urls"]["regular"],
                    "description": r.get("description", ""),
                    "photographer": r["user"]["name"],
                })
            return photos
        except Exception as e:
            logger.error(f"Unsplash search error: {e}")
            return []

    def get_photo_url(self, query: str) -> Optional[str]:
        """获取单张图片URL"""
        photos = self.search_photos(query, per_page=1)
        return photos[0].get("url") if photos else None
