"""知识库导入、索引和检索服务。"""
from __future__ import annotations

import logging

from app.config import settings
from app.services.document_parser import DocumentParser, HybridChunker
from app.services.embedding_service import EmbeddingService
from app.services.es_service import ESService
from app.services.kb_store import KbStore

logger = logging.getLogger(__name__)


class KbService:
    def __init__(self):
        self.store = KbStore()
        self.parser = DocumentParser()
        self.chunker = HybridChunker()
        self.embedding = EmbeddingService()
        self.es = ESService()

    def _index_chunks(
        self,
        rows: list[dict],
        job_id: int,
        progress_start: int,
        progress_span: int,
    ) -> int:
        if not rows:
            raise RuntimeError("未解析出可索引内容，请检查文档内容是否为空或格式是否受支持")
        if not self.es.ensure_kb_index():
            raise RuntimeError(
                f"ES 知识库索引不可用或向量维度不匹配，请检查 {settings.kb_index} mapping 和 EMBEDDING_DIM"
            )

        indexed_count = 0
        total = len(rows)
        for i, row in enumerate(rows, 1):
            vector = self.embedding.embed(row["chunk_text"]) if self.embedding.configured else None
            if self.embedding.configured and not vector:
                self.store.mark_chunk(row["id"], "FAILED", "PENDING")
                continue

            ok = self.es.index_kb_chunk(row, embedding=vector)
            if ok:
                indexed_count += 1
            self.store.mark_chunk(row["id"], "DONE" if vector else "SKIPPED", "DONE" if ok else "FAILED")
            progress = progress_start + int(i / total * progress_span)
            self.store.update_job(job_id, "INDEXING", min(progress, 95), f"已索引 {i}/{total} 个切片")

        if indexed_count == 0:
            raise RuntimeError(f"ES 索引失败：0/{total} 个切片写入成功，请检查 Elasticsearch 日志和 embedding 配置")
        return indexed_count

    def ingest_document(self, payload) -> None:
        doc_id = payload.documentId
        job_id = payload.jobId
        try:
            self.store.update_job(job_id, "PARSING", 10, "正在解析文档")
            self.store.update_document(doc_id, "PARSING")
            blocks = self.parser.parse(payload.filePath, payload.fileType)

            self.store.update_job(job_id, "CHUNKING", 30, "正在切片")
            chunks = self.chunker.chunk(blocks)
            self.store.replace_chunks(doc_id, payload.spaceId, chunks)
            self.store.update_document(doc_id, "INDEXING", chunk_count=len(chunks))

            self.store.update_job(job_id, "EMBEDDING", 55, "正在生成向量")
            rows = self.store.get_chunks(doc_id)
            indexed_count = self._index_chunks(rows, job_id, 55, 40)

            self.store.update_document(doc_id, "READY", chunk_count=len(rows), indexed=True)
            self.store.update_job(job_id, "DONE", 100, "导入完成")
            self.store.create_notification(
                "INGEST_SUCCESS",
                "知识库文档导入成功",
                f"{payload.title} 已生成 {len(rows)} 个切片，成功索引 {indexed_count} 个",
                "DOCUMENT",
                doc_id,
            )
        except Exception as exc:
            logger.exception("knowledge ingest failed")
            message = str(exc)
            self.store.update_document(doc_id, "FAILED", error=message)
            self.store.update_job(job_id, "FAILED", 100, "导入失败", message)
            self.store.create_notification(
                "INGEST_FAILED",
                "知识库文档导入失败",
                f"{payload.title}: {message}",
                "DOCUMENT",
                doc_id,
            )

    def reindex_document(self, document_id: int, job_id: int) -> None:
        try:
            rows = self.store.get_chunks(document_id)
            self.store.update_job(job_id, "INDEXING", 20, "正在重建索引")
            self.es.delete_kb_document(document_id)
            indexed_count = self._index_chunks(rows, job_id, 20, 75)
            self.store.update_document(document_id, "READY", chunk_count=len(rows), indexed=True)
            self.store.update_job(job_id, "DONE", 100, "索引完成")
            self.store.create_notification(
                "REINDEX_SUCCESS",
                "知识库文档索引完成",
                f"文档已重新进入 RAG 检索，成功索引 {indexed_count} 个切片",
                "DOCUMENT",
                document_id,
            )
        except Exception as exc:
            message = str(exc)
            self.store.update_document(document_id, "FAILED", error=message)
            self.store.update_job(job_id, "FAILED", 100, "索引失败", message)
            self.store.create_notification("REINDEX_FAILED", "知识库文档索引失败", message, "DOCUMENT", document_id)

    def qa_test(self, query: str, space_id: int | None = None, document_id: int | None = None, top_k: int = 5) -> dict:
        vector = self.embedding.embed(query) if self.embedding.configured else None
        retrieval_type = "VECTOR"
        hits = self.es.search_kb_by_embedding(vector, top_k, space_id, document_id) if vector else []
        if not hits:
            retrieval_type = "KEYWORD_FALLBACK"
            hits = self.es.search_kb_by_keyword(query, top_k, space_id, document_id)
        return {
            "retrievalType": retrieval_type,
            "hits": hits,
            "embeddingModel": settings.embedding_model if self.embedding.configured else "",
        }
