import os
from pathlib import Path
from dotenv import load_dotenv

load_dotenv(Path(__file__).resolve().parent.parent / ".env", override=True)


class Settings:
    # LLM
    llm_api_key: str = os.getenv("LLM_API_KEY", "")
    llm_base_url: str = os.getenv("LLM_BASE_URL", "https://api.deepseek.com")
    llm_model: str = os.getenv("LLM_MODEL", "deepseek-chat")

    # Embedding（用同一家提供商的 embedding 端点）
    embedding_api_key: str = os.getenv("EMBEDDING_API_KEY", os.getenv("LLM_API_KEY", ""))
    embedding_base_url: str = os.getenv("EMBEDDING_BASE_URL", os.getenv("LLM_BASE_URL", "https://api.deepseek.com"))
    embedding_model: str = os.getenv("EMBEDDING_MODEL", "text-embedding-ada-002")

    # Elasticsearch
    es_host: str = os.getenv("ES_HOST", "http://localhost:9200")
    es_index: str = os.getenv("ES_INDEX", "blog_articles")

    # Chat
    chat_max_tokens: int = int(os.getenv("CHAT_MAX_TOKENS", "2048"))
    chat_temperature: float = float(os.getenv("CHAT_TEMPERATURE", "0.7"))
    retrieval_top_k: int = int(os.getenv("RETRIEVAL_TOP_K", "5"))


settings = Settings()
