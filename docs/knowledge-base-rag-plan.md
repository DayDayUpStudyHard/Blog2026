# 个人知识库 RAG 改造实施方案

## 1. 目标定位

将 Blog2026 从“博客 + AI 问答”升级为“个人学习 / 项目 / 面试知识库 RAG 系统”。

第一版目标不是做复杂 Agent，而是先跑通可信知识库闭环：

```text
上传文档 -> 解析文本 -> 混合切片 -> 生成 embedding -> 写入 MySQL -> 写入 ES
-> 向量检索为主 -> 关键词 fallback -> LLM 回答 -> 返回引用来源 -> 记录 trace/评估
```

## 2. 已确认决策

| 编号 | 决策 | 结论 |
|---|---|---|
| 1 | 应用场景 | 个人学习 / 项目 / 面试知识库 |
| 2 | 第一版文档类型 | Markdown、TXT、PDF |
| 3 | 文章和知识库关系 | 分开管理，RAG 统一召回 |
| 4 | 空间模型 | 单用户多知识库空间 |
| 5 | 检索策略 | 向量检索为主，关键词 fallback |
| 6 | Embedding provider | OpenAI 兼容接口，默认 SiliconFlow / Qwen embedding |
| 7 | RAG 所属服务 | 全部放在 Python `chat-assistant` 微服务 |
| 8 | 数据事实源 | MySQL 存元数据和 chunk，原文件走文件存储，ES 存索引 |
| 9 | 切片策略 | 标题/段落优先 + 固定长度兜底 + overlap |
| 10 | 引用来源 | 第一版强制返回引用 |
| 11 | 文档状态 | `READY` 才可检索，`DISABLED` 不进入 RAG |
| 12 | 导入任务 | 异步导入 + 前端轮询 + 消息中心通知 |
| 13 | 通知存储 | MySQL 落库，前端 5-10 秒轮询，Redis 暂不做 |
| 14 | 问答入口 | 前台 AI 助手 + 后台问答测试台 |
| 15 | 搜索权限 | 普通搜索只搜 `PUBLIC` 文章 |
| 16 | RAG 权限 | `PRIVATE` 文章和 `DISABLED` 文档永远不可被 RAG 检索 |
| 17 | 删除策略 | 软删除 + 移除 ES 索引 + 支持恢复 |
| 18 | 永久删除 | 支持，需二次确认 |
| 19 | 文档详情 | 基本信息 + chunk 列表 + 索引状态 + 文档内检索测试 |
| 20 | 维护操作 | 支持重新解析和重新索引 |
| 21 | 可观测性 | 记录问答 trace / 检索 trace |
| 22 | 评估集 | 第一版做轻量评估集 |
| 23 | Debug 记录 | 支持 `Debug修复记录.md` 一键导入 |
| 24 | Agent 工具 | 第一版不做固定 Agent 工具，只做普通知识库问答 |

## 3. 服务边界

### 3.1 Java `blog-server`

负责博客主业务和管理端接口：

- 用户认证与后台权限
- 知识库空间元数据
- 知识库文档元数据
- 原始文件上传与保存
- 导入任务创建与状态查询
- 通知查询与已读
- 管理端知识库接口聚合
- 调用 Python `chat-assistant` 启动导入 / 重新索引 / 问答

Java 不负责复杂文档解析、embedding、RAG prompt 组装。

### 3.2 Python `tools/chat-assistant/backend`

升级为 RAG 能力中心：

- Markdown / TXT / PDF 文本解析
- 文本清洗
- 混合切片
- embedding 生成
- ES `kb_chunks` 索引写入
- 向量检索
- BM25 / IK fallback
- 文章索引与文档索引统一召回
- LLM 流式回答
- 引用来源生成
- 检索 trace / QA trace 写入
- 评估集运行

目录名可以先保持 `chat-assistant`，避免一次性重命名带来额外风险。

## 4. 存储设计

### 4.1 原始文件

原始文件不进 MySQL，保存到本地文件存储：

```text
upload/knowledge/{space_id}/{document_id}/{original_filename}
```

后续可切换为 S3 / MinIO / R2。

### 4.2 MySQL 表设计

#### `kb_space`

知识库空间。

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| name | VARCHAR(100) | 空间名称 |
| description | VARCHAR(500) | 描述 |
| icon | VARCHAR(50) | 图标 |
| color | VARCHAR(30) | 颜色 |
| sort | INT | 排序 |
| enabled | TINYINT | 是否启用 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | TINYINT | 软删除 |

#### `kb_document`

