<template>
  <div class="article-list">
    <div class="page-header">
      <h3 class="page-title">// articles</h3>
      <div class="toolbar">
        <div class="filter-group">
          <span class="filter-label">筛选:</span>
          <el-select v-model="filterStatus" placeholder="状态" clearable @change="fetchData" class="filter-select" size="small">
            <el-option label="已发布" :value="1" />
            <el-option label="草稿" :value="0" />
          </el-select>
        </div>
        <el-button type="primary" @click="$router.push('/articles/create')">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          新建文章
        </el-button>
      </div>
    </div>

    <div class="table-container">
      <el-table :data="articles" v-loading="loading" stripe class="data-table">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="title" label="标题" min-width="240">
          <template #default="{ row }">
            <span class="article-title">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <span class="status-badge" :class="row.status === 1 ? 'published' : 'draft'">
              <span class="status-dot-inner"></span>
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="阅读" width="80" align="center">
          <template #default="{ row }">
            <span class="view-count">{{ row.viewCount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">
            <span class="time-cell">{{ row.createTime ? row.createTime.substring(0, 16) : '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <div class="actions">
              <button class="action-btn edit" @click="$router.push(`/articles/${row.id}/edit`)">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                编辑
              </button>
              <el-popconfirm title="确定删除？" @confirm="doDelete(row.id)">
                <template #reference>
                  <button class="action-btn delete" @click.stop>
                    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                    删除
                  </button>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination" v-if="total > pageSize">
      <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="fetchData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminArticles, deleteArticle } from '../api/index.js'

const articles = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const filterStatus = ref(null)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const params = { page: page.value, size: pageSize }
    if (filterStatus.value !== null && filterStatus.value !== '') params.status = filterStatus.value
    const res = await getAdminArticles(params)
    articles.value = res.data.data.records
    total.value = res.data.data.total
  } finally {
    loading.value = false
  }
}

async function doDelete(id) {
  await deleteArticle(id)
  fetchData()
}
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-family: 'JetBrains Mono', monospace; font-size: 16px; color: #e8eaed; font-weight: 500; margin: 0; }
.toolbar { display: flex; align-items: center; gap: 14px; }
.filter-group { display: flex; align-items: center; gap: 8px; }
.filter-label { font-family: 'JetBrains Mono', monospace; font-size: 10px; color: #6e7687; letter-spacing: 1px; }
.filter-select { width: 120px; }

.table-container {
  background: rgba(26,39,56,0.5); border: 1px solid rgba(255,255,255,0.04);
  border-radius: 12px; overflow: hidden;
  backdrop-filter: blur(12px);
}

.article-title { color: #e8eaed; font-weight: 500; }

.status-badge {
  display: inline-flex; align-items: center; gap: 5px;
  font-family: 'JetBrains Mono', monospace; font-size: 10px; letter-spacing: 0.5px;
  padding: 3px 10px; border-radius: 4px;
}
.status-badge.published { color: #00d4aa; background: rgba(0,212,170,0.08); border: 1px solid rgba(0,212,170,0.2); }
.status-badge.draft { color: #8b949e; background: rgba(139,148,158,0.08); border: 1px solid rgba(139,148,158,0.2); }
.status-dot-inner { width: 5px; height: 5px; border-radius: 50%; }
.published .status-dot-inner { background: #00d4aa; box-shadow: 0 0 4px rgba(0,212,170,0.5); }
.draft .status-dot-inner { background: #8b949e; }

.view-count { font-family: 'JetBrains Mono', monospace; font-size: 12px; color: #8b949e; }
.time-cell { font-family: 'JetBrains Mono', monospace; font-size: 11px; color: #7d8790; }

.actions { display: flex; gap: 8px; justify-content: center; }
.action-btn {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 4px 10px; border-radius: 5px; border: 1px solid transparent;
  font-family: 'JetBrains Mono', monospace; font-size: 10px; cursor: pointer;
  transition: all 0.2s; background: none; letter-spacing: 0.5px;
}
.action-btn.edit { color: #00d4aa; border-color: rgba(0,212,170,0.2); }
.action-btn.edit:hover { background: rgba(0,212,170,0.1); border-color: rgba(0,212,170,0.4); box-shadow: 0 0 10px rgba(0,212,170,0.1); }
.action-btn.delete { color: #ff4757; border-color: rgba(255,71,87,0.2); }
.action-btn.delete:hover { background: rgba(255,71,87,0.1); border-color: rgba(255,71,87,0.4); box-shadow: 0 0 10px rgba(255,71,87,0.1); }

.pagination { margin-top: 24px; display: flex; justify-content: flex-end; }
</style>
