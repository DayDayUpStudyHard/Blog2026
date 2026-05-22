<template>
  <header class="header" :class="{ scrolled }">
    <div class="header-inner">
      <router-link to="/" class="logo">
        <span class="logo-icon">B</span>
        <span class="logo-text">Blog2026</span>
      </router-link>
      <nav class="nav">
        <router-link to="/" class="nav-link">
          <span class="nav-label">首页</span>
        </router-link>
        <router-link to="/categories" class="nav-link">
          <span class="nav-label">分类</span>
        </router-link>
        <router-link to="/search" class="nav-link">
          <span class="nav-label">搜索</span>
        </router-link>
        <router-link to="/about" class="nav-link">
          <span class="nav-label">关于</span>
        </router-link>
      </nav>
    </div>
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
  background: rgba(255,255,255,0.72);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255,255,255,0.6);
  transition: box-shadow 0.3s;
}
.header.scrolled {
  box-shadow: 0 1px 2px rgba(0,0,0,0.04), 0 4px 16px rgba(0,0,0,0.06);
}
.header-inner {
  max-width: 860px; margin: 0 auto; padding: 0 20px;
  display: flex; justify-content: space-between; align-items: center; height: 60px;
}

/* Logo */
.logo {
  display: flex; align-items: center; gap: 8px;
  text-decoration: none;
}
.logo-icon {
  width: 30px; height: 30px; border-radius: 8px;
  background: linear-gradient(135deg, #409EFF, #66b1ff);
  color: #fff; font-size: 15px; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
}
.logo-text { font-size: 17px; font-weight: 700; color: #303133; }

/* Nav */
.nav { display: flex; gap: 4px; }
.nav-link {
  padding: 6px 14px; border-radius: 6px; text-decoration: none;
  font-size: 14px; color: #606266; transition: all 0.15s;
}
.nav-link:hover {
  background: #f5f7fa; color: #409EFF; transform: translateY(-1px);
}
.nav-link.router-link-active {
  background: #ecf5ff; color: #409EFF; font-weight: 500;
  box-shadow: 0 1px 3px rgba(64,158,255,0.12);
}
</style>