文档元数据。

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| space_id | BIGINT | 所属知识库空间 |
| title | VARCHAR(200) | 文档标题 |
| file_name | VARCHAR(255) | 原始文件名 |
| file_type | VARCHAR(20) | `MD` / `TXT` / `PDF` |
| file_size | BIGINT | 文件大小 |
| file_path | VARCHAR(500) | 原始文件存储路径 |
| status | VARCHAR(30) | `UPLOADED` / `PARSING` / `INDEXING` / `READY` / `FAILED` / `DISABLED` |
| chunk_count | INT | chunk 数 |
| embedding_model | VARCHAR(100) | embedding 模型 |
| embedding_dim | INT | 向量维度 |
| index_name | VARCHAR(100) | ES index 名称 |
| last_index_time | DATETIME | 最近索引时间 |
| error_message | TEXT | 失败原因 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | TINYINT | 软删除 |

#### `kb_document_chunk`

解析后的文本切片，是知识库事实源的一部分。

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| document_id | BIGINT | 文档 ID |
| space_id | BIGINT | 空间 ID |
| chunk_index | INT | 切片序号 |
| section_title | VARCHAR(255) | 所属标题 |
| source_page | INT | PDF 页码，非 PDF 可为空 |
| chunk_text | LONGTEXT | 切片正文 |
| char_count | INT | 字符数 |
| token_count | INT | 估算 token 数 |
| embedding_status | VARCHAR(30) | `PENDING` / `DONE` / `FAILED` |
| index_status | VARCHAR(30) | `PENDING` / `DONE` / `FAILED` |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | TINYINT | 软删除 |

#### `kb_ingest_job`

异步导入任务。

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| document_id | BIGINT | 文档 ID |
| job_type | VARCHAR(30) | `IMPORT` / `REPARSE` / `REINDEX` / `RESTORE` |
| status | VARCHAR(30) | `PENDING` / `PARSING` / `CHUNKING` / `EMBEDDING` / `INDEXING` / `DONE` / `FAILED` |
| progress | INT | 0-100 |
| message | VARCHAR(500) | 当前状态说明 |
| error_message | TEXT | 失败原因 |
| started_at | DATETIME | 开始时间 |
| finished_at | DATETIME | 结束时间 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### `kb_notification`

消息中心通知。

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| type | VARCHAR(50) | `INGEST_SUCCESS` / `INGEST_FAILED` / `REINDEX_SUCCESS` / `REINDEX_FAILED` |
| title | VARCHAR(200) | 通知标题 |
| content | VARCHAR(1000) | 通知内容 |
| related_type | VARCHAR(30) | `DOCUMENT` / `JOB` |
| related_id | BIGINT | 关联 ID |
| read_status | TINYINT | 0 未读，1 已读 |
| create_time | DATETIME | 创建时间 |

#### `kb_qa_session`

问答会话。

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| source | VARCHAR(30) | `FRONT` / `ADMIN_TEST` |
| scope | VARCHAR(50) | `GLOBAL` / `SPACE` / `DOCUMENT` |
| space_id | BIGINT | 可为空 |
| document_id | BIGINT | 可为空 |
| create_time | DATETIME | 创建时间 |

#### `kb_qa_message`

问答消息。

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| session_id | BIGINT | 会话 ID |
| role | VARCHAR(20) | `USER` / `ASSISTANT` |
| content | LONGTEXT | 消息内容 |
| model | VARCHAR(100) | LLM 模型 |
| latency_ms | BIGINT | 耗时 |
| create_time | DATETIME | 创建时间 |

#### `kb_retrieval_trace`

检索 trace。

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| message_id | BIGINT | 用户消息 ID |
| query | TEXT | 查询文本 |
| retrieval_type | VARCHAR(50) | `VECTOR` / `KEYWORD_FALLBACK` |
| top_k | INT | 召回数量 |
| latency_ms | BIGINT | 检索耗时 |
| fallback_reason | VARCHAR(500) | fallback 原因 |
| hit_count | INT | 命中数 |
| create_time | DATETIME | 创建时间 |

#### `kb_retrieval_hit`

召回命中明细。

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| trace_id | BIGINT | trace ID |
| source_type | VARCHAR(30) | `ARTICLE` / `DOCUMENT` |
| source_id | BIGINT | 文章 ID 或文档 ID |
| chunk_id | BIGINT | 文档 chunk ID，文章可为空 |
| title | VARCHAR(255) | 来源标题 |
| score | DOUBLE | 相似度 / BM25 分数 |
| snippet | TEXT | 命中片段 |
| rank_no | INT | 排名 |
| create_time | DATETIME | 创建时间 |

#### `kb_eval_case`

