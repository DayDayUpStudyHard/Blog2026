<template>
  <div class="tag-manage">
    <div class="page-header">
      <h3 class="page-title">// tags</h3>
      <el-button type="primary" @click="showForm(null)">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        NEW TAG
      </el-button>
    </div>

    <div v-loading="loading" class="tag-grid">
      <div v-for="tag in tags" :key="tag.id" class="tag-card" @click="showForm(tag)">
        <div class="tag-card-glow"></div>
        <span class="tag-name"># {{ tag.name }}</span>
        <div class="tag-meta">
          <span class="tag-id">ID:{{ tag.id }}</span>
          <el-popconfirm title="确定删除？" @confirm="doDelete(tag.id)" @click.stop>
            <template #reference><button class="tag-close-btn" @click.stop>&times;</button></template>
          </el-popconfirm>
        </div>
      </div>
      <el-empty v-if="!loading && tags.length === 0" description="暂无标签" />
    </div>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑标签' : '新建标签'" width="420px" class="cool-dialog">
      <div class="dialog-form">
        <el-input v-model="tagName" placeholder="标签名称" @keyup.enter="save" size="large" class="tag-input" />
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">CANCEL</el-button>
        <el-button type="primary" @click="save">SAVE</el-button>
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
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-family: 'JetBrains Mono', monospace; font-size: 16px; color: #e8eaed; font-weight: 500; margin: 0; }

.tag-grid { display: flex; gap: 12px; flex-wrap: wrap; }

.tag-card {
  position: relative; display: flex; align-items: center; gap: 12px;
  cursor: pointer; padding: 10px 16px; border-radius: 10px;
  background: rgba(17,24,39,0.6); border: 1px solid rgba(0,212,170,0.15);
  backdrop-filter: blur(8px);
  transition: all 0.3s; overflow: hidden;
}
.tag-card::before {
  content: '';
  position: absolute; inset: 0;
  background: linear-gradient(135deg, transparent 40%, rgba(0,212,170,0.04) 50%, transparent 60%);
  background-size: 200% 100%;
}
.tag-card:hover {
  border-color: rgba(0,212,170,0.4);
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.3), 0 0 20px rgba(0,212,170,0.08);
}
.tag-card:hover::before { animation: sweepRight 2s ease-in-out infinite; }
.tag-card-glow {
  position: absolute; inset: -1px; border-radius: 10px; padding: 1px;
  background: linear-gradient(135deg, transparent, rgba(0,212,170,0.15), transparent);
  opacity: 0; transition: opacity 0.3s;
}
.tag-card:hover .tag-card-glow { opacity: 1; }

.tag-name { font-family: 'JetBrains Mono', monospace; font-size: 13px; color: #00d4aa; font-weight: 500; }
.tag-meta { display: flex; align-items: center; gap: 8px; }
.tag-id { font-family: 'JetBrains Mono', monospace; font-size: 10px; color: #555d6b; }
.tag-close-btn {
  background: none; border: none; color: #555d6b; font-size: 16px; cursor: pointer;
  padding: 0 2px; line-height: 1; transition: all 0.2s; border-radius: 3px;
}
.tag-close-btn:hover { color: #ff4757; background: rgba(255,71,87,0.1); }

.tag-input { padding-top: 8px; }

@keyframes sweepRight {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}
</style>
