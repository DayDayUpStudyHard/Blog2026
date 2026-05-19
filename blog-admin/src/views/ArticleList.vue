<template>
  <div class="article-list">
    <div class="toolbar">
      <el-select v-model="filterStatus" placeholder="状态筛选" clearable @change="fetchData" style="width:140px">
        <el-option label="已发布" :value="1" />
        <el-option label="草稿" :value="0" />
      </el-select>
      <el-button type="primary" @click="$router.push('/articles/create')">新建文章</el-button>
    </div>
    <el-table :data="articles" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '已发布' : '草稿' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="viewCount" label="阅读" width="80" />
      <el-table-column prop="createTime" label="创建时间" width="170">
        <template #default="{ row }">{{ row.createTime ? row.createTime.substring(0, 16) : '' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button text type="primary" @click="$router.push(`/articles/${row.id}/edit`)">编辑</el-button>
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
.toolbar { display: flex; justify-content: space-between; margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
