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
│   └── sql/init.sql      # 数据库初始化脚本
├── blog-admin/           # 管理后台 (Vue 3)
└── blog-front/           # 博客前台 (Vue 3)
```

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
| GET | `/api/articles/{id}/comments` | 文章留言 |
| POST | `/api/articles/{id}/comments` | 发表留言 |
| GET/POST/PUT/DELETE | `/api/admin/*` | 后台管理接口 (需登录) |

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
