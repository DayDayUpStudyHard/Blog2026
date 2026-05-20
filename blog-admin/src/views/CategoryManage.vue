<template>
  <div class="category-manage">
    <div class="toolbar">
      <h3 class="page-title">// 分类管理</h3>
      <el-button type="primary" @click="showForm(null)">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        新建分类
      </el-button>
    </div>
    <el-table :data="categories" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="名称" width="180" />
      <el-table-column prop="description" label="描述" min-width="200" />
      <el-table-column prop="sort" label="排序" width="80" align="center" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button text type="primary" @click="showForm(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="doDelete(row.id)">
            <template #reference><el-button text type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑分类' : '新建分类'" width="480px">
      <el-form :model="form" label-width="60px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="分类名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" placeholder="分类描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCategories, createCategory, updateCategory, deleteCategory } from '../api/index.js'

const categories = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const editing = ref(null)
const form = ref({ name: '', description: '', sort: 0 })

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try { categories.value = (await getCategories()).data.data } finally { loading.value = false }
}

function showForm(row) {
  if (row) {
    editing.value = row
    form.value = { name: row.name, description: row.description || '', sort: row.sort || 0 }
  } else {
    editing.value = null
    form.value = { name: '', description: '', sort: 0 }
  }
  dialogVisible.value = true
}

async function save() {
  if (editing.value) {
    await updateCategory(editing.value.id, form.value)
  } else {
    await createCategory(form.value)
  }
  dialogVisible.value = false
  fetchData()
}

async function doDelete(id) {
  await deleteCategory(id)
  fetchData()
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-title { font-family: 'JetBrains Mono', monospace; font-size: 16px; color: #e8eaed; font-weight: 500; margin: 0; }
</style>
