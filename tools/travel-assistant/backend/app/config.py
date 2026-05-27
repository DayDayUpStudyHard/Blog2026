import os
from pathlib import Path
from dotenv import load_dotenv

load_dotenv(Path(__file__).resolve().parent.parent / ".env", override=True)


class Settings:
    amap_api_key: str = os.getenv("AMAP_API_KEY", "")
    unsplash_access_key: str = os.getenv("UNSPLASH_ACCESS_KEY", "")
    llm_api_key: str = os.getenv("LLM_API_KEY", "")
    llm_base_url: str = os.getenv("LLM_BASE_URL", "https://api.openai.com/v1")
    llm_model: str = os.getenv("LLM_MODEL", "deepseek-chat")


settings = Settings()
