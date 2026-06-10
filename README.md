# Blog2026 前后端分离博客系统

Spring Boot 3.2.5 + Vue 3 + MyBatis-Plus + Sa-Token

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.2.5, Java 17 |
| ORM | MyBatis-Plus 3.5.7 |
| 权限认证 | Sa-Token 1.43.0 (Redis) |
| 数据库 | MySQL 8.x |
| 缓存 | Redis |
| API文档 | Knife4j 4.5.0 |
| 管理后台 | Vue 3 + Element Plus + md-editor-v3 |
| 博客前台 | Vue 3 + Naive UI + marked |

## 项目结构

```
Blog2026/
├── blog-server/          # 后端 (Spring Boot)
│   ├── src/main/java/com/blog/
│   │   ├── common/       # 通用 (Result, GlobalExceptionHandler)
│   │   ├── config/       # 配置 (MyBatis-Plus, Sa-Token, CORS, Redis)
│   │   ├── controller/   # 控制器 (前台 + 后台)
│   │   ├── dto/          # 数据传输对象
│   │   ├── entity/       # 实体类
│   │   ├── mapper/       # MyBatis-Plus Mapper
│   │   └── service/      # 业务层
│   ├── src/main/resources/
│   │   └── application.yml
│   ├── src/test/java/     # Java 单元测试 (JUnit 5 + Mockito)
│   └── sql/init.sql      # 数据库初始化脚本
├── blog-admin/           # 管理后台 (Vue 3)
├── blog-front/           # 博客前台 (Vue 3)
├── api-tests/            # Python HTTP 集成测试（pytest 黑盒）
└── tools/                # 小工具平台
    ├── travel-assistant/ # 智能旅行助手
    │   ├── backend/      # Python FastAPI
    │   └── frontend/     # Vue 3 + TypeScript
    └── crypto-toolbox/   # 加密解密工具箱
        └── frontend/     # Vue 3 + TypeScript
```

## 核心功能

| 模块 | 功能 |
|------|------|
| 文章系统 | CRUD、分类/标签、Markdown 编辑、置顶、草稿/发布、全文搜索（ES + MySQL） |
| 评论系统 | 嵌套回复（楼中楼）、留言板、后台审核 |
| 体验增强 | 文章点赞（按IP去重）、阅读时长估算、代码高亮（highlight.js） |
| 站点管理 | 说说/动态、关于页（个人时间线）、站点信息、用户设置 |
| 数据归档 | 文章按年月归档、时间轴展示 |
| 运维审计 | 操作审计日志（AOP + 异步入库）、后台日志查询 |
| 监控 | Prometheus + Grafana、Spring Boot Actuator |
| 工程化 | Docker 7 服务编排、CI/CD、全局异常处理、Redis 缓存、限流、AOP 日志 |

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.9+
- MySQL 8.x
- Redis
- Node.js 18+

### 2. 初始化数据库

确保 MySQL 运行中，执行初始化脚本：

```bash
mysql -u root -p123456 < blog-server/sql/init.sql
```

### 3. 启动 Redis

确保 Redis 运行在 `localhost:6379`（默认端口，无密码）。

### 4. 启动后端

```bash
cd blog-server
mvn spring-boot:run
```

后端启动后访问：
- API 文档: http://localhost:8080/doc.html
- 默认管理员: **admin / admin123**（首次启动自动创建）

### 5. 启动管理后台

```bash
cd blog-admin
npm install
npm run dev
```

### 6. 启动博客前台

```bash
cd blog-front
npm install
npm run dev
```

## API 路由

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 登录 |
| GET | `/api/auth/info` | 获取用户信息 |
| GET | `/api/articles` | 文章列表 (公开) |
| GET | `/api/articles/{id}` | 文章详情 (公开) |
| GET | `/api/categories` | 分类列表 |
| GET | `/api/tags` | 标签列表 |
| GET | `/api/articles/{id}/comments` | 文章留言（含嵌套回复） |
| POST | `/api/articles/{id}/comments` | 发表留言/回复 |
| GET | `/api/articles/archive` | 文章归档（按年月分组） |
| GET/POST/PUT/DELETE | `/api/admin/*` | 后台管理接口 (需登录) |
| GET | `/api/admin/logs` | 操作审计日志 |

## 文件存储配置

