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
