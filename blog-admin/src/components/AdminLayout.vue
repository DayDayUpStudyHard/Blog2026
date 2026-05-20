<template>
  <el-container class="layout">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <router-link to="/" class="logo-link">
          <span class="logo-bracket">&lt;</span>
          B2026
          <span class="logo-bracket">/&gt;</span>
        </router-link>
      </div>
      <el-menu :default-active="route.path" router class="menu">
        <el-menu-item index="/">
          <template #title>
            <span class="menu-num">01</span> 仪表盘
          </template>
        </el-menu-item>
        <el-menu-item index="/articles">
          <template #title>
            <span class="menu-num">02</span> 文章管理
          </template>
        </el-menu-item>
        <el-menu-item index="/categories">
          <template #title>
            <span class="menu-num">03</span> 分类管理
          </template>
        </el-menu-item>
        <el-menu-item index="/tags">
          <template #title>
            <span class="menu-num">04</span> 标签管理
          </template>
        </el-menu-item>
        <el-menu-item index="/comments">
          <template #title>
            <span class="menu-num">05</span> 留言管理
          </template>
        </el-menu-item>
        <el-menu-item index="/settings">
          <template #title>
            <span class="menu-num">06</span> 个人设置
          </template>
        </el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <div class="user-info">
          <span class="user-dot"></span>
          {{ user.nickname || user.username }}
        </div>
      </div>
    </el-aside>
    <el-container class="right-area">
      <el-header class="topbar">
        <div class="topbar-title">{{ pageTitle }}</div>
        <div class="topbar-actions">
          <a href="/" target="_blank" class="action-link">查看博客</a>
          <el-button text @click="logout" class="logout-btn">退出</el-button>
        </div>
      </el-header>
      <el-main class="main-area"><router-view /></el-main>
    </el-container>
  </el-container>
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
    '/categories': '分类管理',
    '/tags': '标签管理',
    '/comments': '留言管理',
    '/settings': '个人设置',
  }
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
.layout { height: 100vh; position: relative; z-index: 1; }

.sidebar {
  background: rgba(13,17,26,0.95); border-right: 1px solid rgba(255,255,255,0.06);
  display: flex; flex-direction: column; overflow: hidden;
  backdrop-filter: blur(20px);
}

.logo { padding: 22px 20px 18px; border-bottom: 1px solid rgba(255,255,255,0.06); text-align: center; }
.logo-link {
  font-family: 'JetBrains Mono', monospace; font-size: 18px; font-weight: 700;
  color: #e8eaed; text-decoration: none; letter-spacing: 1px;
}
.logo-bracket { color: #00d4aa; font-size: 16px; }

.menu { flex: 1; border-right: none !important; padding-top: 8px; }
.menu .el-menu-item {
  font-family: 'JetBrains Mono', monospace; font-size: 13px; color: #8b949e;
  margin: 2px 10px; border-radius: 6px; padding-left: 20px !important;
  transition: all 0.2s;
}
.menu .el-menu-item.is-active {
  background: rgba(0,212,170,0.1) !important; color: #00d4aa !important;
  border-left: 2px solid #00d4aa;
}
.menu .el-menu-item:hover { background: rgba(0,212,170,0.05) !important; }
.menu-num { font-size: 10px; color: #555d6b; margin-right: 8px; }

.sidebar-footer { padding: 16px 20px; border-top: 1px solid rgba(255,255,255,0.06); }
.user-info { font-family: 'JetBrains Mono', monospace; font-size: 13px; color: #8b949e; display: flex; align-items: center; gap: 8px; }
.user-dot { width: 8px; height: 8px; border-radius: 50%; background: #00d4aa; box-shadow: 0 0 8px rgba(0,212,170,0.5); }

.right-area { flex-direction: column; }

.topbar {
  background: rgba(13,17,26,0.9); border-bottom: 1px solid rgba(255,255,255,0.06);
  display: flex; align-items: center; justify-content: space-between;
  height: 56px; padding: 0 24px; backdrop-filter: blur(20px);
  position: sticky; top: 0; z-index: 10;
}
.topbar-title { font-family: 'JetBrains Mono', monospace; font-size: 14px; color: #e8eaed; font-weight: 500; }
.topbar-actions { display: flex; align-items: center; gap: 12px; }
.action-link { color: #8b949e; font-size: 13px; text-decoration: none; font-family: 'JetBrains Mono', monospace; transition: color 0.2s; }
.action-link:hover { color: #00d4aa; }
.logout-btn { color: #8b949e !important; font-family: 'JetBrains Mono', monospace; font-size: 12px; }

.main-area { background: #0a0e17; padding: 24px; min-height: 0; }
</style>
