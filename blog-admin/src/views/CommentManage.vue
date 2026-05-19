<template>
  <div class="comment-manage">
    <h3>留言管理</h3>
    <div style="margin-top:16px">
      <el-radio-group v-model="filterStatus" @change="fetchData">
        <el-radio-button :value="null">全部</el-radio-button>
        <el-radio-button :value="1">已通过</el-radio-button>
        <el-radio-button :value="0">待审核</el-radio-button>
        <el-radio-button :value="-1">已拒绝</el-radio-button>
      </el-radio-group>
    </div>
    <el-table :data="comments" v-loading="loading" stripe style="margin-top:16px">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="articleId" label="文章ID" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'warning' : 'danger'">
            {{ row.status === 1 ? '已通过' : row.status === 0 ? '待审核' : '已拒绝' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="170">
        <template #default="{ row }">{{ row.createTime ? row.createTime.substring(0, 16) : '' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button v-if="row.status !== 1" text type="success" @click="doUpdateStatus(row.id, 1)">通过</el-button>
          <el-button v-if="row.status !== -1" text type="warning" @click="doUpdateStatus(row.id, -1)">拒绝</el-button>
          <el-popconfirm title="确定删除？" @confirm="doDelete(row.id)">
            <template #reference><el-button text type="danger">删除</el-button></template>
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
.comment-manage h3 { font-size: 18px; color: #303133; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