图片上传支持**本地存储**和**S3 兼容云存储**（Cloudflare R2 / 阿里云 OSS / MinIO），通过 `application.yml` 切换。

### 本地存储（默认）

无需额外配置，上传文件保存在 `upload/` 目录下，通过 `/upload/xxx.png` 访问。

### Cloudflare R2

1. 在 Cloudflare Dashboard 创建 R2 Bucket
2. 在 `application.yml` 中配置：

```yaml
blog:
  storage:
    type: s3
    s3:
      endpoint: https://<account-id>.r2.cloudflarestorage.com
      region: auto
      access-key: <R2 Access Key ID>
      secret-key: <R2 Secret Access Key>
      bucket: blog-images
      public-url: https://cdn.yourdomain.com   # 可选：绑定自定义域名
```

> **注意：**
> - R2 的 endpoint 中 `<account-id>` 在 R2 管理页右上角可找到
> - Access Key 在 R2 页面 → "管理 R2 API 令牌" 创建，权限选"对象读和写"
> - 如果不配置 `public-url`，返回的 URL 为 `endpoint/bucket/key` 格式，可能无法直接访问，建议绑定自定义域名

### 阿里云 OSS

阿里云 OSS 支持 S3 兼容模式，配置方式与 R2 类似：

```yaml
blog:
  storage:
    type: s3
    s3:
      endpoint: https://oss-cn-hangzhou.aliyuncs.com
      region: oss-cn-hangzhou
      access-key: <AccessKey ID>
      secret-key: <AccessKey Secret>
      bucket: blog-images
      public-url: https://cdn.yourdomain.com   # 推荐：绑定 CDN 域名
```

