<template>
  <header class="header" :class="{ scrolled }">
    <div class="header-inner">
      <router-link to="/" class="logo">
        <span class="logo-bracket">&lt;</span>
        <span class="logo-text">B2026</span>
        <span class="logo-bracket">/&gt;</span>
      </router-link>
      <nav class="nav">
        <router-link to="/" class="nav-link">
          <span class="nav-num">01</span>
          <span class="nav-label">首页</span>
          <span class="nav-indicator"></span>
        </router-link>
        <router-link to="/categories" class="nav-link">
          <span class="nav-num">02</span>
          <span class="nav-label">分类</span>
          <span class="nav-indicator"></span>
        </router-link>
        <router-link to="/search" class="nav-link">
          <span class="nav-num">03</span>
          <span class="nav-label">搜索</span>
          <span class="nav-indicator"></span>
        </router-link>
        <router-link to="/about" class="nav-link">
          <span class="nav-num">04</span>
          <span class="nav-label">关于</span>
          <span class="nav-indicator"></span>
        </router-link>
      </nav>
    </div>
    <div class="header-glow-line"></div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
const scrolled = ref(false)
if (typeof window !== 'undefined') {
  window.addEventListener('scroll', () => { scrolled.value = window.scrollY > 10 })
}
</script>

<style scoped>
.header {
  position: sticky; top: 0; z-index: 100;
  background: rgba(18,27,40,0.88);
  backdrop-filter: blur(22px) saturate(1.2);
  border-bottom: 1px solid rgba(255,255,255,0.05);
  transition: background 0.3s, border-color 0.3s;
}
.header.scrolled {
  background: rgba(18,27,40,0.95);
  border-bottom-color: rgba(255,255,255,0.08);
}
.header-glow-line {
  position: absolute; bottom: 0; left: 0; right: 0; height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0,212,170,0.35), rgba(124,58,237,0.25), transparent);
}
.header-inner {
  max-width: 860px; margin: 0 auto; padding: 0 20px;
  display: flex; justify-content: space-between; align-items: center; height: 62px;
}

/* Logo */
.logo {
  font-family: 'JetBrains Mono', monospace; font-size: 18px; font-weight: 700;
  color: #e8eaed; text-decoration: none; letter-spacing: 1px;
  display: flex; align-items: center; gap: 2px; transition: all 0.3s;
}
.logo:hover { text-shadow: 0 0 16px rgba(0,212,170,0.4); }
.logo-bracket { color: #00d4aa; font-size: 16px; }
.logo-text {
  background: linear-gradient(135deg, #e8eaed 0%, #00d4aa 100%);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
}

/* Nav */
.nav { display: flex; gap: 6px; }
.nav-link {
  display: flex; align-items: center; gap: 5px; padding: 8px 14px;
  border-radius: 8px; text-decoration: none; position: relative;
  transition: all 0.25s;
}
.nav-link:hover { background: rgba(0,212,170,0.05); }
.nav-link.router-link-active { background: rgba(0,212,170,0.1); }

.nav-num { font-family: 'JetBrains Mono', monospace; font-size: 10px; color: #5a6070; transition: color 0.2s; }
.nav-label { font-family: 'JetBrains Mono', monospace; font-size: 13px; color: #8b949e; transition: color 0.2s; }
.nav-link:hover .nav-label { color: #b0b8c4; }
.nav-link.router-link-active .nav-label { color: #00d4aa; }
.nav-link.router-link-active .nav-num { color: #00d4aa; }

.nav-indicator {
  width: 4px; height: 4px; border-radius: 50%; background: transparent;
  transition: all 0.3s;
}
.nav-link.router-link-active .nav-indicator {
  background: #00d4aa;
  box-shadow: 0 0 8px #00d4aa, 0 0 16px rgba(0,212,170,0.4);
  animation: indicatorPulse 2s ease-in-out infinite;
}

@keyframes indicatorPulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(0.7); }
}
</style>
