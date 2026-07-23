"""
将 MySQL 文章批量索引到 Elasticsearch（含 embedding 语义向量）。

用法:
    cd tools/chat-assistant/backend
    python scripts/index_articles.py          # 不带 embedding（文本搜索）
    python scripts/index_articles.py --embed  # 带 embedding（语义搜索）

前置条件:
    - MySQL 可访问（使用 blog-server 的 application-dev.yml 配置）
    - ES 可访问（.env 中的 ES_HOST）
    - embedding 需要 .env 中配置 EMBEDDING_API_KEY
"""
import argparse
import sys
import os

# Add backend to path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import pymysql
from app.config import settings
from app.services.es_service import ESService
from app.services.embedding_service import EmbeddingService


def get_articles() -> list[dict]:
    """从 MySQL 读取所有需要索引的文章。"""
    # 直接使用 blog-server application-dev.yml 的数据库配置
    conn = pymysql.connect(
        host="localhost",
        port=3306,
        user="root",
        password="123456",
        database="blog2026",
        charset="utf8mb4",
        cursorclass=pymysql.cursors.DictCursor,
    )
    try:
        with conn.cursor() as cur:
            cur.execute(
                "SELECT id, title, content, summary, category_id, status, visibility, create_time "
                "FROM t_article "
                "WHERE visibility IN ('PUBLIC', 'RAG_ONLY')"
            )
            return cur.fetchall()
    finally:
        conn.close()


def main():
    parser = argparse.ArgumentParser(description="Index articles into ES")
    parser.add_argument(
        "--embed", action="store_true",
        help="Generate embedding vectors for semantic search"
    )
    parser.add_argument(
        "--dry-run", action="store_true",
        help="Show what would be indexed without actually indexing"
    )
    args = parser.parse_args()

    # 1. 连接 ES
    es = ESService()
    if not es.health():
        print(f"[ERROR] ES 不可达 ({settings.es_host})，请先启动 Elasticsearch")
        sys.exit(1)

    # 2. 确保索引存在（含 dense_vector mapping）
    if not es.ensure_index():
        print("[ERROR] 索引创建失败")
        sys.exit(1)
    print(f"[OK] 索引 '{settings.es_index}' 就绪")

    # 3. 读取文章
    print("[INFO] 读取 MySQL 文章...")
    articles = get_articles()
    print(f"[INFO] 共 {len(articles)} 篇文章需要索引")

    # 4. Embedding（可选）
    embedding_service = None
    if args.embed:
        embedding_service = EmbeddingService()
        if not embedding_service.configured:
            print("[ERROR] embedding 未配置（请在 .env 中设置 EMBEDDING_API_KEY 等）")
            sys.exit(1)
        print(f"[OK] Embedding 已配置: {settings.embedding_model} @ {settings.embedding_base_url}")

    # 5. 逐篇索引
    success = 0
    fail = 0
    for article in articles:
        embedding_vec = None
        if embedding_service:
            embed_text = f"{article['title']}\n{article.get('summary', '')}\n{article.get('content', '')}"
            embedding_vec = embedding_service.embed(embed_text)
            if embedding_vec is None:
                print(f"  [WARN] embedding 失败: [{article['id']}] {article['title'][:40]}")
                fail += 1
                continue  # 跳过这篇，不要不带 embedding 索引（会导致 mapping 不一致）

        if args.dry_run:
            print(f"  [DRY-RUN] [{article['id']}] {article['title'][:50]}"
                  f" (embedding: {'yes' if embedding_vec else 'no'})")
            success += 1
            continue

        ok = es.index_article(
            article_id=article["id"],
            title=article["title"],
            content=article["content"] or "",
            summary=article.get("summary") or "",
            category_id=article.get("category_id", 0),
            status=article.get("status", 1),
            visibility=article.get("visibility", "PUBLIC"),
            create_time=article["create_time"].strftime("%Y-%m-%d %H:%M:%S")
                         if article.get("create_time") else None,
            embedding=embedding_vec,
        )
        if ok:
            success += 1
            print(f"  [OK] [{article['id']}] {article['title'][:50]}")
        else:
            fail += 1
            print(f"  [FAIL] [{article['id']}] {article['title'][:50]}")

    print(f"\n[DONE] 成功: {success}, 失败: {fail}")


if __name__ == "__main__":
    main()
