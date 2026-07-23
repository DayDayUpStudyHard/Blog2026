"""LLM 服务 — prompt 构建 + 流式调用 DeepSeek API。"""
from typing import Generator
from openai import OpenAI, APIError, APIConnectionError, AuthenticationError
from app.config import settings

RAG_SYSTEM_PROMPT = """你是 Blog2026 的知识库 AI 助手。根据提供的博客文章和知识库文档回答用户问题。

规则：
1. 答案在资料中找到 → 准确回答，用 [来源: 标题] 标明引用
2. 答案不在资料中 → 诚实说明“知识库中没有找到足够依据”，不要硬编
3. 回答简洁、友好、有温度，用 Markdown 格式组织
4. 用户问"你能做什么" → 介绍你可以基于博客文章和知识库文档回答问题
5. 问题与知识库无关 → 礼貌引导回学习、项目、面试资料相关话题"""


class LLMService:
    def __init__(self):
        if not settings.llm_api_key:
            raise ValueError("LLM_API_KEY 未设置，请在 .env 中配置")
        self.client = OpenAI(
            api_key=settings.llm_api_key,
            base_url=settings.llm_base_url,
        )
        self.model = settings.llm_model

    def validate_connection(self) -> str | None:
        """测试 LLM 连接（流式调用验证），返回 None 表示成功。"""
        try:
            stream = self.client.chat.completions.create(
                model=self.model,
                messages=[{"role": "user", "content": "hi"}],
                max_tokens=10,
                temperature=0.7,
                stream=True,
            )
            # 消费第一个 chunk 确认连接正常
            for chunk in stream:
                if chunk.choices[0].delta.content is not None:
                    break
            return None
        except AuthenticationError:
            return "LLM API Key 无效，请检查 LLM_API_KEY 配置"
        except APIConnectionError:
            return f"无法连接到 LLM 服务 ({settings.llm_base_url})，请检查网络或 BASE_URL"
        except APIError as e:
            return f"LLM API 错误 (model={self.model}): {e}"
        except Exception as e:
            return f"LLM 连接失败: {e}"

    def build_context(self, sources: list[dict]) -> str:
        """拼接检索结果为 RAG 上下文。"""
        if not sources:
            return "（未检索到相关知识库资料）"
        parts = []
        for i, a in enumerate(sources, 1):
            source_type = a.get("sourceType", "ARTICLE")
            label = "文档" if source_type == "DOCUMENT" else "文章"
            page = f" 第{a.get('page')}页" if a.get("page") else ""
            parts.append(
                f"[{label}{i}] 标题: {a['title']}{page}\n内容: {a['content'][:3000]}\n"
            )
        return "\n".join(parts)

    def build_messages(self, query: str, contexts: str,
                       history: list[dict]) -> list[dict]:
        messages = [
            {"role": "system", "content": RAG_SYSTEM_PROMPT},
            {"role": "system",
             "content": f"以下是相关博客文章和知识库文档，请参考回答并标明来源：\n\n{contexts}"},
        ]
        # 最近 10 轮对话
        for msg in history[-10:]:
            messages.append({"role": msg["role"], "content": msg["content"]})
        messages.append({"role": "user", "content": query})
        return messages

    def chat_stream(self, query: str, contexts: str,
                    history: list[dict]) -> Generator[str, None, None]:
        """流式调用 LLM，逐 token yield。

        Raises:
            AuthenticationError: API Key 无效
            APIConnectionError: 无法连接
            APIError: 其他 API 错误
        """
        messages = self.build_messages(query, contexts, history)
        stream = self.client.chat.completions.create(
            model=self.model,
            messages=messages,
            temperature=settings.chat_temperature,
            max_tokens=settings.chat_max_tokens,
            stream=True,
        )
        for chunk in stream:
            if chunk.choices[0].delta.content:
                yield chunk.choices[0].delta.content
