<template>
  <n-config-provider :theme-overrides="themeOverrides" :theme="null">
    <n-loading-bar-provider>
      <div class="app-root">
        <!-- Background layers -->
        <div class="bg-layer">
          <div class="bg-grid"></div>
          <div class="bg-glow bg-glow-1"></div>
          <div class="bg-glow bg-glow-2"></div>
          <canvas ref="particleCanvas" class="bg-particles"></canvas>
          <div class="bg-scanline"></div>
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
        </div>
      </div>
    </n-loading-bar-provider>
  </n-config-provider>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import AppHeader from './components/AppHeader.vue'
import AppFooter from './components/AppFooter.vue'

const themeOverrides = {
  common: {
    primaryColor: '#00d4aa',
    primaryColorHover: '#00e5c0',
    primaryColorPressed: '#00b890',
    primaryColorSuppl: '#00d4aa',
    bodyColor: '#111a25',
    cardColor: '#1a2740',
    modalColor: '#1a2740',
    popoverColor: '#1a2740',
    borderColor: 'rgba(255,255,255,0.08)',
    hoverColor: 'rgba(0,212,170,0.08)',
    fontFamily: "'Inter', system-ui, -apple-system, sans-serif",
    fontFamilyMono: "'JetBrains Mono', 'Fira Code', monospace",
    textColorBase: '#e8eaed',
    textColor1: '#e8eaed',
    textColor2: '#8b949e',
    textColor3: '#6e7687',
    inputColor: '#1a2740',
    inputColorFocus: '#1e3050',
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
    placeholderColor: '#6e7687',
  },
  Tag: {
    textColor: '#00d4aa',
    colorBordered: 'transparent',
    border: '1px solid rgba(0,212,170,0.35)',
  },
  Pagination: {
    itemColor: '#1a2740',
    itemColorActive: '#00d4aa',
    itemTextColor: '#8b949e',
    itemTextColorActive: '#111a25',
    itemBorder: '1px solid rgba(255,255,255,0.08)',
    itemBorderActive: '1px solid #00d4aa',
  },
  LoadingBar: { colorLoading: '#00d4aa' },
  Spin: { color: '#00d4aa' },
}