轻量评估用例。

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| question | VARCHAR(1000) | 标准问题 |
| expected_source_type | VARCHAR(30) | 期望来源类型 |
| expected_source_id | BIGINT | 期望来源 ID |
| expected_keywords | VARCHAR(1000) | 期望关键词，逗号分隔 |
| expected_points | TEXT | 期望答案要点 |
| enabled | TINYINT | 是否启用 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### `kb_eval_run` / `kb_eval_result`

记录评估批次和单条结果。

第一版核心指标：

- `source_hit_rate`
- `keyword_hit_rate`
- `avg_latency_ms`
- `fallback_rate`

## 5. Elasticsearch 设计

### 5.1 知识库文档索引

第一版使用单 index：

```text
kb_chunks
```

通过 `space_id` / `document_id` 过滤。

推荐 mapping：

```json
{
  "mappings": {
    "properties": {
      "chunk_id": { "type": "long" },
      "document_id": { "type": "long" },
      "space_id": { "type": "long" },
      "title": { "type": "text", "analyzer": "ik_max_word" },
      "content": { "type": "text", "analyzer": "ik_max_word" },
      "section_title": { "type": "text", "analyzer": "ik_max_word" },
      "source_page": { "type": "integer" },
      "status": { "type": "keyword" },
      "embedding_model": { "type": "keyword" },
      "embedding": {
        "type": "dense_vector",
        "dims": 1536,
        "index": true,
        "similarity": "cosine"
      },
      "create_time": { "type": "date" }
    }
  }
}
```

### 5.2 文章索引

第一版文章暂不迁移进 `kb_document_chunk`。文章继续使用现有文章索引，RAG 时统一召回：

```text
blog_articles + kb_chunks -> merge -> rerank/filter -> LLM context
```

后续第二版可考虑统一为：

```text
knowledge_chunks(source_type = ARTICLE / DOCUMENT / DEBUG_LOG)
```

## 6. 权限与可见性规则

### 6.1 普通搜索

普通搜索只允许搜索公开文章：

```sql
t_article.status = 1
AND t_article.deleted = 0
AND t_article.visibility = 'PUBLIC'
```

知识库文档永远不进入普通搜索。

### 6.2 RAG 检索

RAG 可检索：

```text
PUBLIC 文章
RAG_ONLY 文章
READY 知识库文档
```

RAG 永远不可检索：

```text
PRIVATE 文章
DISABLED 知识库文档
FAILED / PARSING / INDEXING 文档
deleted = 1 的文档
```

需要双保险：

```text
索引时不写入不可检索内容
检索时再次 filter 排除不可检索内容
状态变化时同步删除 / 更新 ES 索引
```

## 7. 文档导入流程

### 7.1 上传导入

```text
blog-admin 上传文档
-> blog-server 保存原始文件
-> blog-server 创建 kb_document
-> blog-server 创建 kb_ingest_job
-> blog-server 调用 chat-assistant /ingest/jobs
-> 前端开始轮询 job 状态
```

### 7.2 Python 异步处理

```text
PENDING
-> PARSING: 解析 MD/TXT/PDF
-> CHUNKING: 标题/段落优先，固定长度兜底，overlap
-> EMBEDDING: 调 OpenAI 兼容 embedding API
-> INDEXING: 写 ES kb_chunks
-> DONE: 更新文档 READY，写通知
```

失败时：

```text
-> FAILED
-> document.status = FAILED
-> 写 kb_notification(INGEST_FAILED)
```

### 7.3 消息中心

第一版：

```text
MySQL 存通知
前端每 5-10 秒轮询未读通知
右上角消息中心弹出成功 / 失败
```

暂不引入 Redis。

## 8. 切片策略

第一版采用混合切片：

```text
标题/段落优先 + 固定长度兜底 + overlap
```

建议参数：

```text
chunk_size_chars = 800-1000
chunk_overlap_chars = 150
```

### Markdown

```text
按 h1/h2/h3 标题分段
保留 section_title
过长段落按长度二次切片
```

### TXT

```text
按空行 / 段落切片
过长段落按长度二次切片
```

### PDF

```text
按页提取文本
每页内按段落切片
保留 source_page
过长段落按长度二次切片
```

## 9. 问答流程

### 9.1 前台 AI 助手

```text
用户提问
-> chat-assistant 生成 query embedding
-> 向量检索 blog_articles / kb_chunks
-> 低分或失败时关键词 fallback
-> 合并召回结果
-> 过滤权限
-> 构造 RAG prompt
-> LLM 流式回答
-> 返回简化引用来源
```

前台不暴露调试参数。

### 9.2 后台问答测试台

支持：

