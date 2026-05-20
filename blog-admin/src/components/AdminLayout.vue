<template>
  <div class="layout-root">
    <!-- Animated background -->
    <div class="bg-layer">
      <div class="bg-grid"></div>
      <div class="bg-glow bg-glow-1"></div>
      <div class="bg-glow bg-glow-2"></div>
      <canvas ref="particleCanvas" class="bg-particles"></canvas>
    </div>

    <el-container class="layout">
      <el-aside width="230px" class="sidebar">
        <div class="sidebar-inner">
          <div class="logo">
            <router-link to="/" class="logo-link">
              <span class="logo-bracket">&lt;</span>
              <span class="logo-text">B2026</span>
              <span class="logo-bracket">/&gt;</span>
            </router-link>
            <div class="logo-line"></div>
          </div>

          <el-menu :default-active="route.path" router class="menu">
            <el-menu-item index="/">
              <template #title>
                <span class="menu-icon">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
                </span>
                <span class="menu-num">01</span>
                <span class="menu-label">仪表盘</span>
                <span class="menu-indicator"></span>
              </template>
            </el-menu-item>
            <el-menu-item index="/articles">
              <template #title>
                <span class="menu-icon">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                </span>
                <span class="menu-num">02</span>
                <span class="menu-label">文章管理</span>
                <span class="menu-indicator"></span>
              </template>
            </el-menu-item>
            <el-menu-item index="/categories">
              <template #title>
                <span class="menu-icon">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>
                </span>
                <span class="menu-num">03</span>
                <span class="menu-label">分类管理</span>
                <span class="menu-indicator"></span>
              </template>
            </el-menu-item>
            <el-menu-item index="/tags">
              <template #title>
                <span class="menu-icon">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg>
                </span>
                <span class="menu-num">04</span>
                <span class="menu-label">标签管理</span>
                <span class="menu-indicator"></span>
              </template>
            </el-menu-item>
            <el-menu-item index="/comments">
              <template #title>
                <span class="menu-icon">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                </span>
                <span class="menu-num">05</span>
                <span class="menu-label">留言管理</span>
                <span class="menu-indicator"></span>
              </template>
            </el-menu-item>
            <el-menu-item index="/settings">
              <template #title>
                <span class="menu-icon">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
                </span>
                <span class="menu-num">06</span>
                <span class="menu-label">个人设置</span>
                <span class="menu-indicator"></span>
              </template>
            </el-menu-item>
          </el-menu>

          <div class="sidebar-footer">
            <div class="user-row">
              <div class="user-avatar">
                <span class="avatar-inner"></span>
              </div>
              <div class="user-meta">
                <span class="user-name">{{ user.nickname || user.username || 'Admin' }}</span>
                <span class="user-role">admin</span>
              </div>
              <div class="status-dot" title="在线"></div>
            </div>
          </div>
        </div>
      </el-aside>

      <el-container class="right-area">
        <el-header class="topbar">
          <div class="topbar-left">
            <span class="topbar-path">~/{{ pageTitle }}</span>
            <span class="topbar-cursor">_</span>
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
          <div class="page-enter">
            <router-view />
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserInfo } from '../api/index.js'

const route = useRoute()
const router = useRouter()
const user = ref({})
const particleCanvas = ref(null)

const pageTitle = computed(() => {
  const map = {
    '/': 'dashboard',
    '/articles': 'articles',
    '/articles/create': 'articles/new',
    '/categories': 'categories',
    '/tags': 'tags',
    '/comments': 'comments',
    '/settings': 'settings',
  }
  if (route.path.startsWith('/articles/') && route.path.endsWith('/edit')) return 'articles/edit'
  return map[route.path] || ''
})

