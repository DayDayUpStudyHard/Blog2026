<template>
  <div class="admin-app">
    <div class="bg-blobs">
      <div class="bg-blob blob-1"></div>
      <div class="bg-blob blob-2"></div>
      <div class="bg-blob blob-3"></div>
    </div>
    <router-view v-slot="{ Component }">
      <Transition name="page" mode="out-in">
        <component :is="Component" />
      </Transition>
    </router-view>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'

const theme = ref(localStorage.getItem('blog-admin-theme') || 'light')

function applyTheme(t) {
  document.documentElement.setAttribute('data-theme', t)
  localStorage.setItem('blog-admin-theme', t)
}

onMounted(() => applyTheme(theme.value))
watch(theme, applyTheme)

// Expose for other components
window.__adminTheme = theme
</script>

<style>
.admin-app { min-height: 100vh; position: relative; }

.bg-blobs { position: fixed; inset: 0; z-index: 0; pointer-events: none; overflow: hidden; }

.bg-blob {
  position: absolute; border-radius: 50%; filter: blur(100px); opacity: 0.35;
  will-change: transform;
}

.blob-1 {
  width: 500px; height: 500px;
  background: rgba(64,158,255,0.14);
  top: -10%; right: -5%;
  animation: blobFloat1 14s ease-in-out infinite;
}

.blob-2 {
  width: 420px; height: 420px;
  background: rgba(139,92,246,0.10);
  bottom: -8%; left: -3%;
  animation: blobFloat2 16s ease-in-out infinite;
  animation-delay: -2s;
}

.blob-3 {
  width: 360px; height: 360px;
  background: rgba(16,185,129,0.09);
  top: 40%; left: 50%;
  animation: blobFloat3 18s ease-in-out infinite;
  animation-delay: -5s;
}

@keyframes blobFloat1 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -25px) scale(1.06); }
  66% { transform: translate(-15px, 20px) scale(0.94); }
}

@keyframes blobFloat2 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(-25px, 20px) scale(1.04); }
  66% { transform: translate(20px, -15px) scale(0.96); }
}

@keyframes blobFloat3 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(18px, -28px) scale(1.05); }
}

.page-enter-active,
.page-leave-active {
  transition: all 0.25s ease;
}
.page-enter-from {
  opacity: 0;
  transform: translateY(12px) scale(0.98);
}
.page-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

/* ═══ Dark Mode ═══ */
[data-theme="dark"] .admin-app {
  color: #e0e0e0;
}

[data-theme="dark"] body {
  background: #0f172a;
}

[data-theme="dark"] .bg-blob {
  filter: blur(100px); opacity: 0.2;
}
[data-theme="dark"] .blob-1 { background: rgba(64,158,255,0.18); }
[data-theme="dark"] .blob-2 { background: rgba(139,92,246,0.13); }
[data-theme="dark"] .blob-3 { background: rgba(16,185,129,0.10); }

/* Sidebar */
[data-theme="dark"] .sidebar-inner {
  background: rgba(15, 23, 42, 0.85) !important;
  border-right-color: rgba(255,255,255,0.06) !important;
}
[data-theme="dark"] .logo-text { color: #e2e8f0 !important; }
[data-theme="dark"] .menu :deep(.el-menu-item) { color: #94a3b8 !important; }
[data-theme="dark"] .menu :deep(.el-menu-item):hover { background: rgba(64,158,255,0.1) !important; color: #60a5fa !important; }
[data-theme="dark"] .menu :deep(.el-menu-item.is-active) { background: rgba(64,158,255,0.15) !important; }
[data-theme="dark"] .sidebar-footer { border-top-color: rgba(255,255,255,0.06) !important; }
[data-theme="dark"] .user-name { color: #e2e8f0 !important; }
[data-theme="dark"] .user-role { color: #94a3b8 !important; }

/* Topbar */
[data-theme="dark"] .topbar {
  background: rgba(15, 23, 42, 0.8) !important;
  border-bottom-color: rgba(255,255,255,0.06) !important;
}
[data-theme="dark"] .topbar-path { color: #e2e8f0 !important; }
[data-theme="dark"] .action-btn { color: #94a3b8 !important; border-color: rgba(255,255,255,0.1) !important; }
[data-theme="dark"] .action-btn:hover { color: #60a5fa !important; border-color: #60a5fa !important; background: rgba(64,158,255,0.1) !important; }
[data-theme="dark"] .logout-btn:hover { background: rgba(245,108,108,0.1) !important; border-color: rgba(245,108,108,0.2) !important; }

/* Main area */
[data-theme="dark"] .main-area { background: transparent; }

/* Cards & panels */
[data-theme="dark"] .stat-card {
  background: rgba(30, 41, 59, 0.75) !important;
  border-color: rgba(255,255,255,0.06) !important;
}
[data-theme="dark"] .num { color: #e2e8f0 !important; }
[data-theme="dark"] .label { color: #94a3b8 !important; }
[data-theme="dark"] .page-title { color: #e2e8f0 !important; }

/* Forms */
[data-theme="dark"] .el-input__inner {
  background: rgba(30,41,59,0.8) !important;
  border-color: rgba(255,255,255,0.1) !important;
  color: #e2e8f0 !important;
}
[data-theme="dark"] .el-input__inner:focus {
  border-color: #409EFF !important;
}
[data-theme="dark"] .el-input__inner::placeholder { color: #64748b !important; }

/* Table */
[data-theme="dark"] .el-table {
  background: rgba(30,41,59,0.6) !important;
  color: #e2e8f0 !important;
}
[data-theme="dark"] .el-table th {
  background: rgba(30,41,59,0.8) !important;
  color: #94a3b8 !important;
}
[data-theme="dark"] .el-table td { border-bottom-color: rgba(255,255,255,0.05) !important; }
[data-theme="dark"] .el-table tr:hover td { background: rgba(64,158,255,0.05) !important; }

/* Dialog */
[data-theme="dark"] .el-dialog {
  background: #1e293b !important;
  border: 1px solid rgba(255,255,255,0.08);
}
[data-theme="dark"] .el-dialog__title { color: #e2e8f0 !important; }

/* Pagination */
[data-theme="dark"] .el-pagination button,
[data-theme="dark"] .el-pager li {
  background: rgba(30,41,59,0.6) !important;
  color: #94a3b8 !important;
}
[data-theme="dark"] .el-pager li.active { background: #409EFF !important; color: #fff !important; }

/* Page header */
[data-theme="dark"] .page-header .page-title { color: #e2e8f0 !important; }
</style>