```text
选择知识库空间
选择指定文档
选择是否包含博客文章
设置 topK
显示检索方式：VECTOR / KEYWORD_FALLBACK
显示 chunk 分数
显示耗时
显示引用来源
显示 trace
```

## 10. 引用来源结构

所有回答必须返回引用：

```json
{
  "answer": "...",
  "citations": [
    {
      "sourceType": "DOCUMENT",
      "sourceId": 12,
      "chunkId": 88,
      "title": "Redis面试题.pdf",
      "page": 5,
      "sectionTitle": "缓存穿透",
      "snippet": "缓存穿透是指..."
    },
    {
      "sourceType": "ARTICLE",
      "sourceId": 22,
      "title": "Redis 缓存三级防护深度实践",
      "snippet": "..."
    }
  ]
}
```

检索不足时不硬编：

```text
知识库中没有找到足够依据，建议补充资料或换个问法。
```

## 11. 管理端页面

### 11.1 知识库空间列表

功能：

- 新建空间
- 编辑空间
- 启用 / 禁用
- 排序
- 查看文档数、chunk 数、最近更新时间

### 11.2 文档列表

功能：

- 上传 MD / TXT / PDF
- 按空间筛选
- 按状态筛选
- 显示已删除开关
- 查看导入状态
- 删除 / 恢复 / 永久删除
- 重新解析 / 重新索引
- 一键导入 `Debug修复记录.md`

### 11.3 文档详情

展示：

- 基本信息
- 解析状态
- embedding 状态
- ES 索引状态
- 失败原因
- chunk 列表
- chunk 正文预览
- 文档内检索测试

### 11.4 知识库问答测试台

展示：

- 问题输入
- 范围选择
- 回答正文
- 引用来源
- 检索 hits
- 分数
- fallback 原因
- 耗时

### 11.5 消息中心

功能：

- 未读数量
- 通知列表
- 标记已读
- 全部已读
- 点击跳转文档详情或任务详情

### 11.6 评估集页面

第一版轻量实现：

- 新增评估问题
- 运行评估
- 查看命中率
- 查看平均延迟
- 查看 fallback 比例
- 查看失败样例

## 12. 接口草案

### 12.1 Java 管理接口

```text
GET    /api/admin/kb/spaces
POST   /api/admin/kb/spaces
PUT    /api/admin/kb/spaces/{id}
DELETE /api/admin/kb/spaces/{id}

GET    /api/admin/kb/documents
POST   /api/admin/kb/documents/upload
GET    /api/admin/kb/documents/{id}
DELETE /api/admin/kb/documents/{id}
POST   /api/admin/kb/documents/{id}/restore
DELETE /api/admin/kb/documents/{id}/permanent
POST   /api/admin/kb/documents/{id}/reparse
POST   /api/admin/kb/documents/{id}/reindex
POST   /api/admin/kb/documents/import-debug-record

GET    /api/admin/kb/jobs/{id}
GET    /api/admin/kb/notifications
GET    /api/admin/kb/notifications/unread-count
PUT    /api/admin/kb/notifications/{id}/read
PUT    /api/admin/kb/notifications/read-all

POST   /api/admin/kb/qa/test
GET    /api/admin/kb/qa/sessions
GET    /api/admin/kb/qa/sessions/{id}

GET    /api/admin/kb/eval/cases
POST   /api/admin/kb/eval/cases
POST   /api/admin/kb/eval/run
GET    /api/admin/kb/eval/runs/{id}
```

### 12.2 Python `chat-assistant` 内部接口

```text
POST /internal/kb/ingest/jobs
POST /internal/kb/documents/{id}/reparse
POST /internal/kb/documents/{id}/reindex
POST /internal/kb/documents/{id}/restore-index
DELETE /internal/kb/documents/{id}/index

POST /api/chat/send
POST /api/kb/qa/test
POST /api/kb/eval/run
```

第一版如果鉴权来不及，可先限制本机调用，后续加内部 token。

## 13. Embedding 配置

不要把真实 key 写入文档或提交。

`.env` 使用：

```text
EMBEDDING_API_KEY=你的密钥
EMBEDDING_BASE_URL=https://api.siliconflow.cn/v1
EMBEDDING_MODEL=Qwen/Qwen3-Embedding-4B
EMBEDDING_DIM=1536
```

代码需要记录：

```text
embedding_model
embedding_dim
```

模型更换后，旧 chunk 需要重新索引或重新 embedding。

## 14. 删除和恢复

### 14.1 软删除

```text
kb_document.deleted = 1
kb_document.status = DISABLED
删除 ES 中 document_id 对应 chunks
保留 MySQL chunk
保留原始文件
```

### 14.2 恢复

