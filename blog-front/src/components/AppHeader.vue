<template>
  <header class="header">
    <div class="header-inner">
      <router-link to="/" class="logo">Blog2026</router-link>
      <nav class="nav">
        <router-link to="/">首页</router-link>
        <router-link to="/categories">分类</router-link>
        <router-link to="/search">搜索</router-link>
        <router-link to="/about">关于</router-link>
      </nav>
    </div>
  </header>
</template>

<style scoped>
.header {
  background: #fff;
  border-bottom: 1px solid #eee;
  position: sticky; top: 0; z-index: 100;
}
.header-inner {
  max-width: 860px; margin: 0 auto; padding: 0 20px;
  display: flex; justify-content: space-between; align-items: center; height: 56px;
}
.logo {
  font-size: 20px; font-weight: 700; color: #5b8c5a; text-decoration: none; letter-spacing: 1px;
}
.nav { display: flex; gap: 24px; }
.nav a { color: #666; text-decoration: none; font-size: 15px; transition: color .2s; }
.nav a:hover, .nav a.router-link-active { color: #5b8c5a; }
</style>
