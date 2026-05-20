<template>
  <div class="admin-app">
    <div class="bg-grid"></div>
    <router-view />
  </div>
</template>

<style>
.admin-app { min-height: 100vh; position: relative; }
.bg-grid {
  position: fixed; inset: 0; z-index: 0; pointer-events: none;
  background-image:
    linear-gradient(rgba(255,255,255,0.015) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.015) 1px, transparent 1px);
  background-size: 60px 60px;
}
</style>
