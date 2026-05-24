#!/bin/bash

# Blog2026 一键启动脚本
# 启动后端 + 管理后台 + 博客前台

set -e

ROOT="$(cd "$(dirname "$0")" && pwd)"

echo "=============================="
echo "  Blog2026 一键启动"
echo "=============================="

# 1. 启动后端 (Spring Boot)
echo "[1/3] 启动后端..."
(cd "$ROOT/blog-server" && mvn spring-boot:run) &

# 2. 启动管理后台 (Vue3 + Element Plus)
echo "[2/3] 启动管理后台..."
(cd "$ROOT/blog-admin" && npm run dev) &

# 3. 启动博客前台 (Vue3 + Naive UI)
echo "[3/3] 启动博客前台..."
(cd "$ROOT/blog-front" && npm run dev) &

echo ""
echo "=============================="
echo "  启动完成，等待服务就绪..."
echo "=============================="
echo ""
echo "  后端 API:    http://localhost:8080"
echo "  管理后台:    http://localhost:5173  (admin/admin123)"
echo "  博客前台:    http://localhost:5174"
echo ""
echo "  按 Ctrl+C 停止所有服务"
echo "=============================="

wait
