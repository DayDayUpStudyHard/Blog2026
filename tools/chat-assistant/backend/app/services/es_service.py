"""ES 检索服务 — 用 IK 中文分词做 multi_match 搜索，取 top-k 文章作为 RAG 上下文。"""
from elasticsearch import Elasticsearch
from app.config import settings


class ESService:
    def __init__(self):
        self.client = Elasticsearch(settings.es_host)
        self.index = settings.es_index

    def search_articles(self, query: str, top_k: int = None) -> list[dict]:
        """多字段匹配搜索，过滤 visibility IN (PUBLIC, RAG_ONLY)，返回 top-k 结果。"""
        if top_k is None:
            top_k = settings.retrieval_top_k

        body = {
            "query": {
                "bool": {
                    "must": [{
                        "multi_match": {
                            "query": query,
                            "fields": ["title^3", "summary^2", "content"],
                            "type": "best_fields"
                        }
                    }],
                    "filter": [{
                        "terms": {"visibility": ["PUBLIC", "RAG_ONLY"]}
                    }]
                }
            },
            "highlight": {
                "fields": {
                    "content": {"fragment_size": 150, "number_of_fragments": 2}
                }
            },
            "size": top_k
        }

        try:
            response = self.client.search(index=self.index, body=body)
        except Exception:
            return []  # ES 不可用时降级为空

        hits = response["hits"]["hits"]
        results = []
        for hit in hits:
            source = hit["_source"]
            highlights = hit.get("highlight", {}).get("content", [])
            snippet = " ... ".join(highlights) if highlights else (source.get("summary", "") or "")[:200]
            results.append({
                "id": source["id"],
                "title": source["title"],
                "content": source.get("content", ""),
                "summary": source.get("summary", ""),
                "snippet": snippet,
                "score": hit["_score"],
            })
        return results