// Particle animation
let animId = null
function initParticles() {
  const canvas = particleCanvas.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  const dpr = window.devicePixelRatio || 1
  const rect = canvas.parentElement.getBoundingClientRect()

  canvas.width = rect.width * dpr
  canvas.height = rect.height * dpr
  canvas.style.width = rect.width + 'px'
  canvas.style.height = rect.height + 'px'
  ctx.scale(dpr, dpr)

  const particles = Array.from({ length: 40 }, () => ({
    x: Math.random() * rect.width,
    y: Math.random() * rect.height,
    vx: (Math.random() - 0.5) * 0.4,
    vy: (Math.random() - 0.5) * 0.4,
    r: Math.random() * 1.2 + 0.3,
    alpha: Math.random() * 0.5 + 0.1,
  }))

  function draw() {
    ctx.clearRect(0, 0, rect.width, rect.height)
    particles.forEach((p, i) => {
      p.x += p.vx
      p.y += p.vy
      if (p.x < 0) p.x = rect.width
      if (p.x > rect.width) p.x = 0
      if (p.y < 0) p.y = rect.height
      if (p.y > rect.height) p.y = 0

      ctx.beginPath()
      ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(0,212,170,${p.alpha})`
      ctx.fill()

      // Connections
      for (let j = i + 1; j < particles.length; j++) {
        const dx = p.x - particles[j].x
        const dy = p.y - particles[j].y
        const dist = Math.sqrt(dx * dx + dy * dy)
        if (dist < 120) {
          ctx.beginPath()
          ctx.moveTo(p.x, p.y)
          ctx.lineTo(particles[j].x, particles[j].y)
          ctx.strokeStyle = `rgba(0,212,170,${0.04 * (1 - dist / 120)})`
          ctx.stroke()
        }
      }
    })
    animId = requestAnimationFrame(draw)
  }
  draw()
}

function handleResize() {
  if (animId) cancelAnimationFrame(animId)
  initParticles()
}

onMounted(async () => {
  try {
    const res = await getUserInfo()
    user.value = res.data.data
  } catch { logout() }
  initParticles()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (animId) cancelAnimationFrame(animId)
  window.removeEventListener('resize', handleResize)
})

function logout() {
  localStorage.removeItem('blog-token')
  router.push('/login')
}
</script>

<style scoped>
/* ═══ Root ═══ */
.layout-root {
  height: 100vh; overflow: hidden; position: relative;
  background: #060b14;
}

/* ═══ Background ═══ */
.bg-layer { position: absolute; inset: 0; pointer-events: none; z-index: 0; }
.bg-grid {
  position: absolute; inset: 0;
  background-image:
    linear-gradient(rgba(255,255,255,0.012) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.012) 1px, transparent 1px);
  background-size: 64px 64px;
  mask-image: radial-gradient(ellipse at 30% 50%, black 40%, transparent 70%);
}
.bg-glow {
  position: absolute; border-radius: 50%; filter: blur(120px); opacity: 0.08;
}
.bg-glow-1 { width: 600px; height: 600px; background: #00d4aa; top: -200px; left: -100px; }
.bg-glow-2 { width: 400px; height: 400px; background: #7c3aed; bottom: -100px; right: -80px; }
.bg-particles { position: absolute; inset: 0; }

/* ═══ Layout ═══ */
.layout { height: 100vh; position: relative; z-index: 1; }

/* ═══ Sidebar ═══ */
.sidebar {
  width: 230px !important;
  position: relative;
}
.sidebar-inner {
  height: 100%; display: flex; flex-direction: column;
  background: rgba(9,14,22,0.92);
  border-right: 1px solid rgba(255,255,255,0.05);
  backdrop-filter: blur(24px);
  position: relative;
}
.sidebar-inner::after {
  content: '';
  position: absolute; top: 0; right: 0; bottom: 0; width: 1px;
  background: linear-gradient(180deg, transparent, rgba(0,212,170,0.3), transparent);
}

/* Logo */
.logo { padding: 24px 20px 18px; position: relative; }
.logo-link {
  font-family: 'JetBrains Mono', monospace; font-size: 19px; font-weight: 700;
  color: #e8eaed; text-decoration: none; letter-spacing: 2px;
  display: flex; align-items: center; justify-content: center; gap: 2px;
  transition: all 0.3s;
}
.logo-link:hover { text-shadow: 0 0 20px rgba(0,212,170,0.5); }
.logo-bracket { color: #00d4aa; font-size: 17px; }
.logo-text { background: linear-gradient(135deg, #e8eaed 0%, #00d4aa 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
.logo-line {
  width: 40px; height: 1px; margin: 14px auto 0;
  background: linear-gradient(90deg, transparent, rgba(0,212,170,0.4), transparent);
}

/* Menu */
.menu { flex: 1; border-right: none !important; padding: 8px 0; }
.menu :deep(.el-menu-item) {
  font-family: 'JetBrains Mono', monospace; font-size: 12px; color: #6b7280;
  margin: 2px 10px; border-radius: 8px; padding: 0 18px !important; height: 42px;
  transition: all 0.25s; position: relative; overflow: visible;
  display: flex; align-items: center; gap: 8px;
}
.menu :deep(.el-menu-item):hover {
  background: rgba(0,212,170,0.06); color: #b0b8c4;
  border-left: 2px solid rgba(0,212,170,0.3);
}
.menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(0,212,170,0.12) 0%, rgba(0,212,170,0.02) 100%) !important;
  color: #00d4aa !important;
  border-left: 2px solid #00d4aa;
  box-shadow: 0 0 16px rgba(0,212,170,0.06);
}
.menu-icon { display: flex; align-items: center; opacity: 0.5; transition: opacity 0.2s; flex-shrink: 0; }
.menu :deep(.el-menu-item.is-active) .menu-icon { opacity: 1; color: #00d4aa; }
.menu :deep(.el-menu-item):hover .menu-icon { opacity: 0.8; }
.menu-num { font-size: 9px; color: #4a5060; min-width: 14px; transition: color 0.2s; }
.menu :deep(.el-menu-item.is-active) .menu-num { color: #00d4aa; }
.menu-label { flex: 1; }
.menu-indicator {
  width: 4px; height: 4px; border-radius: 50%; background: transparent; transition: all 0.3s;
}
.menu :deep(.el-menu-item.is-active) .menu-indicator {
  background: #00d4aa; box-shadow: 0 0 8px #00d4aa, 0 0 16px rgba(0,212,170,0.5);
  animation: neonPulse 2s ease-in-out infinite;
}

/* Footer */
.sidebar-footer { padding: 16px 16px; border-top: 1px solid rgba(255,255,255,0.05); }
.user-row { display: flex; align-items: center; gap: 10px; }
.user-avatar {
  width: 34px; height: 34px; border-radius: 10px;
  background: linear-gradient(135deg, rgba(0,212,170,0.2), rgba(124,58,237,0.2));
  border: 1px solid rgba(0,212,170,0.3);
  display: flex; align-items: center; justify-content: center;
  position: relative; overflow: hidden;
}
.user-avatar::before {
  content: '';
  position: absolute; inset: 0;
  background: linear-gradient(135deg, transparent 40%, rgba(0,212,170,0.15) 50%, transparent 60%);
  animation: borderSweep 3s linear infinite;
  background-size: 200% 100%;
}
.avatar-inner {
  width: 14px; height: 14px; border-radius: 50%;
  background: rgba(0,212,170,0.6);
  box-shadow: 0 0 6px rgba(0,212,170,0.4);
}
.user-meta { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.user-name { font-family: 'JetBrains Mono', monospace; font-size: 12px; color: #e8eaed; font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.user-role { font-size: 10px; color: #555d6b; font-family: 'JetBrains Mono', monospace; text-transform: uppercase; letter-spacing: 1px; }
.status-dot {
  width: 7px; height: 7px; border-radius: 50%; background: #00d4aa;
  box-shadow: 0 0 6px #00d4aa, 0 0 12px rgba(0,212,170,0.4);
  animation: neonPulse 2s ease-in-out infinite;
  flex-shrink: 0;
}

/* ═══ Right Area ═══ */
.right-area { flex-direction: column; min-width: 0; }

/* Topbar */
.topbar {
  background: rgba(9,14,22,0.85); border-bottom: 1px solid rgba(255,255,255,0.05);
  display: flex; align-items: center; justify-content: space-between;
  height: 56px; padding: 0 28px; backdrop-filter: blur(20px);
  position: sticky; top: 0; z-index: 10; flex-shrink: 0;
}
.topbar-left { display: flex; align-items: center; gap: 2px; }
.topbar-path { font-family: 'JetBrains Mono', monospace; font-size: 13px; color: #00d4aa; }
.topbar-cursor {
  font-family: 'JetBrains Mono', monospace; font-size: 14px; color: #00d4aa;
  animation: neonPulse 1.2s step-end infinite;
}
.topbar-actions { display: flex; align-items: center; gap: 8px; }
.action-btn {
  display: flex; align-items: center; gap: 6px;
  color: #8b949e; font-size: 12px; text-decoration: none;
  font-family: 'JetBrains Mono', monospace; padding: 6px 12px;
  border-radius: 6px; border: 1px solid rgba(255,255,255,0.06);
  transition: all 0.2s;
}
.action-btn:hover { color: #00d4aa; border-color: rgba(0,212,170,0.3); background: rgba(0,212,170,0.06); }
.logout-btn {
  display: flex; align-items: center; padding: 6px 10px;
  background: none; border: 1px solid transparent; border-radius: 6px;
  color: #6b7280; cursor: pointer; transition: all 0.2s;
}
.logout-btn:hover { color: #ff4757; border-color: rgba(255,71,87,0.3); background: rgba(255,71,87,0.06); }

/* Main */
.main-area {
  background: transparent;
  padding: 28px;
  overflow-y: auto;
  position: relative;
}
.page-enter {
  animation: floatUp 0.4s ease;
}

/* ═══ Keyframes ═══ */
@keyframes neonPulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}
@keyframes floatUp {
  0% { opacity: 0; transform: translateY(12px); }
  100% { opacity: 1; transform: translateY(0); }
}
@keyframes borderSweep {
  0% { background-position: 0% 0%; }
  100% { background-position: 200% 0%; }
}
</style>