```text
deleted = 0
status = INDEXING / RESTORING
使用 MySQL chunk 重建 ES 索引
成功后 status = READY
失败后 status = FAILED
```

### 14.3 永久删除

二次确认后执行：

```text
删除原始文件
删除 MySQL chunk
删除 MySQL document
删除 ES 索引
保留通知和任务历史，避免消息中心引用报错
```

## 15. 实施拆分

建议按小提交推进。

### Phase 1：数据库和基础模型

- 新增知识库 SQL 迁移
- Java entity / mapper / service
- 基础管理接口
- 文档上传元数据落库

### Phase 2：Python 文档解析与切片

- Markdown parser
- TXT parser
- PDF parser
- chunker
- 单元测试 / 样例文档

### Phase 3：Embedding 与 ES 索引

- `kb_chunks` mapping
- embedding provider 抽象
- 写入 ES
- 重新索引
- 删除索引

### Phase 4：异步任务和消息中心

- `kb_ingest_job`
- 任务状态更新
- `kb_notification`
- 前端轮询未读通知
- 右上角消息中心

### Phase 5：后台知识库页面

- 空间列表
- 文档列表
- 文档详情
- chunk 预览
- 重新解析 / 重新索引 / 删除 / 恢复 / 永久删除
- Debug 修复记录一键导入

### Phase 6：统一 RAG 问答

- 文档向量检索
- 文章索引召回
- 结果合并
- 权限过滤
- 引用来源
- 前台 ChatWindow 改造
- 后台问答测试台

### Phase 7：Trace 和评估集

- QA session/message
- retrieval trace/hit
- 评估用例
- 批量运行评估
- 命中率 / 延迟 / fallback 比例展示

## 16. 第一版验收标准

完成后应能演示：

1. 后台创建知识库空间。
2. 上传 Markdown / TXT / PDF。
3. 文档异步导入，右上角消息中心提示成功或失败。
4. 文档详情页查看 chunk、页码、索引状态。
5. 后台问答测试台指定空间提问，返回答案和引用。
6. 前台右下角 AI 助手可以同时检索博客文章和知识库文档。
7. `PRIVATE` 文章和 `DISABLED` 文档不会被 RAG 检索。
8. 删除文档后 ES 索引被移除，恢复后可重新索引。
9. `Debug修复记录.md` 可一键导入。
10. 评估集可运行并展示命中率、延迟和 fallback 比例。

## 17. 当前落地状态

截至 2026-07-24，本轮已落地第一版主干闭环：

| 模块 | 状态 | 说明 |
|---|---|---|
| SQL | 已新增 | `blog-server/sql/knowledge_base.sql` 包含空间、文档、chunk、任务、通知、trace、评估表 |
| Java 管理接口 | 已新增 | 空间、文档、切片、导入、重解析、重索引、删除、恢复、永久删除、通知、QA 测试代理 |
| Python RAG 能力 | 已新增 | MD/TXT/PDF 解析、混合切片、MySQL chunk 落库、ES `kb_chunks` 索引、向量主检索与关键词 fallback |
| 前台 ChatWindow | 已接入 | `/api/chat/send` 统一召回文章索引与文档索引，并返回引用来源 |
| 管理端页面 | 已新增 | `/knowledge` 支持空间、上传、Debug 记录导入、文档操作、切片预览、检索测试 |
| 消息中心 | 已新增 | 后台右上角轮询未读通知，异步导入成功/失败后提醒 |

已验证：

```text
python -m compileall tools/chat-assistant/backend/app
blog-server/.mvnw.cmd -DskipTests package
blog-admin npm run build
```

已在本机 `blog2026` 数据库执行：

```text
mysql -u root -p blog2026 < blog-server/sql/knowledge_base.sql
```

之后用项目启动脚本启动服务，在后台 `/knowledge` 导入 `Debug修复记录.md` 或上传文档验证真实链路。

## 18. 面试叙事

可包装为：

```text
Blog2026 个人知识库 RAG 系统：
基于 Spring Boot + Vue + Python FastAPI + Elasticsearch + MySQL 的个人学习/项目/面试知识库。
支持 Markdown/TXT/PDF 文档导入、混合切片、向量检索、关键词 fallback、引用来源、
异步导入任务、问答 trace 和轻量评估集。
```

关键亮点：

- 不是普通聊天框，而是可追溯 RAG。
- MySQL 事实源和 ES 检索索引分离。
- 向量检索主路径，关键词 fallback 保底。
- 私有内容在索引和检索两层都做权限过滤。
- 异步导入任务和消息中心体现真实产品工程。
- 评估集体现 RAG 效果治理意识。
