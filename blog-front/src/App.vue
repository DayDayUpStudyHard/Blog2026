<template>
  <n-config-provider :theme-overrides="themeOverrides" :theme="null">
    <n-loading-bar-provider>
      <div class="app-root">
        <div class="bg-blobs">
          <div class="bg-blob blob-1"></div>
          <div class="bg-blob blob-2"></div>
          <div class="bg-blob blob-3"></div>
        </div>
        <div class="app-container">
          <AppHeader />
          <main class="main-content">
            <router-view v-slot="{ Component }">
              <transition name="page" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
          </main>
          <AppFooter />
          <ToolsWidget />
        </div>
        <button class="theme-toggle" @click="toggleTheme" :title="themeLabel">
          <svg v-if="currentTheme === 'light'" width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>
          <svg v-else width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/></svg>
        </button>
      </div>
    </n-loading-bar-provider>
  </n-config-provider>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import AppHeader from './components/AppHeader.vue'
import AppFooter from './components/AppFooter.vue'
import ToolsWidget from './components/ToolsWidget.vue'

const currentTheme = ref(localStorage.getItem('blog-theme') || 'light')
const themeLabel = computed(() => currentTheme.value === 'light' ? '切换暗色模式' : '切换亮色模式')

onMounted(() => {
  document.documentElement.setAttribute('data-theme', currentTheme.value)
})

function toggleTheme() {
  currentTheme.value = currentTheme.value === 'light' ? 'dark' : 'light'
  document.documentElement.setAttribute('data-theme', currentTheme.value)
  localStorage.setItem('blog-theme', currentTheme.value)
}

const themeOverrides = {
  common: {
    primaryColor: '#409EFF',
    primaryColorHover: '#66b1ff',
    primaryColorPressed: '#3a8ee6',
    primaryColorSuppl: '#409EFF',
    bodyColor: '#f0f2f5',
    cardColor: '#ffffff',
    modalColor: '#ffffff',
    popoverColor: '#ffffff',
    borderColor: '#e8ecf0',
    hoverColor: 'rgba(64,158,255,0.06)',
    fontFamily: "'Inter', system-ui, -apple-system, sans-serif",
    fontFamilyMono: "'JetBrains Mono', 'Fira Code', monospace",
    textColorBase: '#303133',
    textColor1: '#303133',
    textColor2: '#909399',
    textColor3: '#c0c4cc',
    inputColor: '#ffffff',
    inputColorFocus: '#ffffff',
    tagColor: 'rgba(64,158,255,0.08)',
    successColor: '#67c23a',
    successColorHover: '#85ce61',
    warningColor: '#e6a23c',
    errorColor: '#f56c6c',
  },
  Button: {
    textColor: '#409EFF',
    border: '1px solid #409EFF',
    borderHover: '1px solid #66b1ff',
    borderFocus: '1px solid #409EFF',
    borderPressed: '1px solid #3a8ee6',
    colorHover: 'rgba(64,158,255,0.06)',
    colorFocus: 'rgba(64,158,255,0.1)',
    colorPressed: 'rgba(64,158,255,0.14)',
  },
  Input: {
    border: '1px solid #dcdfe6',
    borderHover: '1px solid #c0c4cc',
    borderFocus: '1px solid #409EFF',
    boxShadowFocus: '0 0 0 3px rgba(64,158,255,0.12)',
    placeholderColor: '#c0c4cc',
  },
  Tag: {
    textColor: '#409EFF',
    colorBordered: 'transparent',
    border: '1px solid rgba(64,158,255,0.3)',
  },
  Pagination: {
    itemColor: '#ffffff',
    itemColorActive: '#409EFF',
    itemTextColor: '#606266',
    itemTextColorActive: '#ffffff',
    itemBorder: '1px solid #e4e7ed',
    itemBorderActive: '1px solid #409EFF',
  },
  LoadingBar: { colorLoading: '#409EFF' },
  Spin: { color: '#409EFF' },
}
</script>

<style>
/* ═══ Reset & Base ═══ */
* { margin: 0; padding: 0; box-sizing: border-box; }