> **注意：**
> - 需先在阿里云 RAM 控制台创建 AccessKey，并为对应用户授权 OSS 读写权限
> - Bucket 需设置 ACL 为"公共读"或绑定 CDN 域名，否则图片无法公网访问
> - `endpoint` 根据 Bucket 所在地域选择，见[OSS 地域列表](https://help.aliyun.com/document_detail/31837.html)

### MinIO（自建对象存储）

```yaml
blog:
  storage:
    type: s3
    s3:
      endpoint: http://localhost:9000
      region: us-east-1
      access-key: <MinIO Access Key>
      secret-key: <MinIO Secret Key>
      bucket: blog-images
      public-url: http://localhost:9000/blog-images
```

> **注意：**
> - MinIO 默认使用路径样式访问（`forcePathStyle: true`），代码已启用此选项
> - Bucket 需在 MinIO Console 中提前创建并设为 public 访问

### 图片压缩与缩略图

上传图片时自动处理（纯 JDK `javax.imageio` + `java.awt`，零外部依赖）：

| 处理 | 说明 |
|------|------|
| **压缩** | 宽度 > 1920px 或高度 > 1920px 时等比缩放至阈值内，JPEG 质量 80% |
| **缩略图** | 生成 400px 宽等比缩略图，文件名加 `_thumb` 后缀 |
| **GIF** | 动图跳过压缩（防动画丢失），仅生成静态缩略图 |
| **非图片** | 直接存储，不做处理 |

API 返回格式（图片文件额外包含 `thumbUrl`）：

```json
{
  "code": 200,
  "data": {
    "url": "/upload/abc123.jpg",
    "thumbUrl": "/upload/abc123_thumb.jpg"
  }
}
```

> 实现类：[`ImageUtil.java`](blog-server/src/main/java/com/blog/util/ImageUtil.java) — 静态工具方法，JPEG 使用 `ImageWriter` 精确控制压缩质量，PNG/BMP 保持原格式。

## 小工具平台

博客右下角浮窗提供小工具入口，hover 弹出工具列表，点击在全屏 Modal 中通过 iframe 加载。各工具独立部署，前后端分离。

### 智能旅行助手

输入城市、天数、偏好，自动生成多天旅行计划（景点推荐 + 餐饮搭配 + 酒店推荐 + 天气查询）。

| 层级 | 技术 |
|------|------|
| LLM 对话 | DeepSeek (OpenAI 兼容 API) |
| 后端框架 | FastAPI + Uvicorn |
| 数据校验 | Pydantic v2 |
| 地图服务 | 高德 POI 搜索 API |
| 图片服务 | Unsplash API |
| 前端框架 | Vue 3 + TypeScript |
| UI 组件 | Ant Design Vue 4 |
| 导出 | html2canvas + jsPDF（PNG / PDF） |

**项目结构：**

```
tools/travel-assistant/
├── backend/
│   ├── run.py                    # 启动入口 (uvicorn, port 8001)
│   ├── requirements.txt
│   └── app/
│       ├── api/
│       │   ├── main.py           # FastAPI app 工厂 + CORS
│       │   └── routes.py         # /api/trip/plan, /api/ping
│       ├── agents/
│       │   ├── prompts.py        # LLM prompt 模板（标准/紧凑）
│       │   └── trip_planner.py   # 规划引擎：LLM 调用、去重补位、并行编排
│       ├── models/
│       │   └── schemas.py        # Pydantic 数据模型
│       ├── services/
│       │   ├── amap_service.py   # 高德 POI 搜索 + 天气
│       │   └── unsplash_service.py  # Unsplash 城市封面图
│       └── config.py             # 环境变量配置
└── frontend/
    ├── vite.config.ts
    ├── package.json
    └── src/
        ├── views/
        │   ├── Home.vue          # 表单页（城市、天数、偏好、日期）
        │   └── Result.vue        # 结果展示 + 导出
        ├── services/api.ts       # axios 封装 (base URL, timeout 600s)
        ├── types/index.ts        # TypeScript 类型定义
        └── router/index.ts       # 路由配置
```

**架构对照（FastAPI vs Spring Boot）：**

每个后端文件都能在 Spring Boot 项目里找到对应角色：

| 旅行助手文件 | 角色 | Spring Boot 对应 |
|-------------|------|-----------------|
| `api/routes.py` | 路由定义，处理 HTTP 请求 → 调用 service → 返回响应 | **Controller**（`@RestController`） |
| `api/main.py` | 创建 FastAPI app，注册路由、CORS 中间件 | **`@SpringBootApplication` + WebConfig** |
| `models/schemas.py` | 请求/响应数据结构定义，入参校验 | **Entity + DTO**（`@Entity` / 自定义 DTO） |
| `agents/trip_planner.py` | 核心业务逻辑：编排 LLM 调用、去重补位、结果拼装 | **Service**（`@Service`） |
| `agents/prompts.py` | LLM 提示词模板常量 | **资源文件**（`application.yml` 中的配置 / i18n） |
| `services/amap_service.py` | 封装高德地图 API（POI、天气） | **Service + Feign Client** |
| `services/unsplash_service.py` | 封装 Unsplash 图片检索 API | **Service + RestTemplate** |
| `config.py` | 读取环境变量，集中管理配置 | **`application.yml` + `@ConfigurationProperties`** |

前后端分工上，`routes.py` 只做薄薄一层——接收请求、调 agent、返回结果——和
`ArticleController.java` 调用 `ArticleServiceImpl` 的模式一致。

**核心功能：**

- **Token 优化**：天气数据纯代码转换（零 LLM 调用），全面 compact prompt，3 次 LLM 调用完成规划，input token 减半
- **多路并行**：酒店/景点 2 个 LLM 调用 + 天气代码转换 + 备用 POI 并行（ThreadPoolExecutor），Unsplash 图片并行获取
- **跨天去重**：prompt 约束 + 后处理去重，保证多天无重复景点，不足时降级补位
- **自适应模式**：≤5 天标准模式（完整数据），>5 天紧凑模式（精简 POI + 每天 2 景点 2 餐）
- **LLM 超时保护**：每个 LLM 调用 60s 超时，防挂死
- **前端联动**：结束日期自动计算（开始 + 天数 - 1），不可选过去日期
- **偏好多选**：旅行偏好支持多选标签
- **导出**：PNG 图片 + PDF A4 多页自动分页

**启动：**

```bash
# 后端
cd tools/travel-assistant/backend
pip install -r requirements.txt
cp .env.example .env    # 编辑 .env 填入 API key
python run.py           # → http://localhost:8001

# 前端
cd tools/travel-assistant/frontend
npm install
npm run dev             # → http://localhost:5175
```

**环境变量 (.env)：**

| 变量 | 说明 |
|------|------|
| `LLM_API_KEY` | LLM API 密钥（支持 DeepSeek 等 OpenAI 兼容 API） |
| `LLM_BASE_URL` | LLM API 地址（默认 https://api.openai.com/v1） |
| `LLM_MODEL` | 模型名称（默认 deepseek-chat） |
| `AMAP_API_KEY` | 高德地图 Web API 密钥 |
| `UNSPLASH_ACCESS_KEY` | Unsplash API 访问密钥 |

### 加密解密工具箱

MD5 / SHA / AES / Base64 / URL 编解码，纯前端运行，数据不上传。

| 层级 | 技术 |
|------|------|
| 前端框架 | Vue 3 + TypeScript |
| UI 组件 | Ant Design Vue 4 |
| 加密库 | crypto-js 4.x |
| 构建工具 | Vite 5 |

**项目结构：**

```
tools/crypto-toolbox/
└── frontend/
    ├── vite.config.ts             # port 5176
    ├── package.json
    └── src/
        ├── main.ts                # Vue app 入口
        ├── App.vue
        ├── router/index.ts        # 路由配置
        └── views/
            └── Home.vue           # 5 个 Tab 的完整实现
```

**5 个工具模块：**

| Tab | 功能 | 算法 |
|-----|------|------|
| MD5 | 文本 → MD5 哈希 | `CryptoJS.MD5()` |
| SHA | SHA-1 / SHA-256 / SHA-512 可选 | `CryptoJS.SHA1/256/512()` |
| AES | 对称加解密，密钥 + 明文/密文双向 | `CryptoJS.AES.encrypt/decrypt()` |
| Base64 | UTF-8 ↔ Base64 编解码 | `CryptoJS.enc.Base64` |
| URL | URL 编解码 | `encodeURIComponent/decodeURIComponent` |

**特点：**

- **纯前端**：所有运算在浏览器本地完成，数据零上传
- **一键复制**：每个结果带复制按钮
- **错误处理**：解密/解码失败有明确错误提示（密钥错误、格式非法等）

**启动：**

```bash
cd tools/crypto-toolbox/frontend
npm install
npm run dev             # → http://localhost:5176
```

## API 黑盒测试（Python）

基于 pytest + requests 的 HTTP 集成测试，连真实服务器验证全部公开接口、认证流程和后台管理 CRUD。与 `blog-server/src/test/` 下的 Java 单元测试（JUnit 5 + Mockito，测 Service 层内部逻辑）互补，两者不重复。

| 层级 | 技术 |
|------|------|
| 测试框架 | pytest 9.x |
| HTTP 客户端 | requests (Session 复用) |
| 断言风格 | 原生 assert + 自定义辅助函数 |
| Fixtures | base_url, session, admin_token, auth_headers, test_data_tracker |
| 标记体系 | smoke / public / auth / admin / slow |

**项目结构：**

```
api-tests/
├── requirements.txt       # pytest + requests
├── pytest.ini             # 配置、markers、addopts
├── conftest.py            # 公共 fixtures + 断言辅助 + 测试数据清理
├── test_public.py         # 公开接口 — 21 tests（文章/分类/标签/说说/评论/留言板）
├── test_auth.py           # 认证接口 — 8 tests（登录/用户信息/改密/改资料）
└── test_admin.py          # 后台管理 — 30 tests（CRUD + 审核 + 日志 + 未授权拦截）
```

**测试覆盖（59 tests）：**

| 模块 | 测试数 | 覆盖接口 |
|------|--------|----------|
| `test_public.py` | 21 | `GET /api/articles` 列表/详情/导航/归档、`POST /like` / `GET /likes`、`GET /search`、分类、标签、说说、关于、站点、评论 CR、留言板 CR |
| `test_auth.py` | 8 | `POST /login`（成功/错误密码/空字段）、`GET /info`（无 token/有 token）、`PUT /password`、`PUT /profile` |
| `test_admin.py` | 30 | Article/Tag/Category/Moment CRUD、Comment 列表/审核、About 管理、操作日志、无权限拦截 |

**运行：**

```bash
cd api-tests
pip install -r requirements.txt

# 冒烟测试（仅公开接口）
pytest -m public

# 全部测试
pytest

# 跳过 DB 写入测试
pytest -m "not slow"

# 指定后端地址
BLOG_BASE_URL=http://localhost:8080 pytest

# 并行加速
pytest -n auto
```

**测试结果示例：**

```
======================== 52 passed, 7 xfailed in 1.18s ========================
```

> 7 个 `xfail` 为预期失败 — 标记了后台 `/api/admin/**` 路由缺少 Sa-Token 登录保护的已知安全问题，待修复后移除 xfail。
