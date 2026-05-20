<template>
  <div class="comment-manage">
    <div class="page-header">
      <h3 class="page-title">// comments</h3>
      <div class="toolbar">
        <div class="filter-group">
          <span class="filter-label">STATUS:</span>
          <el-radio-group v-model="filterStatus" @change="fetchData" size="small">
            <el-radio-button :value="null">ALL</el-radio-button>
            <el-radio-button :value="1">OK</el-radio-button>
            <el-radio-button :value="0">PEND</el-radio-button>
            <el-radio-button :value="-1">REJ</el-radio-button>
          </el-radio-group>
        </div>
      </div>
    </div>

    <div class="table-container">
      <el-table :data="comments" v-loading="loading" stripe class="data-table">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="author" label="作者" width="120">
          <template #default="{ row }">
            <span class="author-cell">{{ row.author }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="content-cell">{{ row.content }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="articleId" label="文章ID" width="80" align="center" />
        <el-table-column label="状态" width="110" align="center">
          <template #default="{ row }">
            <span class="status-badge" :class="statusClass(row.status)">
              <span class="status-dot-inner"></span>
              {{ statusText(row.status) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="时间" width="170">
          <template #default="{ row }">
            <span class="time-cell">{{ row.createTime ? row.createTime.substring(0, 16) : '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center">
          <template #default="{ row }">
            <div class="actions">
              <button v-if="row.status !== 1" class="action-btn approve" @click="doUpdateStatus(row.id, 1)">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"/></svg>
                OK
              </button>
              <button v-if="row.status !== -1" class="action-btn reject" @click="doUpdateStatus(row.id, -1)">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                REJ
              </button>
              <el-popconfirm title="确定删除？" @confirm="doDelete(row.id)">
                <template #reference>
                  <button class="action-btn del" @click.stop>
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                    DEL
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
import { getAdminComments, updateCommentStatus, deleteComment } from '../api/index.js'

const comments = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const filterStatus = ref(null)

onMounted(() => fetchData())

function statusClass(s) { return s === 1 ? 'approved' : s === 0 ? 'pending' : 'rejected' }
function statusText(s) { return s === 1 ? 'APPROVED' : s === 0 ? 'PENDING' : 'REJECTED' }

async function fetchData() {
  loading.value = true
  try {
    const params = { page: page.value, size: pageSize }
    if (filterStatus.value !== null) params.status = filterStatus.value
    const res = await getAdminComments(params)
    comments.value = res.data.data.records
    total.value = res.data.data.total
  } finally {
    loading.value = false
  }
}

async function doUpdateStatus(id, status) {
  await updateCommentStatus(id, { status })
  fetchData()
}

async function doDelete(id) {
  await deleteComment(id)
  fetchData()
}
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-family: 'JetBrains Mono', monospace; font-size: 16px; color: #e8eaed; font-weight: 500; margin: 0; }
.toolbar { display: flex; align-items: center; }
.filter-group { display: flex; align-items: center; gap: 10px; }
.filter-label { font-family: 'JetBrains Mono', monospace; font-size: 10px; color: #555d6b; letter-spacing: 1px; }

.table-container {
  background: rgba(17,24,39,0.5); border: 1px solid rgba(255,255,255,0.04);
  border-radius: 12px; overflow: hidden;
  backdrop-filter: blur(12px);
}

.author-cell { color: #e8eaed; font-weight: 500; }
.content-cell { color: #b0b8c4; }
.time-cell { font-family: 'JetBrains Mono', monospace; font-size: 11px; color: #6b7280; }

.status-badge {
  display: inline-flex; align-items: center; gap: 5px;
  font-family: 'JetBrains Mono', monospace; font-size: 10px; letter-spacing: 0.5px;
  padding: 3px 10px; border-radius: 4px;
}
.status-badge.approved { color: #00d4aa; background: rgba(0,212,170,0.08); border: 1px solid rgba(0,212,170,0.2); }
.status-badge.pending { color: #f0a020; background: rgba(240,160,32,0.08); border: 1px solid rgba(240,160,32,0.2); }
.status-badge.rejected { color: #ff4757; background: rgba(255,71,87,0.08); border: 1px solid rgba(255,71,87,0.2); }
.status-dot-inner { width: 5px; height: 5px; border-radius: 50%; }
.approved .status-dot-inner { background: #00d4aa; box-shadow: 0 0 4px rgba(0,212,170,0.5); }
.pending .status-dot-inner { background: #f0a020; box-shadow: 0 0 4px rgba(240,160,32,0.5); }
.rejected .status-dot-inner { background: #ff4757; box-shadow: 0 0 4px rgba(255,71,87,0.5); }

.actions { display: flex; gap: 6px; justify-content: center; }
.action-btn {
  display: inline-flex; align-items: center; gap: 3px;
  padding: 4px 8px; border-radius: 5px; border: 1px solid transparent;
  font-family: 'JetBrains Mono', monospace; font-size: 10px; cursor: pointer;
  transition: all 0.2s; background: none; letter-spacing: 0.5px;
}
.action-btn.approve { color: #00d4aa; border-color: rgba(0,212,170,0.2); }
.action-btn.approve:hover { background: rgba(0,212,170,0.1); border-color: rgba(0,212,170,0.4); box-shadow: 0 0 10px rgba(0,212,170,0.1); }
.action-btn.reject { color: #f0a020; border-color: rgba(240,160,32,0.2); }
.action-btn.reject:hover { background: rgba(240,160,32,0.1); border-color: rgba(240,160,32,0.4); box-shadow: 0 0 10px rgba(240,160,32,0.1); }
.action-btn.del { color: #ff4757; border-color: rgba(255,71,87,0.2); }
.action-btn.del:hover { background: rgba(255,71,87,0.1); border-color: rgba(255,71,87,0.4); box-shadow: 0 0 10px rgba(255,71,87,0.1); }

.pagination { margin-top: 24px; display: flex; justify-content: flex-end; }
</style>
