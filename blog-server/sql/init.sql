-- Blog2026 数据库初始化脚本
CREATE DATABASE IF NOT EXISTS blog2026 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog2026;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    email VARCHAR(100),
    bio VARCHAR(255) COMMENT '一句话简介',
    social_links TEXT COMMENT '社交链接 JSON',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 分类表
CREATE TABLE IF NOT EXISTS t_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 标签表
CREATE TABLE IF NOT EXISTS t_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 文章表
CREATE TABLE IF NOT EXISTS t_article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content LONGTEXT,
    summary VARCHAR(500),
    category_id BIGINT,
    cover VARCHAR(255),
    is_top INT DEFAULT 0,
    status INT DEFAULT 0,
    view_count INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 文章标签关联表
CREATE TABLE IF NOT EXISTS t_article_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 留言表（article_id 为 NULL 表示留言板留言）
CREATE TABLE IF NOT EXISTS t_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT NULL,
    author VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    content TEXT NOT NULL,
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 说说表
CREATE TABLE IF NOT EXISTS t_moment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL COMMENT '说说内容',
    image VARCHAR(255) COMMENT '可选配图',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 文章点赞表
CREATE TABLE IF NOT EXISTS t_article_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT NOT NULL,
    user_ip VARCHAR(45) NOT NULL COMMENT '用户 IP（支持IPv6）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_article_id (article_id),
    UNIQUE KEY uk_article_ip (article_id, user_ip)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 关于页表（单行）
