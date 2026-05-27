# Debug 修复记录

项目开发过程中遇到的问题及修复，按时间倒序记录。

---

## 智能旅行助手 — Token + 速度优化

**日期**：2026-05-28

### 改动

4 项优化，将单次请求 LLM 调用从 4 次减为 3 次，input token 减少约 50%。

1. **天气 Agent 改纯代码**：天气数据从 Amap API 返回结构固定，`dayweather` → `day_weather` 纯字段映射，无需 LLM 理解。删掉 `WEATHER_AGENT_PROMPT`，`_fetch_weather` 改为 dict 推导式转换。
2. **全面 compact 化**：所有 prompt 精简为单行紧凑格式，子代理输出统一 `json.dumps(indent=None)`，planner prompt 和输入去 Markdown 缩进标记。
3. **LLM timeout**：`_llm_chat` 添加 `timeout=60`，防止 LLM 服务挂起无限阻塞。
4. **Unsplash 并行化**：景点图片获取从顺序遍历改为 `ThreadPoolExecutor(max_workers=5)` 并行。
5. **默认模型**：`config.py` 从 `gpt-3.5-turbo` 改为 `deepseek-chat`。

### Token 对比

| 指标 | 优化前 | 优化后 |
|------|--------|--------|
| LLM 调用 | 4 次 | **3 次** |
| 输入 token (~3天) | ~8000 | **~3500** |
| 天气 prompt | 30 行 | 0（纯代码） |
| 景点 prompt | 25 行 | **3 行** |
| 酒店 prompt | 20 行 | **4 行** |
| Planner prompt (标准) | 50 行 | **15 行** |
| Planner prompt (紧凑) | 35 行 | **10 行** |

| 文件 | 改动 |
|------|------|
| `prompts.py` | 删除 WEATHER_AGENT_PROMPT；全部 prompt 精简为紧凑单行格式 |
| `trip_planner.py` | `_fetch_weather` 改纯代码；子代理统一 indent=None；`_llm_chat` 加 timeout；planner 输入 compact |
| `routes.py` | Unsplash ThreadPoolExecutor 并行 |
| `config.py` | 默认 model → deepseek-chat |

---

## blog-server — Lombok 优化：@RequiredArgsConstructor 替换显式构造器

**日期**：2026-05-27

### 改动

16 个 Controller/Service/Config 类的手写构造器替换为 `@RequiredArgsConstructor` 自动生成，净减少 37 行。

- 所有 `private final` 字段自动注入，无需手动 `this.x = x`
- `S3FileStorageService` 保留显式构造器（含 S3Client 初始化逻辑，非简单赋值型）

| 文件 | 改动 |
|------|------|
| 10 个 Controller | `@RequiredArgsConstructor` + 移除构造器 |
| 5 个 ServiceImpl | 同上 |
| `DataInitializer.java` | 同上 |

---

## blog-server — 全部 Java 文件添加类级 Javadoc 注释

**日期**：2026-05-27

### 改动

49 个 Java 文件补充类级 Javadoc，说明文件用途及值得注意的重点：

- **config**：Redis 序列化策略、CORS 注意点、Sa-Token 拦截路径、分页插件必要性、启动初始化密码硬编码风险
- **controller**：前台/后台职责区分、权限拦截、数据裁剪原因
- **entity**：逻辑删除 `@TableLogic`、非数据库字段 `exist=false`、自动填充策略
- **service**：事务边界 `@Transactional`、条件装配 `@ConditionalOnProperty`、密码安全

---

## 智能旅行助手 — LLM 跨天重复景点 + 降级补位

**日期**：2026-05-27

### 现象

LLM 在多天计划中有时会跨天重复推荐同一景点，去重后部分天数景点不足（< 2 个）。

### 修复

**四层防线：**

1. **Prompt 约束**：两个 planner prompt 加规则 `跨天不可重复景点`
2. **后处理去重**：`_normalize_plan` 中跨天去重，归一化名称后首次出现保留、后续移除
3. **两级补位**：景点不足 2 个时，先从 LLM 推荐景点池找未使用的（同类优先），再从 raw backup POI（泛关键词搜索）找
4. **替补标记**：补位景点设 `is_substitute = True`，前端用橙色序号 + `备选` 标签区分

**并行优化**：`_fetch_backup_poi` 与前三路 LLM 调用合并到同一个 ThreadPoolExecutor（4 workers），不影响响应时间。

| 文件 | 改动 |
|------|------|
| `schemas.py` | Attraction 加 `is_substitute` 字段 |
| `prompts.py` | 两个 planner prompt 加跨天不重复规则 |
| `trip_planner.py` | 新增 `_fetch_backup_poi`、`_parse_attraction_pool`、`_deduplicate_and_fill`、`_pick_substitute`；并行化 4 路 |
| `types/index.ts` | Attraction 加 `is_substitute?: boolean` |
| `Result.vue` | 替补景点橙色序号 + `备选` tag |

### 验证

4 天计划 0 重复景点，180s 内完成。

---

## 智能旅行助手 — max_tokens 截断导致 JSON 解析失败

**日期**：2026-05-26

### 现象

4 天计划报错 `Failed to parse LLM response as JSON`，返回的 JSON 在字段中间被截断。

### 根因

紧凑模式设了 `max_tokens=8192`，中文 JSON 单日约 500-800 token，4 天 + 酒店 + 天气轻松超过上限，被硬截断。

### 修复

| 模式 | 旧值 | 新值 |
|------|------|------|
| 标准 (≤3 天) | 4096 | 8192 |
| 精简 (>3 天) | 8192 | 16384 |

