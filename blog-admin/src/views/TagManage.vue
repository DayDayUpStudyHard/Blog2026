<template>
  <div class="tag-manage">
    <div class="toolbar">
      <h3>标签管理</h3>
      <el-button type="primary" @click="showCreate">新建标签</el-button>
    </div>
    <div v-loading="loading">
      <el-tag v-for="tag in tags" :key="tag.id" closable size="large" @close="doDelete(tag.id)" style="margin:0 8px 8px 0;font-size:14px">
        {{ tag.name }}
      </el-tag>
      <el-empty v-if="!loading && tags.length === 0" description="暂无标签" />
    </div>

    <el-dialog v-model="dialogVisible" title="新建标签" width="400px">
      <el-input v-model="tagName" placeholder="标签名称" @keyup.enter="save" />
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getTags, createTag, deleteTag } from '../api/index.js'

const tags = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const tagName = ref('')

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try { tags.value = (await getTags()).data.data } finally { loading.value = false }
}

function showCreate() {
  tagName.value = ''
  dialogVisible.value = true
}

async function save() {
  if (!tagName.value.trim()) return
  await createTag({ name: tagName.value.trim() })
  dialogVisible.value = false
  fetchData()
}

async function doDelete(id) {
  await deleteTag(id)
  fetchData()
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.toolbar h3 { font-size: 18px; color: #303133; }
</style>