// Particle network
const particleCanvas = ref(null)
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

  const particles = Array.from({ length: 35 }, () => ({
    x: Math.random() * rect.width,
    y: Math.random() * rect.height,
    vx: (Math.random() - 0.5) * 0.35,
    vy: (Math.random() - 0.5) * 0.35,
    r: Math.random() * 1.2 + 0.3,
    alpha: Math.random() * 0.45 + 0.08,
  }))

  function draw() {
    ctx.clearRect(0, 0, rect.width, rect.height)
    particles.forEach((p, i) => {
      p.x += p.vx; p.y += p.vy
      if (p.x < 0) p.x = rect.width; if (p.x > rect.width) p.x = 0
      if (p.y < 0) p.y = rect.height; if (p.y > rect.height) p.y = 0

      ctx.beginPath()
      ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(0,212,170,${p.alpha})`
      ctx.fill()

      for (let j = i + 1; j < particles.length; j++) {
        const dx = p.x - particles[j].x
        const dy = p.y - particles[j].y
        const dist = Math.sqrt(dx * dx + dy * dy)
        if (dist < 140) {
          ctx.beginPath()
          ctx.moveTo(p.x, p.y); ctx.lineTo(particles[j].x, particles[j].y)
          ctx.strokeStyle = `rgba(0,212,170,${0.035 * (1 - dist / 140)})`
          ctx.stroke()
        }
      }
    })
    animId = requestAnimationFrame(draw)
  }
  draw()
}

onMounted(initParticles)
onUnmounted(() => { if (animId) cancelAnimationFrame(animId) })
</script>

<style>
/* ═══ Reset & Base ═══ */
* { margin: 0; padding: 0; box-sizing: border-box; }

body {
  background: #0d1520;
  color: #e8eaed;
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
  -webkit-font-smoothing: antialiased;
  overflow-x: hidden;
}

::selection { background: rgba(0,212,170,0.3); color: #e8eaed; }

::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: #2a3040; border-radius: 3px; border: 1px solid rgba(255,255,255,0.03); }
::-webkit-scrollbar-thumb:hover { background: #3a5060; }

/* ═══ App Shell ═══ */
.app-root { min-height: 100vh; position: relative; }
.app-container {
  min-height: 100vh; display: flex; flex-direction: column;
  position: relative; z-index: 1;
}

/* ═══ Background Layers ═══ */
.bg-layer { position: fixed; inset: 0; z-index: 0; pointer-events: none; }
.bg-grid {
  position: absolute; inset: 0;
  background-image:
    linear-gradient(rgba(255,255,255,0.022) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.022) 1px, transparent 1px);
  background-size: 64px 64px;
  mask-image: radial-gradient(ellipse at 50% 0%, black 60%, transparent 90%);
}
.bg-glow {
  position: absolute; border-radius: 50%; filter: blur(140px); opacity: 0.06;
}
.bg-glow-1 { width: 700px; height: 700px; background: #00d4aa; top: -300px; left: -150px; }
.bg-glow-2 { width: 500px; height: 500px; background: #7c3aed; bottom: -200px; right: -100px; }
.bg-particles { position: absolute; inset: 0; }
.bg-scanline {
  position: absolute; inset: 0;
  background: repeating-linear-gradient(0deg, transparent, transparent 3px, rgba(0,0,0,0.025) 3px, rgba(0,0,0,0.025) 4px);
}

/* ═══ Main Content ═══ */
.main-content {
  flex: 1; max-width: 860px; width: 100%; margin: 0 auto; padding: 24px 20px;
  position: relative; z-index: 1;
}

/* ═══ Page Transitions ═══ */
.page-enter-active { transition: all 0.3s ease; }
.page-leave-active { transition: all 0.15s ease; }
.page-enter-from { opacity: 0; transform: translateY(10px); }
.page-leave-to { opacity: 0; transform: translateY(-6px); }

/* ═══ Markdown Content ═══ */
.markdown-body { color: #d0d5dd; line-height: 1.85; font-size: 16px; }
.markdown-body h1 { font-size: 2em; margin: 0.67em 0; color: #fff; font-family: 'JetBrains Mono', monospace; font-weight: 700; }
.markdown-body h2 {
  font-size: 1.5em; margin: 1.3em 0 0.6em; color: #00d4aa;
  font-family: 'JetBrains Mono', monospace; font-weight: 600;
  padding-bottom: 6px; border-bottom: 2px solid rgba(0,212,170,0.15);
}
.markdown-body h3 { font-size: 1.17em; margin: 1em 0 0.5em; color: #e0e8f0; font-family: 'JetBrains Mono', monospace; }
.markdown-body p { margin-bottom: 0.9em; }
.markdown-body a {
  color: #00d4aa; text-decoration: none; border-bottom: 1px dashed rgba(0,212,170,0.4);
  transition: all 0.2s;
}
.markdown-body a:hover { border-bottom-style: solid; text-shadow: 0 0 8px rgba(0,212,170,0.3); }
.markdown-body strong { color: #fff; font-weight: 600; }
.markdown-body code {
  background: rgba(0,212,170,0.08); color: #00d4aa; padding: 3px 8px; border-radius: 4px;
  font-family: 'JetBrains Mono', monospace; font-size: 0.88em; border: 1px solid rgba(0,212,170,0.15);
}
.markdown-body pre {
  background: #1a2740; border: 1px solid rgba(255,255,255,0.10); border-radius: 8px;
  padding: 20px; overflow-x: auto; margin: 16px 0; position: relative;
}
.markdown-body pre::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; height: 1px;
  background: linear-gradient(90deg, #00d4aa, #7c3aed, transparent); opacity: 0.6;
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
