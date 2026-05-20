<template>
  <div class="dashboard">
    <div class="page-header">
      <h3 class="page-title">// dashboard</h3>
      <span class="page-badge">SYSTEM ONLINE</span>
    </div>

    <!-- Stat Cards -->
    <el-row :gutter="24" class="stat-row">
      <el-col :span="8" v-for="(item, i) in statItems" :key="item.label">
        <div class="stat-card" :style="{ '--delay': i * 0.1 + 's' }">
          <div class="stat-inner">
            <div class="stat-ring" :style="{ borderColor: item.color }">
              <svg class="stat-ring-svg" viewBox="0 0 100 100">
                <circle cx="50" cy="50" r="44" fill="none" :stroke="item.color" stroke-width="2" opacity="0.15" />
                <circle cx="50" cy="50" r="44" fill="none" :stroke="item.color" stroke-width="2"
                  stroke-linecap="round" :stroke-dasharray="276" :stroke-dashoffset="276 - (276 * animProgress[i])"
                  class="ring-fill" />
              </svg>
            </div>
            <div class="stat-icon" :style="{ background: item.gradient, boxShadow: item.glow }">
              <span v-html="item.icon"></span>
            </div>
            <div class="stat-body">
              <span class="num" ref="numRefs" :style="{ color: item.color }">{{ displayValues[i] }}</span>
              <span class="label">{{ item.label }}</span>
            </div>
          </div>
          <div class="stat-bg-label">{{ item.bgLabel }}</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { getAdminArticles, getCategories, getAdminComments } from '../api/index.js'

const stats = ref({ articleCount: 0, categoryCount: 0, commentCount: 0 })
const displayValues = ref([0, 0, 0])
const animProgress = ref([0, 0, 0])

const statItems = computed(() => [
  {
    label: 'Articles',
    value: stats.value.articleCount,
    bgLabel: 'POSTS',
    color: '#00d4aa',
    gradient: 'linear-gradient(135deg, rgba(0,212,170,0.2), rgba(0,212,170,0.05))',
    glow: '0 0 20px rgba(0,212,170,0.2)',
    icon: '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>'
  },
  {
    label: 'Categories',
    value: stats.value.categoryCount,
    bgLabel: 'DIRS',
    color: '#7c3aed',
    gradient: 'linear-gradient(135deg, rgba(124,58,237,0.2), rgba(124,58,237,0.05))',
    glow: '0 0 20px rgba(124,58,237,0.2)',
    icon: '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>'
  },
  {
    label: 'Comments',
    value: stats.value.commentCount,
    bgLabel: 'MSGS',
    color: '#f0a020',
    gradient: 'linear-gradient(135deg, rgba(240,160,32,0.2), rgba(240,160,32,0.05))',
    glow: '0 0 20px rgba(240,160,32,0.2)',
    icon: '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>'
  }
])

function animateValue(idx, from, to) {
  const duration = 1200
  const start = performance.now()
  function tick(now) {
    const elapsed = now - start
    const progress = Math.min(elapsed / duration, 1)
    // easeOutCubic
    const eased = 1 - Math.pow(1 - progress, 3)
    displayValues.value[idx] = Math.round(from + (to - from) * eased)
    animProgress.value[idx] = eased
    if (progress < 1) requestAnimationFrame(tick)
  }
  requestAnimationFrame(tick)
}

onMounted(async () => {
  const [aRes, cRes, mRes] = await Promise.all([
    getAdminArticles({ page: 1, size: 1 }),
    getCategories(),
    getAdminComments({ page: 1, size: 1 })
  ])
  const newStats = {
    articleCount: aRes.data.data.total,
    categoryCount: cRes.data.data.length,
    commentCount: mRes.data.data.total
  }
  stats.value = newStats
  animateValue(0, 0, newStats.articleCount)
  animateValue(1, 0, newStats.categoryCount)
  animateValue(2, 0, newStats.commentCount)
})
</script>

<style scoped>
.dashboard { }
.page-header { display: flex; align-items: center; gap: 14px; margin-bottom: 32px; }
.page-title {
  font-family: 'JetBrains Mono', monospace; font-size: 16px; color: #e8eaed; font-weight: 500; margin: 0;
}
.page-badge {
  font-family: 'JetBrains Mono', monospace; font-size: 9px; color: #00d4aa;
  padding: 3px 10px; border-radius: 20px;
  border: 1px solid rgba(0,212,170,0.25); letter-spacing: 2px;
  animation: neonPulse 2s ease-in-out infinite;
}

.stat-row { margin-top: 8px; }

.stat-card {
  position: relative; padding: 32px 28px; border-radius: 16px;
  background: rgba(17,24,39,0.7);
  border: 1px solid rgba(255,255,255,0.05);
  backdrop-filter: blur(16px);
  overflow: hidden;
  transition: all 0.4s;
  animation: floatUp 0.5s ease forwards;
  animation-delay: var(--delay);
  opacity: 0;
}
.stat-card:hover {
  transform: translateY(-4px);
  border-color: rgba(255,255,255,0.1);
  box-shadow: 0 12px 40px rgba(0,0,0,0.4), 0 0 0 1px rgba(0,212,170,0.08);
}

.stat-bg-label {
  position: absolute; top: 12px; right: 16px;
  font-family: 'JetBrains Mono', monospace; font-size: 52px; font-weight: 700;
  color: rgba(255,255,255,0.015); pointer-events: none; line-height: 1;
  user-select: none;
}

.stat-inner { display: flex; align-items: center; gap: 20px; position: relative; z-index: 1; }

.stat-icon {
  width: 56px; height: 56px; border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.stat-ring { position: absolute; left: 0; width: 56px; height: 56px; }
.stat-ring-svg { width: 56px; height: 56px; }
.ring-fill { transition: stroke-dashoffset 0.3s; filter: drop-shadow(0 0 4px currentColor); }

.stat-body { display: flex; flex-direction: column; }
.num {
  font-size: 40px; font-weight: 700; font-family: 'JetBrains Mono', monospace;
  line-height: 1; letter-spacing: -1px;
  animation: counterTick 2s ease-in-out infinite;
}
.label {
  margin-top: 4px; color: #555d6b; font-size: 12px;
  font-family: 'JetBrains Mono', monospace; text-transform: uppercase; letter-spacing: 1px;
}

/* ═══ Keyframes ═══ */
@keyframes floatUp {
  0% { opacity: 0; transform: translateY(16px); }
  100% { opacity: 1; transform: translateY(0); }
}
@keyframes neonPulse {
  0%, 100% { opacity: 0.7; }
  50% { opacity: 1; }
}
@keyframes counterTick {
  0%, 100% { text-shadow: 0 0 8px currentColor; }
  50% { text-shadow: 0 0 20px currentColor, 0 0 40px currentColor; }
}
</style>