body {
  background: linear-gradient(135deg, #f5f7fa 0%, #f0f2f5 50%, #e8ecf1 100%);
  background-attachment: fixed;
  color: #303133;
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
  -webkit-font-smoothing: antialiased;
  overflow-x: hidden;
}

::selection { background: rgba(64,158,255,0.2); color: #303133; }

::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: #d0d5dd; border-radius: 3px; }
::-webkit-scrollbar-thumb:hover { background: #b0b8c4; }

/* ═══ Background Blobs ═══ */
.bg-blobs { position: fixed; inset: 0; z-index: -1; pointer-events: none; overflow: hidden; }

.bg-blob {
  position: absolute; border-radius: 50%; filter: blur(100px); opacity: 0.35;
  will-change: transform;
}

.blob-1 {
  width: 480px; height: 480px;
  background: rgba(64,158,255,0.12);
  top: -8%; right: -4%;
  animation: blobFloat1 14s ease-in-out infinite;
}

.blob-2 {
  width: 400px; height: 400px;
  background: rgba(139,92,246,0.09);
  bottom: -6%; left: -2%;
  animation: blobFloat2 16s ease-in-out infinite;
  animation-delay: -2s;
}

.blob-3 {
  width: 350px; height: 350px;
  background: rgba(16,185,129,0.08);
  top: 50%; left: 40%;
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

/* ═══ App Shell ═══ */
.app-root { min-height: 100vh; position: relative; }
.app-container {
  min-height: 100vh; display: flex; flex-direction: column;
  position: relative; z-index: 1;
}

/* ═══ Main Content ═══ */
.main-content {
  flex: 1; max-width: 860px; width: 100%; margin: 0 auto; padding: 24px 20px;
}

/* ═══ Page Transitions ═══ */
.page-enter-active { transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
.page-leave-active { transition: all 0.15s ease; }
.page-enter-from { opacity: 0; transform: translateY(16px) scale(0.98); }
.page-leave-to { opacity: 0; transform: translateY(-8px); }

/* ═══ Utility Classes ═══ */
.glass-card {
  background: rgba(255,255,255,0.7);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255,255,255,0.5);
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.05);
}

/* ═══ Markdown Content ═══ */
.markdown-body { color: #4a5568; line-height: 1.85; font-size: 16px; }
.markdown-body h1 { font-size: 2em; margin: 0.67em 0; color: #1a202c; font-weight: 700; }
.markdown-body h2 {
  font-size: 1.5em; margin: 1.3em 0 0.6em; color: #409EFF;
  font-weight: 600;
  padding-bottom: 6px;
  background: linear-gradient(90deg, #409EFF, #10b981, transparent) left bottom no-repeat;
  background-size: 100% 2px;
}
.markdown-body h3 { font-size: 1.17em; margin: 1em 0 0.5em; color: #2d3748; }
.markdown-body p { margin-bottom: 0.9em; }
.markdown-body a {
  color: #409EFF; text-decoration: none; border-bottom: 1px dashed #b3d8ff;
  transition: all 0.2s;
}
.markdown-body a:hover { border-bottom-style: solid; color: #337ecc; }
.markdown-body strong { color: #1a202c; font-weight: 600; }
.markdown-body code {
  background: #f5f7fa; color: #409EFF; padding: 3px 8px; border-radius: 4px;
  font-family: 'JetBrains Mono', monospace; font-size: 0.88em; border: 1px solid #e8ecf0;
}
.markdown-body pre {
  background: #f8f9fb; border: 1px solid #e8ecf0; border-radius: 10px;
  padding: 20px; overflow-x: auto; margin: 16px 0;
}
.markdown-body pre code { background: none; border: none; padding: 0; color: #4a5568; font-size: 14px; line-height: 1.6; }
.markdown-body blockquote {
  border-left: 3px solid;
  border-image: linear-gradient(180deg, #409EFF, #8b5cf6) 1;
  padding: 12px 20px; margin: 16px 0;
  background: linear-gradient(135deg, #f8f9fb, #f5f7fa);
  border-radius: 0 8px 8px 0; color: #606266;
}
.markdown-body ul, .markdown-body ol { padding-left: 1.5em; margin-bottom: 0.9em; }
.markdown-body li { margin-bottom: 0.3em; }
.markdown-body img { max-width: 100%; border-radius: 8px; border: 1px solid #e8ecf0; }
.markdown-body hr { border: none; height: 1px; background: linear-gradient(90deg, transparent, #d0d5dd, transparent); margin: 2em 0; }
.markdown-body table { width: 100%; border-collapse: collapse; margin: 16px 0; }
.markdown-body th { background: #f5f7fa; padding: 10px 16px; text-align: left; color: #409EFF; border: 1px solid #e8ecf0; }
.markdown-body td { padding: 8px 16px; border: 1px solid #e8ecf0; }

/* ═══ Theme Toggle ═══ */
.theme-toggle {
  position: fixed; bottom: 32px; left: 32px; z-index: 100;
  width: 40px; height: 40px; border-radius: 50%;
  border: 1px solid rgba(0,0,0,0.08);
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  color: #909399; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06), 0 4px 16px rgba(0,0,0,0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.theme-toggle:hover {
  transform: translateY(-2px);
  color: #f59e0b;
  box-shadow: 0 4px 12px rgba(245,158,11,0.15), 0 8px 24px rgba(0,0,0,0.08);
}

/* ═══ Dark Mode ═══ */
[data-theme="dark"] body {
  background: #0f172a;
}

[data-theme="dark"] .bg-blob {
  filter: blur(100px); opacity: 0.22;
}
[data-theme="dark"] .blob-1 { background: rgba(64,158,255,0.16); }
[data-theme="dark"] .blob-2 { background: rgba(139,92,246,0.12); }
[data-theme="dark"] .blob-3 { background: rgba(16,185,129,0.09); }

[data-theme="dark"] .glass-card {
  background: rgba(30, 41, 59, 0.7);
  border-color: rgba(255,255,255,0.06);
}

/* Header */
[data-theme="dark"] .app-header {
  background: rgba(15, 23, 42, 0.8) !important;
  border-bottom-color: rgba(255,255,255,0.06) !important;
}
[data-theme="dark"] .header-links a { color: #94a3b8 !important; }
[data-theme="dark"] .header-links a:hover { color: #60a5fa !important; }
[data-theme="dark"] .header-links a.router-link-exact-active { color: #60a5fa !important; }

/* Cards */
[data-theme="dark"] .card-inner.hover {
  background: rgba(30, 41, 59, 0.75) !important;
  border-color: rgba(255,255,255,0.06) !important;
}
[data-theme="dark"] .title { color: #e2e8f0 !important; }
[data-theme="dark"] .card-inner.hover .title {
  background: linear-gradient(135deg, #60a5fa, #34d399);
  -webkit-background-clip: text;
  background-clip: text;
}
[data-theme="dark"] .summary { color: #94a3b8 !important; }
[data-theme="dark"] .meta { color: #64748b !important; }
[data-theme="dark"] .card:not(:last-child)::after {
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.06) 20%, rgba(255,255,255,0.06) 80%, transparent) !important;
}

/* Hero */
[data-theme="dark"] .hero-subtitle { color: #94a3b8 !important; }
[data-theme="dark"] .featured-card {
  background: rgba(30, 41, 59, 0.8) !important;
  border-color: rgba(255,255,255,0.06) !important;
}
[data-theme="dark"] .featured-title { color: #e2e8f0 !important; }
[data-theme="dark"] .featured-summary { color: #94a3b8 !important; }
[data-theme="dark"] .featured-cover.placeholder {
  background: linear-gradient(135deg, #1e293b, #1a1a2e, #1e293b) !important;
}

/* Article detail */
[data-theme="dark"] .article-title { color: #e2e8f0 !important; }
[data-theme="dark"] .meta-sep { color: #475569 !important; }
[data-theme="dark"] .article-footer { border-top-color: rgba(255,255,255,0.06) !important; }
[data-theme="dark"] .tag-item { background: rgba(64,158,255,0.1) !important; border-color: rgba(64,158,255,0.15) !important; }
[data-theme="dark"] .nav-link {
  background: rgba(30, 41, 59, 0.6) !important;
  border-color: rgba(255,255,255,0.06) !important;
}
[data-theme="dark"] .nav-link:hover { background: rgba(30, 41, 59, 0.85) !important; }
[data-theme="dark"] .nav-title { color: #e2e8f0 !important; }

/* Markdown content in dark mode */
[data-theme="dark"] .markdown-body { color: #cbd5e1 !important; }
[data-theme="dark"] .markdown-body h1 { color: #e2e8f0 !important; }
[data-theme="dark"] .markdown-body h3 { color: #e2e8f0 !important; }
[data-theme="dark"] .markdown-body strong { color: #e2e8f0 !important; }
[data-theme="dark"] .markdown-body code {
  background: rgba(30,41,59,0.6) !important; color: #60a5fa !important;
  border-color: rgba(255,255,255,0.08) !important;
}
[data-theme="dark"] .markdown-body pre {
  background: rgba(30,41,59,0.5) !important;
  border-color: rgba(255,255,255,0.08) !important;
}
[data-theme="dark"] .markdown-body pre code { color: #cbd5e1 !important; }
[data-theme="dark"] .markdown-body blockquote {
  background: rgba(30,41,59,0.5) !important;
  color: #94a3b8 !important;
}
[data-theme="dark"] .markdown-body th { background: rgba(30,41,59,0.6) !important; border-color: rgba(255,255,255,0.08) !important; }
[data-theme="dark"] .markdown-body td { border-color: rgba(255,255,255,0.08) !important; }

/* Theme toggle dark */
[data-theme="dark"] .theme-toggle {
  background: rgba(30, 41, 59, 0.8);
  border-color: rgba(255,255,255,0.08);
  color: #94a3b8;
}
[data-theme="dark"] .theme-toggle:hover { color: #fbbf24; }

/* BackToTop */
[data-theme="dark"] .back-to-top {
  background: rgba(30, 41, 59, 0.8);
}
[data-theme="dark"] .ring-bg { stroke: rgba(255,255,255,0.1); }

/* Footer */
[data-theme="dark"] .app-footer { color: #64748b !important; border-top-color: rgba(255,255,255,0.06) !important; }

/* Pagination */
[data-theme="dark"] .n-pagination .n-pagination-item {
  background: rgba(30,41,59,0.6) !important;
  color: #94a3b8 !important;
  border-color: rgba(255,255,255,0.08) !important;
}
[data-theme="dark"] .n-pagination .n-pagination-item--active {
  background: #409EFF !important; color: #fff !important;
}

/* Empty state */
[data-theme="dark"] .n-empty .n-empty__description { color: #64748b !important; }

/* Search box in header */
[data-theme="dark"] .search-box { background: rgba(255,255,255,0.06); }
[data-theme="dark"] .search-box:focus-within { background: rgba(30,41,59,0.9); }
[data-theme="dark"] .search-input { color: #e2e8f0; }
[data-theme="dark"] .search-input::placeholder { color: #64748b; }
[data-theme="dark"] .search-hint { color: #94a3b8; }
</style>
