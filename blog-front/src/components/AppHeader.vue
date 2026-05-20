<template>
  <header class="header">
    <div class="header-inner">
      <router-link to="/" class="logo">
        <span class="logo-bracket">&lt;</span>
        Blog2026
        <span class="logo-bracket">/&gt;</span>
      </router-link>
      <nav class="nav">
        <router-link to="/"><span class="nav-num">01</span> 首页</router-link>
        <router-link to="/categories"><span class="nav-num">02</span> 分类</router-link>
        <router-link to="/search"><span class="nav-num">03</span> 搜索</router-link>
        <router-link to="/about"><span class="nav-num">04</span> 关于</router-link>
      </nav>
    </div>
    <div class="header-glow"></div>
  </header>
</template>

<style scoped>
.header {
  position: sticky; top: 0; z-index: 100;
  background: rgba(10,14,23,0.85);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255,255,255,0.06);
}
.header-glow {
  position: absolute; bottom: 0; left: 0; right: 0; height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0,212,170,0.3), rgba(124,58,237,0.3), transparent);
}
.header-inner {
  max-width: 860px; margin: 0 auto; padding: 0 20px;
  display: flex; justify-content: space-between; align-items: center; height: 60px;
}
.logo {
  font-family: 'JetBrains Mono', monospace;
  font-size: 18px; font-weight: 700; color: #e8eaed; text-decoration: none; letter-spacing: 0.5px;
  transition: color 0.3s;
}
.logo:hover { color: #00d4aa; }
.logo-bracket { color: #00d4aa; font-size: 16px; }
.nav { display: flex; gap: 28px; }
.nav a {
  color: #8b949e; text-decoration: none; font-size: 14px; font-family: 'JetBrains Mono', monospace;
  transition: color 0.2s; position: relative; letter-spacing: 0.5px;
}
.nav a:hover, .nav a.router-link-active {
  color: #00d4aa;
}
.nav a::after {
  content: ''; position: absolute; bottom: -20px; left: 50%; transform: translateX(-50%);
  width: 0; height: 2px; background: #00d4aa; transition: width 0.3s;
  box-shadow: 0 0 8px rgba(0,212,170,0.4);
}
.nav a:hover::after, .nav a.router-link-active::after { width: 100%; }
.nav-num { color: #555d6b; font-size: 11px; margin-right: 2px; }
</style>
