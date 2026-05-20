<template>
  <n-config-provider :theme-overrides="themeOverrides" :theme="null">
    <n-loading-bar-provider>
      <div class="app-container">
        <div class="bg-grid"></div>
        <AppHeader />
        <main class="main-content">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
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
    primaryColor: '#00d4aa',
    primaryColorHover: '#00e5c0',
    primaryColorPressed: '#00b890',
    primaryColorSuppl: '#00d4aa',
    bodyColor: '#0a0e17',
    cardColor: '#131a2b',
    modalColor: '#131a2b',
    popoverColor: '#131a2b',
    borderColor: 'rgba(255,255,255,0.08)',
    hoverColor: 'rgba(0,212,170,0.08)',
    fontFamily: "'Inter', system-ui, -apple-system, sans-serif",
    fontFamilyMono: "'JetBrains Mono', 'Fira Code', monospace",
    textColorBase: '#e8eaed',
    textColor1: '#e8eaed',
    textColor2: '#8b949e',
    textColor3: '#555d6b',
    inputColor: '#131a2b',
    inputColorFocus: '#1a2740',
    tagColor: 'rgba(0,212,170,0.12)',
    successColor: '#00d4aa',
    successColorHover: '#00e5c0',
    warningColor: '#f0a020',
    errorColor: '#ff4757',
  },
  Button: {
    textColor: '#00d4aa',
    border: '1px solid rgba(0,212,170,0.4)',
    borderHover: '1px solid rgba(0,212,170,0.7)',
    borderFocus: '1px solid rgba(0,212,170,0.9)',
    borderPressed: '1px solid rgba(0,212,170,0.9)',
    colorHover: 'rgba(0,212,170,0.08)',
    colorFocus: 'rgba(0,212,170,0.12)',
    colorPressed: 'rgba(0,212,170,0.16)',
  },
  Input: {
    border: '1px solid rgba(255,255,255,0.1)',
    borderHover: '1px solid rgba(0,212,170,0.4)',
    borderFocus: '1px solid rgba(0,212,170,0.7)',
    boxShadowFocus: '0 0 0 3px rgba(0,212,170,0.15)',
    placeholderColor: '#555d6b',
  },
  Tag: {
    textColor: '#00d4aa',
    colorBordered: 'transparent',
    border: '1px solid rgba(0,212,170,0.35)',
  },
  Pagination: {
    itemColor: '#131a2b',
    itemColorActive: '#00d4aa',
    itemTextColor: '#8b949e',
    itemTextColorActive: '#0a0e17',
    itemBorder: '1px solid rgba(255,255,255,0.08)',
    itemBorderActive: '1px solid #00d4aa',
  },
  LoadingBar: {
    colorLoading: '#00d4aa',
  },
  Spin: {
    color: '#00d4aa',
  },
}
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }

body {
  background: #0a0e17;
  color: #e8eaed;
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

::selection {
  background: rgba(0,212,170,0.3);
  color: #e8eaed;
}

::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-track { background: #0a0e17; }
::-webkit-scrollbar-thumb { background: #2a3040; border-radius: 3px; }
::-webkit-scrollbar-thumb:hover { background: #3a4050; }

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.bg-grid {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background-image:
    linear-gradient(rgba(255,255,255,0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.02) 1px, transparent 1px);
  background-size: 60px 60px;
}

.main-content {
  flex: 1;
  max-width: 860px;
  width: 100%;
  margin: 0 auto;
  padding: 24px 20px;
  position: relative;
  z-index: 1;
}

/* fade transition */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.25s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* markdown content */
.markdown-body { color: #d0d5dd; line-height: 1.85; font-size: 16px; }
.markdown-body h1 { font-size: 2em; margin: 0.67em 0; color: #fff; font-family: 'JetBrains Mono', monospace; font-weight: 700; }
.markdown-body h2 { font-size: 1.5em; margin: 1.2em 0 0.6em; color: #00d4aa; font-family: 'JetBrains Mono', monospace; font-weight: 600; padding-bottom: 6px; border-bottom: 1px solid rgba(0,212,170,0.2); }
.markdown-body h3 { font-size: 1.17em; margin: 1em 0 0.5em; color: #e0e8f0; font-family: 'JetBrains Mono', monospace; }
.markdown-body p { margin-bottom: 0.9em; }
.markdown-body a { color: #00d4aa; text-decoration: none; border-bottom: 1px dashed rgba(0,212,170,0.4); transition: border-color 0.2s; }
.markdown-body a:hover { border-bottom-style: solid; }
.markdown-body strong { color: #fff; font-weight: 600; }
.markdown-body code {
  background: rgba(0,212,170,0.08); color: #00d4aa; padding: 3px 8px; border-radius: 4px;
  font-family: 'JetBrains Mono', monospace; font-size: 0.88em; border: 1px solid rgba(0,212,170,0.15);
}
.markdown-body pre {
  background: #0d1117; border: 1px solid rgba(255,255,255,0.08); border-radius: 8px;
  padding: 20px; overflow-x: auto; margin: 16px 0; position: relative;
}
.markdown-body pre::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; height: 1px;
  background: linear-gradient(90deg, #00d4aa, #7c3aed, transparent);
  opacity: 0.6;
}
.markdown-body pre code { background: none; border: none; padding: 0; color: #c9d1d9; font-size: 14px; line-height: 1.6; }
.markdown-body blockquote {
  border-left: 3px solid #00d4aa; padding: 12px 20px; margin: 16px 0;
  background: rgba(0,212,170,0.05); border-radius: 0 8px 8px 0; color: #8b949e;
}
.markdown-body ul, .markdown-body ol { padding-left: 1.5em; margin-bottom: 0.9em; }
.markdown-body li { margin-bottom: 0.3em; }
.markdown-body img { max-width: 100%; border-radius: 8px; border: 1px solid rgba(255,255,255,0.08); }
.markdown-body hr { border: none; height: 1px; background: linear-gradient(90deg, rgba(255,255,255,0.08), transparent); margin: 2em 0; }
.markdown-body table { width: 100%; border-collapse: collapse; margin: 16px 0; }
.markdown-body th { background: rgba(0,212,170,0.08); padding: 10px 16px; text-align: left; color: #00d4aa; border: 1px solid rgba(255,255,255,0.08); }
.markdown-body td { padding: 8px 16px; border: 1px solid rgba(255,255,255,0.08); }
</style>
