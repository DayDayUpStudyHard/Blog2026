#!/bin/bash

# Blog2026 一键启动脚本
# 启动后端 + 管理后台 + 博客前台

set -e

ROOT="$(cd "$(dirname "$0")" && pwd)"

echo "=============================="
echo "  Blog2026 一键启动"
echo "=============================="

# 1. 启动后端 (Spring Boot)
echo "[1/7] 启动后端..."
(cd "$ROOT/blog-server" && chmod +x mvnw && ./mvnw spring-boot:run) &

# 2. 启动管理后台 (Vue3 + Element Plus)
echo "[2/7] 启动管理后台..."
(cd "$ROOT/blog-admin" && npm run dev) &

# 3. 启动博客前台 (Vue3 + Naive UI)
echo "[3/7] 启动博客前台..."
(cd "$ROOT/blog-front" && npm run dev) &

# 4. 启动旅行助手后端 (Python FastAPI)
echo "[4/7] 启动旅行助手后端..."
(cd "$ROOT/tools/travel-assistant/backend" && pip install -r requirements.txt -q && python run.py) &

# 5. 启动旅行助手前端 (Vue 3 + Ant Design Vue)
echo "[5/7] 启动旅行助手前端..."
(cd "$ROOT/tools/travel-assistant/frontend" && npm install && npm run dev) &

# 6. 启动加密解密工具箱 (Vue 3 + Ant Design Vue)
echo "[6/7] 启动加密解密工具箱..."
(cd "$ROOT/tools/crypto-toolbox/frontend" && npm install && npm run dev) &

# 7. 启动AI对话助手 (Python FastAPI)
echo "[7/7] 启动AI对话助手..."
(cd "$ROOT/tools/chat-assistant/backend" && pip install -r requirements.txt -q && python run.py) &

echo ""
echo "=============================="
echo "  启动完成，等待服务就绪..."
echo "=============================="
echo ""
echo "  后端 API:        http://localhost:8080"
echo "  管理后台:        http://localhost:5173  (admin/admin123)"
echo "  博客前台:        http://localhost:5174"
echo "  旅行助手后端:    http://localhost:8001"
echo "  旅行助手前端:    http://localhost:5175"
echo "  加密解密工具箱:  http://localhost:5176"
echo "  AI对话助手:      http://localhost:8088"
echo ""
echo "  按 Ctrl+C 停止所有服务"
echo "=============================="

wait
