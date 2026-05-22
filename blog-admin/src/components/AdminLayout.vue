<template>
  <div class="layout-root">
    <el-container class="layout">
      <el-aside width="220px" class="sidebar">
        <div class="sidebar-inner">
          <div class="logo">
            <router-link to="/" class="logo-link">
              <span class="logo-icon">B</span>
              <span class="logo-text">Blog2026</span>
            </router-link>
          </div>

          <el-menu :default-active="route.path" router class="menu">
            <el-menu-item index="/">
              <template #title>
                <span class="menu-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
                </span>
                <span class="menu-label">仪表盘</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/articles">
              <template #title>
                <span class="menu-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                </span>
                <span class="menu-label">文章管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/categories">
              <template #title>
                <span class="menu-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>
                </span>
                <span class="menu-label">分类管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/tags">
              <template #title>
                <span class="menu-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg>
                </span>
                <span class="menu-label">标签管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/comments">
              <template #title>
                <span class="menu-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                </span>
                <span class="menu-label">留言管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/settings">
              <template #title>
                <span class="menu-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
                </span>
                <span class="menu-label">个人设置</span>
              </template>
            </el-menu-item>
          </el-menu>

          <div class="sidebar-footer">
            <div class="user-row">
              <div class="user-avatar">
                <span>{{ (user.nickname || user.username || 'A').charAt(0) }}</span>
              </div>
              <div class="user-meta">
                <span class="user-name">{{ user.nickname || user.username || 'Admin' }}</span>
                <span class="user-role">管理员</span>
              </div>
              <div class="status-dot" title="在线"></div>
            </div>
          </div>
        </div>
      </el-aside>

      <el-container class="right-area">
        <el-header class="topbar">
          <div class="topbar-left">
            <span class="topbar-path">{{ pageTitle }}</span>
          </div>
          <div class="topbar-actions">
            <a href="/" target="_blank" class="action-btn" title="查看博客">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/><polyline points="15 3 21 3 21 9"/><line x1="10" y1="14" x2="21" y2="3"/></svg>
              <span>查看博客</span>
            </a>
            <button class="logout-btn" @click="logout" title="退出登录">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
            </button>
          </div>
        </el-header>
        <el-main class="main-area">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserInfo } from '../api/index.js'

const route = useRoute()
const router = useRouter()
const user = ref({})

const pageTitle = computed(() => {
  const map = {
    '/': '仪表盘',
    '/articles': '文章管理',
    '/articles/create': '新建文章',
    '/categories': '分类管理',
    '/tags': '标签管理',
    '/comments': '留言管理',
    '/settings': '个人设置',
  }
  if (route.path.startsWith('/articles/') && route.path.endsWith('/edit')) return '编辑文章'
  return map[route.path] || ''
})

onMounted(async () => {
  try {
    const res = await getUserInfo()
    user.value = res.data.data
  } catch { logout() }
})

function logout() {
  localStorage.removeItem('blog-token')
  router.push('/login')
}
</script>

<style scoped>
/* ═══ Root ═══ */
.layout-root {
  height: 100vh; overflow: hidden;
}

/* ═══ Layout ═══ */
.layout { height: 100vh; }

/* ═══ Sidebar ═══ */
.sidebar {
  width: 220px !important;
  position: relative;
}
.sidebar-inner {
  height: 100%; display: flex; flex-direction: column;
  background: rgba(255,255,255,0.75);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-right: 1px solid rgba(255,255,255,0.5);
}

/* Logo */
.logo { padding: 24px 20px 20px; }
.logo-link {
  display: flex; align-items: center; gap: 8px;
  text-decoration: none;
}
.logo-icon {
  width: 32px; height: 32px; border-radius: 8px;
  background: linear-gradient(135deg, #409EFF, #66b1ff);
  color: #fff; font-size: 16px; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
}
.logo-text { font-size: 17px; font-weight: 700; color: #303133; letter-spacing: -0.3px; }

/* Menu */
.menu { flex: 1; border-right: none !important; padding: 4px 0; }
.menu :deep(.el-menu-item) {
  font-size: 13px; color: #606266;
  margin: 1px 8px; border-radius: 8px; padding: 0 16px !important; height: 40px;
  transition: all 0.15s; display: flex; align-items: center; gap: 8px;
}
.menu :deep(.el-menu-item):hover {
  background: #f0f7ff; color: #409EFF; transform: translateX(2px);
}
.menu :deep(.el-menu-item.is-active) {
  background: #ecf5ff !important;
  color: #409EFF !important;
  font-weight: 600;
  box-shadow: 0 1px 3px rgba(64,158,255,0.12);
}
.menu-icon { display: flex; align-items: center; opacity: 0.55; flex-shrink: 0; }
.menu :deep(.el-menu-item.is-active) .menu-icon { opacity: 1; }
.menu :deep(.el-menu-item):hover .menu-icon { opacity: 0.85; }
.menu-label { flex: 1; }

/* Footer */
.sidebar-footer { padding: 14px 16px; border-top: 1px solid #f0f2f5; }
.user-row { display: flex; align-items: center; gap: 10px; }
.user-avatar {
  width: 34px; height: 34px; border-radius: 8px;
  background: linear-gradient(135deg, #ecf5ff, #d9ecff); color: #409EFF;
  display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 600;
}
.user-meta { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.user-name { font-size: 13px; color: #303133; font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.user-role { font-size: 11px; color: #909399; }
.status-dot {
  width: 7px; height: 7px; border-radius: 50%; background: #67c23a;
  flex-shrink: 0;
}

/* ═══ Right Area ═══ */
.right-area { flex-direction: column; min-width: 0; }

/* Topbar */
.topbar {
  background: rgba(255,255,255,0.75);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255,255,255,0.5);
  display: flex; align-items: center; justify-content: space-between;
  height: 56px; padding: 0 24px;
  position: sticky; top: 0; z-index: 10; flex-shrink: 0;
}
.topbar-left { display: flex; align-items: center; }
.topbar-path { font-size: 14px; color: #303133; font-weight: 500; }
.topbar-actions { display: flex; align-items: center; gap: 8px; }
.action-btn {
  display: flex; align-items: center; gap: 5px;
  color: #606266; font-size: 12px; text-decoration: none;
  padding: 6px 12px; border-radius: 6px;
  border: 1px solid #e4e7ed; transition: all 0.2s;
}
.action-btn:hover { color: #409EFF; border-color: #409EFF; background: #f0f7ff; }
.logout-btn {
  display: flex; align-items: center; padding: 6px 10px;
  background: none; border: 1px solid transparent; border-radius: 6px;
  color: #909399; cursor: pointer; transition: all 0.2s;
}
.logout-btn:hover { color: #f56c6c; border-color: #fde2e2; background: #fef0f0; }

/* Main */
.main-area {
  padding: 24px;
  overflow-y: auto;
}
</style>
