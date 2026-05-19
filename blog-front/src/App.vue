<template>
  <n-config-provider :theme-overrides="themeOverrides">
    <n-loading-bar-provider>
      <div class="app-container">
        <AppHeader />
        <main class="main-content">
          <router-view />
        </main>
        <AppFooter />
      </div>
    </n-loading-bar-provider>
  </n-config-provider>
</template>

<script setup>
import AppHeader from './components/AppHeader.vue'
import AppFooter from './components/AppFooter.vue'

const themeOverrides = {
  common: {
    primaryColor: '#5b8c5a',
    primaryColorHover: '#6fa86d',
    fontFamily: '"Noto Serif SC", "Source Han Serif SC", Georgia, serif'
  }
}
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { background: #f8f7f4; color: #333; }
.app-container { min-height: 100vh; display: flex; flex-direction: column; }
.main-content { flex: 1; max-width: 860px; width: 100%; margin: 0 auto; padding: 24px 20px; }
</style>
