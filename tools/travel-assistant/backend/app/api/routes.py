from fastapi import APIRouter, HTTPException
from app.models.schemas import TripPlanRequest, TripPlan
from app.agents.trip_planner import TripPlannerAgent
from app.services.unsplash_service import UnsplashService
from app.config import settings
import logging

logger = logging.getLogger(__name__)

router = APIRouter()

_trip_planner = None
_unsplash_service = None

def _get_planner():
    global _trip_planner
    if _trip_planner is None:
        _trip_planner = TripPlannerAgent()
    return _trip_planner

def _get_unsplash():
    global _unsplash_service
    if _unsplash_service is None and settings.unsplash_access_key:
        _unsplash_service = UnsplashService(settings.unsplash_access_key)
    return _unsplash_service


@router.post("/trip/plan", response_model=TripPlan)
async def create_trip_plan(request: TripPlanRequest) -> TripPlan:
    """生成旅行计划"""
    try:
        trip_plan = _get_planner().plan_trip(request)

        # Enrich with Unsplash images
        unsplash = _get_unsplash()
        if unsplash:
            for day in trip_plan.days:
                for attraction in day.attractions:
                    if not attraction.image_url:
                        try:
                            image_url = unsplash.get_photo_url(
                                f"{attraction.name} {trip_plan.city}"
                            )
                            attraction.image_url = image_url
                        except Exception as e:
                            logger.warning(f"Failed to get image: {e}")

        return trip_plan
    except Exception as e:
        logger.error(f"Trip planning failed: {e}")
        raise HTTPException(status_code=500, detail=f"生成旅行计划失败: {str(e)}")


@router.get("/health")
async def health_check():
    return {"status": "ok"}

@router.post("/ping")
async def ping():
    """Fast test endpoint to verify CORS + connectivity"""
    return {"status": "ok", "message": "Backend is reachable"}
