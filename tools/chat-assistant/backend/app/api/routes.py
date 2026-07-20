"""Chat API — SSE 流式对话 + RAG 检索。"""
import json
from fastapi import APIRouter
from fastapi.responses import StreamingResponse
from app.models.schemas import ChatRequest, SuggestionResponse
from app.services.es_service import ESService
from app.services.llm_service import LLMService

router = APIRouter()
es_service = ESService()
llm_service = LLMService()

DEFAULT_SUGGESTIONS = [
    "介绍一下Spring Boot的核心特性",
    "MySQL索引优化有哪些方法？",
    "Docker如何部署Spring Boot应用？",
    "Vue 3 和 Vue 2 有什么区别？",
    "博客里有哪些关于Redis的文章？",
]


def _sse(event_type: str, data: dict) -> str:
    """格式化为 SSE 事件。"""
    payload = json.dumps(data, ensure_ascii=False)
    return f"event: {event_type}\ndata: {payload}\n\n"


@router.post("/send")
async def chat_send(request: ChatRequest):
    """发送消息，返回 SSE 流。"""
    # 1. ES 检索
    articles = es_service.search_articles(request.message)
    contexts = llm_service.build_context(articles)
    sources = [
        {"id": a["id"], "title": a["title"], "snippet": a["snippet"]}
        for a in articles
    ]
    history_dicts = [{"role": m.role, "content": m.content}
                     for m in request.history]

    # 2. SSE 流式生成
    async def generate():
        try:
            yield _sse("status", {"status": "thinking"})
            full = ""
            for token in llm_service.chat_stream(
                request.message, contexts, history_dicts
            ):
                full += token
                yield _sse("chunk", {"content": token})
            yield _sse("sources", {"sources": sources})
            yield _sse("done", {"content": full})
        except Exception as e:
            yield _sse("error", {"error": str(e)})

    return StreamingResponse(
        generate(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "Connection": "keep-alive",
            "X-Accel-Buffering": "no",
        },
    )


@router.get("/suggestions")
async def get_suggestions():
    """推荐问题。"""
    return SuggestionResponse(suggestions=DEFAULT_SUGGESTIONS)


@router.get("/health")
async def health():
    return {"status": "ok"}
