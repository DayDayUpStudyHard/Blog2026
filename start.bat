@echo off
REM Blog2026 一键启动脚本 (Windows)

echo ==============================
echo   Blog2026 一键启动
echo ==============================

echo [1/3] 启动后端...
start "blog-server" cmd /c "cd %~dp0blog-server && mvn spring-boot:run"

echo [2/3] 启动管理后台...
start "blog-admin" cmd /c "cd %~dp0blog-admin && npm run dev"

echo [3/3] 启动博客前台...
start "blog-front" cmd /c "cd %~dp0blog-front && npm run dev"

echo.
echo ==============================
echo   启动完成，等待服务就绪...
echo ==============================
echo.
echo   后端 API:    http://localhost:8080
echo   管理后台:    http://localhost:5173  (admin/admin123)
echo   博客前台:    http://localhost:5174
echo.
echo ==============================
