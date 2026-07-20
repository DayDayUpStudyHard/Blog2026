"""LLM 服务 — prompt 构建 + 流式调用 DeepSeek API。"""
from typing import Generator
from openai import OpenAI
from app.config import settings

RAG_SYSTEM_PROMPT = """你是本博客的AI助手。根据提供的博客文章内容回答用户问题。

规则：
1. 答案在文章中找到 → 准确回答，用 [来源: 文章标题] 标明引用
2. 答案不在文章中 → 诚实说明，可基于你的知识给出一般性建议
3. 回答简洁、友好、有温度，用 Markdown 格式组织
4. 用户问"你能做什么" → 介绍你基于博客文章回答问题的能力
5. 问题与博客无关 → 礼貌引导回博客相关话题"""


class LLMService:
    def __init__(self):
        self.client = OpenAI(
            api_key=settings.llm_api_key,
            base_url=settings.llm_base_url,
        )
        self.model = settings.llm_model

    def build_context(self, articles: list[dict]) -> str:
        """拼接检索到的文章为 RAG 上下文。"""
        parts = []
        for i, a in enumerate(articles, 1):
            parts.append(
                f"[文章{i}] 标题: {a['title']}\n内容: {a['content'][:3000]}\n"
            )
        return "\n".join(parts)

    def build_messages(self, query: str, contexts: str,
                       history: list[dict]) -> list[dict]:
        messages = [
            {"role": "system", "content": RAG_SYSTEM_PROMPT},
            {"role": "system",
             "content": f"以下是相关博客文章，请参考回答：\n\n{contexts}"},
        ]
        # 最近 10 轮对话
        for msg in history[-10:]:
            messages.append({"role": msg["role"], "content": msg["content"]})
        messages.append({"role": "user", "content": query})
        return messages

    def chat_stream(self, query: str, contexts: str,
                    history: list[dict]) -> Generator[str, None, None]:
        """流式调用 LLM，逐 token yield。"""
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
