@echo off
title Blog2026 Launcher

echo ==============================
echo   Blog2026 Start
echo ==============================
echo.

cd /d "%~dp0"

echo [1/3] Starting backend (Spring Boot :8080) ...
start "Blog2026-Server" cmd /k "title Blog2026-Server && cd /d %~dp0blog-server && mvn spring-boot:run"

echo [2/3] Starting admin (Vite :5173) ...
start "Blog2026-Admin" cmd /k "title Blog2026-Admin && cd /d %~dp0blog-admin && npm run dev"

echo [3/3] Starting front (Vite :5174) ...
start "Blog2026-Front" cmd /k "title Blog2026-Front && cd /d %~dp0blog-front && npm run dev"

echo.
echo ==============================
echo   All services launched:
echo     Backend:  http://localhost:8080
echo     Admin:    http://localhost:5173
echo     Front:    http://localhost:5174
echo ==============================
echo.
echo Close each window to stop the service.
pause
