<template>
  <div class="tag-manage">
    <div class="toolbar">
      <h3 class="page-title">// 标签管理</h3>
      <el-button type="primary" @click="showForm(null)">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        新建标签
      </el-button>
    </div>
    <div v-loading="loading" class="tag-grid">
      <div v-for="tag in tags" :key="tag.id" class="tag-item" @click="showForm(tag)">
        <span class="tag-name">{{ tag.name }}</span>
        <el-popconfirm title="确定删除？" @confirm="doDelete(tag.id)" @click.stop>
          <template #reference><el-button text size="small" class="tag-close" @click.stop>×</el-button></template>
        </el-popconfirm>
      </div>
      <el-empty v-if="!loading && tags.length === 0" description="暂无标签" />
    </div>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑标签' : '新建标签'" width="400px">
      <el-input v-model="tagName" placeholder="标签名称" @keyup.enter="save" size="large" />
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getTags, createTag, updateTag, deleteTag } from '../api/index.js'

const tags = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const editing = ref(null)
const tagName = ref('')

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try { tags.value = (await getTags()).data.data } finally { loading.value = false }
}

function showForm(row) {
  if (row) {
    editing.value = row
    tagName.value = row.name
  } else {
    editing.value = null
    tagName.value = ''
  }
  dialogVisible.value = true
}

async function save() {
  if (!tagName.value.trim()) return
  if (editing.value) {
    await updateTag(editing.value.id, { name: tagName.value.trim() })
  } else {
    await createTag({ name: tagName.value.trim() })
  }
  dialogVisible.value = false
  fetchData()
}

async function doDelete(id) {
  await deleteTag(id)
  fetchData()
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-family: 'JetBrains Mono', monospace; font-size: 16px; color: #e8eaed; font-weight: 500; margin: 0; }

.tag-grid { display: flex; gap: 10px; flex-wrap: wrap; }
.tag-item {
  display: flex; align-items: center; gap: 6px; cursor: pointer;
  padding: 6px 10px 6px 16px; border-radius: 8px;
  background: rgba(0,212,170,0.06); border: 1px solid rgba(0,212,170,0.15);
  transition: all 0.3s;
}
.tag-item:hover { background: rgba(0,212,170,0.1); border-color: rgba(0,212,170,0.3); box-shadow: 0 0 12px rgba(0,212,170,0.08); }
.tag-name { font-family: 'JetBrains Mono', monospace; font-size: 13px; color: #00d4aa; }
.tag-close { color: #555d6b !important; padding: 2px; font-size: 16px; }
.tag-close:hover { color: #ff4757 !important; }
</style>
