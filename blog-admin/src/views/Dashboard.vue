<template>
  <div class="dashboard">
    <h3 class="page-title">// 仪表盘</h3>
    <el-row :gutter="24" style="margin-top:24px">
      <el-col :span="8" v-for="item in statItems" :key="item.label">
        <el-card shadow="never" class="stat-card">
          <div class="stat">
            <div class="stat-icon" :style="{ background: item.color }">
              <span v-html="item.icon"></span>
            </div>
            <div class="stat-body">
              <span class="num" :style="{ color: item.color }">{{ item.value }}</span>
              <span class="label">{{ item.label }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAdminArticles, getCategories, getAdminComments } from '../api/index.js'

const stats = ref({ articleCount: 0, categoryCount: 0, commentCount: 0 })

const statItems = computed(() => [
  {
    label: '文章',
    value: stats.value.articleCount,
    color: '#00d4aa',
    icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>'
  },
  {
    label: '分类',
    value: stats.value.categoryCount,
    color: '#7c3aed',
    icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>'
  },
  {
    label: '留言',
    value: stats.value.commentCount,
    color: '#f0a020',
    icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>'
  }
])

onMounted(async () => {
  const [aRes, cRes, mRes] = await Promise.all([
    getAdminArticles({ page: 1, size: 1 }),
    getCategories(),
    getAdminComments({ page: 1, size: 1 })
  ])
  stats.value.articleCount = aRes.data.data.total
  stats.value.categoryCount = cRes.data.data.length
  stats.value.commentCount = mRes.data.data.total
})
</script>

<style scoped>
.dashboard { }
.page-title { font-family: 'JetBrains Mono', monospace; font-size: 16px; color: #e8eaed; font-weight: 500; margin: 0; }

.stat-card {
  background: rgba(19,26,43,0.8) !important;
  border: 1px solid rgba(255,255,255,0.06) !important;
  border-radius: 12px !important;
  transition: all 0.3s;
  cursor: default;
}
.stat-card:hover { border-color: rgba(255,255,255,0.12) !important; transform: translateY(-2px); }

.stat { display: flex; align-items: center; gap: 18px; padding: 8px 0; }
.stat-icon {
  width: 52px; height: 52px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; flex-shrink: 0;
}
.stat-body { display: flex; flex-direction: column; }
.num { font-size: 32px; font-weight: 700; font-family: 'JetBrains Mono', monospace; line-height: 1; }
.label { margin-top: 4px; color: #555d6b; font-size: 13px; font-family: 'JetBrains Mono', monospace; }
</style>
