<template>
  <div class="comment-manage">
    <h3 class="page-title">// 留言管理</h3>
    <div class="filter-bar">
      <el-radio-group v-model="filterStatus" @change="fetchData">
        <el-radio-button :value="null">全部</el-radio-button>
        <el-radio-button :value="1">已通过</el-radio-button>
        <el-radio-button :value="0">待审核</el-radio-button>
        <el-radio-button :value="-1">已拒绝</el-radio-button>
      </el-radio-group>
    </div>
    <el-table :data="comments" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column prop="content" label="内容" min-width="220" show-overflow-tooltip />
      <el-table-column prop="articleId" label="文章ID" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'warning' : 'danger'" size="small">
            {{ row.status === 1 ? '已通过' : row.status === 0 ? '待审核' : '已拒绝' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="170">
        <template #default="{ row }">{{ row.createTime ? row.createTime.substring(0, 16) : '' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button v-if="row.status !== 1" text type="success" size="small" @click="doUpdateStatus(row.id, 1)">通过</el-button>
          <el-button v-if="row.status !== -1" text type="warning" size="small" @click="doUpdateStatus(row.id, -1)">拒绝</el-button>
          <el-popconfirm title="确定删除？" @confirm="doDelete(row.id)">
            <template #reference><el-button text type="danger" size="small">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
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
.comment-manage { }
.page-title { font-family: 'JetBrains Mono', monospace; font-size: 16px; color: #e8eaed; font-weight: 500; margin: 0 0 16px; }
.filter-bar { margin-bottom: 20px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