保留 `max_tokens` 做成本上限，但提到不会截断的水平。

---

## 智能旅行助手 — 长计划超时 + 按天数自适应

**日期**：2026-05-26

### 现象

10 天计划生成超时 (>300s)。

### 根因

LLM 处理数据量和输出量与天数正相关，长计划 prompt 巨大、响应时间长。

### 修复

- axios timeout 300s → 600s
- 按天数自适应：≤3 天标准模式（完整数据 + 详细 prompt），>3 天紧凑模式（POI 限 6 条 + 紧凑 prompt + 每天 2 景点 2 餐）
- 前置 LLM 调用并行化（ThreadPoolExecutor）

| 文件 | 改动 |
|------|------|
| `api.ts` | timeout 600s |
| `amap_service.py` | search_poi 加 offset 参数 |
| `prompts.py` | 新增 PLANNER_AGENT_PROMPT_COMPACT |
| `trip_planner.py` | 按天数自适应数据量/prompt/max_tokens |

---

## 智能旅行助手 — 前端日期选择改进

**日期**：2026-05-26

### 需求

结束日期改为自动计算（开始日期 + 天数 - 1），只读显示。防止用户选非法日期范围。

### 修复

- 移除结束日期选择器，改为 `disabled` 输入框
- `computed` 自动计算结束日期显示
- 开始日期加 `disabledDate`，不可选过去日期
- 调天数或开始日期实时联动

---

## 智能旅行助手 — 导出功能 + 偏好多选

**日期**：2026-05-26

### 需求

1. 完善图片/PDF 导出
2. 导出下拉菜单被遮挡
3. 旅行偏好支持多选

### 修复

- 安装 `html2canvas` + `jspdf`，实现真实导出（图片 PNG、PDF A4 多页自动分页）
- 导出下拉 `placement` 改为 `top`，向上弹出
- 偏好 Select 改为 `mode="multiple"`，提交时用 `、` 拼接

---

## 智能旅行助手 — 点击"开始规划"失败

**日期**：2026-05-26

### 现象

填写表单点击"开始规划"后等待约 3 分钟，提示"生成计划失败"或 timeout。

### 排查

1. **后端 API 验证**：直接 curl `POST /api/trip/plan` 返回 200，后端逻辑正常。
2. **编译错误**：`Result.vue:5` 中 `v-model:selectedKeys="[activeSection]"` 不合法，Vue v-model 不能绑定数组字面量。
3. **路由状态丢失**：Home → Result 用 `history.state` 传行程数据，iframe 内 history API 不可靠。
4. **前后端类型不一致**：后端 Pydantic `Attraction.location: {longitude, latitude}`（嵌套），前端 TS `Attraction.longitude`（扁平），导致数据解析异常。
5. **API 超时（根因）**：axios timeout 180s。后端 4 个 LLM 调用串行，DeepSeek 每次 25-50s，波动时超过 180s。

### 修复

| 文件 | 改动 |
|------|------|
| `tools/travel-assistant/frontend/src/views/Result.vue` | v-model 改用 computed；sessionStorage 读取数据 |
| `tools/travel-assistant/frontend/src/views/Home.vue` | sessionStorage 存储数据；新增行内错误展示和连接测试按钮 |
| `tools/travel-assistant/frontend/src/types/index.ts` | Location 改为嵌套结构对齐后端 |
| `tools/travel-assistant/frontend/src/services/api.ts` | timeout 180s → 300s |
| `tools/travel-assistant/frontend/tsconfig.node.json` | 补 composite: true |
| `tools/travel-assistant/backend/app/agents/trip_planner.py` | ThreadPoolExecutor 并行化 3 个前置 LLM 调用 |
| `tools/travel-assistant/backend/app/api/routes.py` | 新增 `/api/ping` 连通性测试端点 |

---

## 工具入口改为右下角浮窗

**日期**：2026-05-25

### 需求

小工具入口从顶部导航栏移到右下角浮窗，hover 弹出工具列表，点击工具在全屏 Modal 中通过 iframe 加载。

### 实现

| 文件 | 操作 |
|------|------|
| `blog-front/src/components/ToolsWidget.vue` | 新建，浮窗按钮 + 工具面板 + Modal + iframe |
| `blog-front/src/App.vue` | 引入 ToolsWidget |
| `blog-front/src/components/AppHeader.vue` | 移除"工具"导航链接 |
| `blog-front/src/router/index.js` | 移除 /tools 和 /tools/:toolId 路由 |
| `blog-front/src/views/ToolsHub.vue` | 删除 |
| `blog-front/src/views/ToolRunner.vue` | 删除 |

### 遇到的问题

- **iframe 加载 spinner 不消失**：`v-show` 隐藏的 iframe 不会触发 `@load` 事件。改为始终渲染 iframe + loading 遮罩层 + 20s 超时降级。
- **旅行助手后端 .env 加载失败**：`load_dotenv()` 无路径参数，uvicorn 从不同 CWD 启动时找不到 .env。修复：`load_dotenv(Path(__file__).resolve().parent.parent / ".env", override=True)`。
- **Pydantic 校验失败**：LLM 输出 `longitude`/`latitude` 为扁平常量，schema 要求嵌套 `location: {longitude, latitude}`。新增 `_normalize_location()` 做后处理，缺失坐标时回退到 `{0, 0}`。
- **uvicorn reload 端口冲突**：`reload=True` 产生 4 个子进程争抢 8001 端口。改为 `reload=False`。