CREATE TABLE IF NOT EXISTS t_about (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content LONGTEXT COMMENT '关于页 Markdown 内容',
    timeline TEXT COMMENT '个人时间线 JSON',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 示例数据
-- ============================================

-- 管理员账号 (DataInitializer 也会自动创建此账号: admin / admin123)
INSERT IGNORE INTO t_user (username, password, nickname, email) VALUES
('admin',  '$2a$10$EBa0uuluAyanxFVQ2IUDA.vcH7ozd8r/Wb1s3fF91ajfVVBe.HsDW', '管理员', 'admin@blog.com'),
('zhangsan', '$2a$10$KqmjU46pUHW8M9vMcfewQe7ankyWXAUiM2PYhFn3jCpGHTKqs/Tve', '张三', 'zhangsan@blog.com'),
('lisi', '$2a$10$Ti/00kn8bQ8.w.d.sO/5FOmaJlDrwJqwEn1Jth6.ZrVGZy/Mm.NwC', '李四', 'lisi@blog.com');

-- 分类
INSERT INTO t_category (name, description, sort) VALUES
('技术分享', '技术文章与经验分享', 1),
('生活随笔', '生活感悟与日常记录', 2),
('前端开发', '前端技术栈相关文章', 3),
('后端开发', '后端架构与开发实践', 4);

-- 标签
INSERT INTO t_tag (name) VALUES
('Java'),
('Spring Boot'),
('React'),
('Vue'),
('MySQL'),
('Docker'),
('Linux'),
('JavaScript'),
('CSS'),
('MyBatis-Plus');

-- 文章
INSERT INTO t_article (title, content, summary, category_id, is_top, status, view_count, create_time) VALUES
(
    'Spring Boot 快速入门指南',
    '# Spring Boot 快速入门指南\n\n## 什么是 Spring Boot\n\nSpring Boot 是由 Pivotal 团队提供的全新框架，其设计目的是用来简化新 Spring 应用的初始搭建以及开发过程。\n\n## 核心特性\n\n- **自动配置**：Spring Boot 会自动配置你的 Spring 应用\n- **起步依赖**：将常用的依赖聚合在一起，简化依赖管理\n- **端点监控**：提供生产就绪的监控和管理功能\n\n## 快速开始\n\n### 1. 创建项目\n\n使用 Spring Initializr 创建一个新项目，选择以下依赖：\n\n```xml\n<dependency>\n    <groupId>org.springframework.boot</groupId>\n    <artifactId>spring-boot-starter-web</artifactId>\n</dependency>\n```\n\n### 2. 编写控制器\n\n```java\n@RestController\npublic class HelloController {\n    @GetMapping("/hello")\n    public String hello() {\n        return "Hello, Spring Boot!";\n    }\n}\n```\n\n### 3. 启动应用\n\n运行 main 方法，访问 `http://localhost:8080/hello` 即可看到结果。\n\n## 总结\n\nSpring Boot 让 Java 开发变得简单高效，是现代 Java 开发的必备技能。',
    '本文介绍 Spring Boot 的核心特性和快速入门方法，包含代码示例和项目搭建步骤。',
    1, 1, 1, 328, '2024-01-15 10:30:00'
),
(
    '使用 React Hooks 管理状态',
    '# 使用 React Hooks 管理状态\n\n## 为什么需要 Hooks\n\nHooks 是 React 16.8 引入的新特性，它可以让你在不编写 class 的情况下使用 state 以及其他的 React 特性。\n\n## 常用 Hooks\n\n### useState\n\n```jsx\nconst [count, setCount] = useState(0);\n```\n\n`useState` 返回一个数组，第一个元素是当前状态值，第二个是更新状态的函数。\n\n### useEffect\n\n```jsx\nuseEffect(() => {\n    document.title = `点击了 ${count} 次`;\n}, [count]);\n```\n\n`useEffect` 用于处理副作用操作，比如数据获取、订阅或手动修改 DOM。\n\n### useContext\n\n```jsx\nconst theme = useContext(ThemeContext);\n```\n\n让你更方便地使用 Context API。\n\n## 自定义 Hook\n\n```jsx\nfunction useLocalStorage(key, initialValue) {\n    const [value, setValue] = useState(() => {\n        const stored = localStorage.getItem(key);\n        return stored ? JSON.parse(stored) : initialValue;\n    });\n\n    useEffect(() => {\n        localStorage.setItem(key, JSON.stringify(value));\n    }, [key, value]);\n\n    return [value, setValue];\n}\n```\n\n## 总结\n\nHooks 让函数组件拥有了类组件的能力，同时代码更加简洁和可复用。',
    '详细介绍 React Hooks 的使用方式，包括 useState、useEffect 和自定义 Hook 的实际案例。',
    3, 0, 1, 215, '2024-02-20 14:20:00'
),
(
    'MySQL 索引优化实战',
    '# MySQL 索引优化实战\n\n## 索引的重要性\n\n在数据量较大时，合适的索引可以成百上千倍地提升查询速度。\n\n## 索引类型\n\n### 1. B+Tree 索引\n\n最常见的索引类型，适用于全值匹配、范围查询等场景。\n\n### 2. 哈希索引\n\n只适用于等值比较查询，不支持范围查询。\n\n### 3. 全文索引\n\n适用于全文搜索场景。\n\n## 创建索引的最佳实践\n\n```sql\n-- 为经常查询的列创建索引\nCREATE INDEX idx_username ON t_user(username);\n\n-- 复合索引，注意最左前缀原则\nCREATE INDEX idx_category_time ON t_article(category_id, create_time);\n```\n\n## 注意事项\n\n- 不要在低基数列上创建索引（如性别字段）\n- 避免在频繁更新的列上创建过多索引\n- 定期使用 EXPLAIN 分析查询语句\n\n## 总结\n\n正确使用索引是数据库优化的核心技能，需要结合具体业务场景来设计合适的索引策略。',
    '深入讲解 MySQL 索引的类型、创建原则和优化技巧，帮助提升数据库查询性能。',
    1, 0, 1, 187, '2024-03-10 09:00:00'
),
(
    'Docker 部署 Spring Boot 应用',
    '# Docker 部署 Spring Boot 应用\n\n## 为什么选择 Docker\n\nDocker 让应用的部署变得一致且可重复，解决了"在我机器上能跑"的问题。\n\n## 编写 Dockerfile\n\n```dockerfile\nFROM openjdk:17-jdk-slim\nWORKDIR /app\nCOPY target/*.jar app.jar\nEXPOSE 8080\nENTRYPOINT ["java", "-jar", "app.jar"]\n```\n\n## Docker Compose 编排\n\n```yaml\nversion: ''3''\nservices:\n  mysql:\n    image: mysql:8.0\n    environment:\n      MYSQL_ROOT_PASSWORD: 123456\n      MYSQL_DATABASE: blog2026\n  app:\n    build: .\n    ports:\n      - "8080:8080"\n    depends_on:\n      - mysql\n```\n\n## 常用命令\n\n```bash\n# 构建镜像\ndocker build -t blog-app .\n\n# 启动服务\ndocker-compose up -d\n\n# 查看日志\ndocker-compose logs -f app\n```\n\n## 总结\n\n使用 Docker 部署 Spring Boot 应用，可以让部署流程标准化，提高运维效率。',
    '手把手教你使用 Docker 和 Docker Compose 部署 Spring Boot 项目，附带完整配置示例。',
    4, 1, 1, 456, '2024-04-05 16:45:00'
),
(
    'Flexbox 布局完全指南',
    '# Flexbox 布局完全指南\n\n## 什么是 Flexbox\n\nFlexbox（弹性盒子）是 CSS3 的一种布局方式，可以轻松实现各种复杂的页面布局。\n\n## 容器属性\n\n```css\n.container {\n    display: flex;\n    flex-direction: row;\n    justify-content: space-between;\n    align-items: center;\n    flex-wrap: wrap;\n    gap: 16px;\n}\n```\n\n## 项目属性\n\n```css\n.item {\n    flex: 1;\n    align-self: flex-start;\n    order: 2;\n}\n```\n\n## 常见布局案例\n\n### 圣杯布局\n\n使用 Flexbox 实现经典的三栏布局只需几行代码。\n\n### 居中对齐\n\n```css\n.center {\n    display: flex;\n    justify-content: center;\n    align-items: center;\n}\n```\n\n### 等宽列\n\n```css\n.columns {\n    display: flex;\n}\n.column {\n    flex: 1;\n}\n```\n\n## 总结\n\n掌握 Flexbox 是现代前端开发的必备技能，可以大幅提升布局效率。',
    '从零开始学习 Flexbox 弹性布局，涵盖容器属性、项目属性及常见布局案例。',
    3, 0, 1, 142, '2024-04-20 11:30:00'
),
(
    'Linux 常用命令速查',
    '# Linux 常用命令速查\n\n## 文件操作\n\n```bash\nls -la          # 列出所有文件（含隐藏文件）\nfind / -name *.log  # 查找文件\ntar -xzf file.tar.gz  # 解压 tar.gz\n```\n\n## 系统信息\n\n```bash\ntop             # 查看系统进程\ndf -h           # 查看磁盘使用\nfree -m         # 查看内存使用\nps aux          # 查看所有进程\n```\n\n## 权限管理\n\n```bash\nchmod 755 file  # 修改文件权限\nchown user:group file  # 修改文件所有者\n```\n\n## 网络相关\n\n```bash\ncurl -X GET http://localhost:8080/api\nnetstat -tlnp   # 查看监听端口\nss -tlnp        # 查看 socket 状态\n```\n\n## 文本处理\n\n```bash\ngrep -r "keyword" ./    # 递归搜索\ntail -f app.log         # 实时查看日志\n```\n\n掌握这些常用命令可以大幅提升在 Linux 环境下的工作效率。',
    '整理 Linux 日常开发中最常用的命令，包括文件操作、系统监控、权限和网络等分类。',
    2, 0, 1, 98, '2024-05-08 08:15:00'
);

-- 文章-标签关联
INSERT INTO t_article_tag (article_id, tag_id) VALUES
(1, 1), (1, 2),          -- Spring Boot 快速入门 -> Java, Spring Boot
(2, 3), (2, 8),          -- React Hooks -> React, JavaScript
(3, 5),                  -- MySQL 索引优化 -> MySQL
(4, 2), (4, 6),          -- Docker 部署 -> Spring Boot, Docker
(5, 9),                  -- Flexbox -> CSS
(6, 7);                  -- Linux 命令 -> Linux

-- 留言
INSERT INTO t_comment (article_id, author, email, content, create_time) VALUES
(1, '王五', 'wangwu@example.com', '感谢分享，Spring Boot 入门教程写得很清楚！', '2024-01-16 11:00:00'),
(1, '赵六', 'zhaoliu@example.com', '请问可以出一期 Spring Security 的教程吗？', '2024-01-17 15:30:00'),
(4, '孙七', 'sunqi@example.com', 'Docker Compose 配置文件帮了大忙，已成功部署。', '2024-04-06 10:00:00');

-- 留言板留言（article_id 为 NULL）
INSERT INTO t_comment (article_id, author, email, content, create_time) VALUES
(NULL, '访客小明', 'xiaoming@example.com', '博客做得真棒！界面简洁大方，文章内容也很有深度，已收藏。', '2024-05-05 13:20:00'),
(NULL, '前端爱好者', 'fe_dev@example.com', 'Vue 3 + Naive UI 的组合确实很舒服，你的暗色模式切换做得也很流畅。', '2024-05-08 09:45:00'),
(NULL, '路过的Java开发', 'java_dev@example.com', 'Spring Boot 入门教程对我帮助很大，期待更多后端实践文章！', '2024-05-12 16:30:00'),
(NULL, '设计师小王', 'designer@example.com', '网站的配色和排版都很用心，毛玻璃效果加了不少分 👍', '2024-05-16 11:00:00'),
(NULL, '老读者', 'reader@example.com', '从你开始写博客就一直在关注，内容越来越好了，加油！', '2024-05-20 08:15:00');

-- 说说
INSERT INTO t_moment (content, create_time) VALUES
('今天终于把博客系统搭建完成了，Spring Boot + Vue 3 全家桶，开发体验非常流畅！', '2024-05-10 09:30:00'),
('分享一个实用技巧：MyBatis-Plus 的分页插件配置非常简单，配合 Page 对象一行代码搞定分页查询。', '2024-05-12 14:20:00'),
('周末研究了一下 Docker Compose，现在整个项目可以一键启动了：docker-compose up -d 🐳', '2024-05-15 16:45:00'),
('刚给博客加上了暗色模式支持，CSS 变量的威力真的太大了，几行代码就能实现全局主题切换。', '2024-05-18 11:00:00'),
('最近在学 Vue 3 的 Composition API，setup 语法糖写起来太舒服了，告别了 Options API 的繁琐。', '2024-05-20 20:15:00');
