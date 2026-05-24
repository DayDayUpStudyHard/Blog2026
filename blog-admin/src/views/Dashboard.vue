<template>
  <div class="dashboard">
    <div class="page-header">
      <h3 class="page-title">仪表盘</h3>
      <span class="page-badge">运行中</span>
    </div>

    <el-row :gutter="20" class="stat-row">
      <el-col :span="8" v-for="(item, i) in statItems" :key="item.label">
        <div class="stat-card">
          <div class="stat-icon" :style="{ background: item.bg, color: item.color }">
            <span v-html="item.icon"></span>
          </div>
          <div class="stat-body">
            <span class="num" :style="{ color: item.color }">{{ displayValues[i] }}</span>
            <span class="label">{{ item.label }}</span>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAdminArticles, getCategories, getAdminComments } from '../api/index.js'

const stats = ref({ articleCount: 0, categoryCount: 0, commentCount: 0 })
const displayValues = ref([0, 0, 0])

const statItems = computed(() => [
  {
    label: '文章',
    value: stats.value.articleCount,
    color: '#409EFF',
    bg: 'linear-gradient(135deg, #ecf5ff, #d9ecff)',
    icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>'
  },
  {
    label: '分类',
    value: stats.value.categoryCount,
    color: '#10b981',
    bg: 'linear-gradient(135deg, #ecfdf5, #d1fae5)',
    icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>'
  },
  {
    label: '留言',
    value: stats.value.commentCount,
    color: '#f59e0b',
    bg: 'linear-gradient(135deg, #fffbeb, #fef3c7)',
    icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>'
  }
])

function animateValue(idx, from, to) {
  const duration = 1000
  const start = performance.now()
  function tick(now) {
    const elapsed = now - start
    const progress = Math.min(elapsed / duration, 1)
    const eased = 1 - Math.pow(1 - progress, 3)
    displayValues.value[idx] = Math.round(from + (to - from) * eased)
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
.page-header { display: flex; align-items: center; gap: 12px; margin-bottom: 24px; }
.page-title { font-size: 18px; color: #303133; font-weight: 600; margin: 0; }
.page-badge {
  font-size: 11px; color: #10b981; padding: 2px 10px; border-radius: 12px;
  background: linear-gradient(135deg, #ecfdf5, #d1fae5); font-weight: 500;
}

.stat-card {
  display: flex; align-items: center; gap: 18px;
  background: rgba(255,255,255,0.7);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255,255,255,0.5);
  border-radius: 14px; padding: 28px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.03), 0 1px 3px rgba(0,0,0,0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative; overflow: hidden;
}
/* Top gradient bar */
.stat-card::before {
  content: '';
  position: absolute; top: 0; left: 0; right: 0;
  height: 3px;
  border-radius: 14px 14px 0 0;
}
.stat-card:nth-child(1)::before { background: linear-gradient(90deg, #409EFF, #66b1ff); }
.stat-card:nth-child(2)::before { background: linear-gradient(90deg, #10b981, #34d399); }
.stat-card:nth-child(3)::before { background: linear-gradient(90deg, #f59e0b, #fbbf24); }

/* Grid texture overlay */
.stat-card::after {
  content: '';
  position: absolute; inset: 0; pointer-events: none;
  background-image:
    linear-gradient(rgba(64,158,255,0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(64,158,255,0.02) 1px, transparent 1px);
  background-size: 20px 20px;
  -webkit-mask-image: radial-gradient(circle at 30% 20%, black 40%, transparent 70%);
  mask-image: radial-gradient(circle at 30% 20%, black 40%, transparent 70%);
}

.stat-card:hover {
  box-shadow: 0 8px 30px rgba(0,0,0,0.1);
  transform: translateY(-4px);
}

.stat-icon {
  width: 56px; height: 56px; border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  transition: transform 0.3s;
  position: relative; z-index: 1;
}
.stat-card:hover .stat-icon {
  transform: scale(1.08);
}

.stat-body { display: flex; flex-direction: column; position: relative; z-index: 1; }
.num {
  font-size: 36px; font-weight: 800; line-height: 1.1;
  font-variant-numeric: tabular-nums;
  font-feature-settings: "tnum";
}
.label { margin-top: 4px; color: #909399; font-size: 13px; font-weight: 500; }
</style>
