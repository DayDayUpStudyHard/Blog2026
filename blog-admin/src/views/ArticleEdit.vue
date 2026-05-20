<template>
  <div class="article-edit">
    <h3 class="page-title">{{ isEdit ? '// 编辑文章' : '// 新建文章' }}</h3>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="60px" class="edit-form">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="文章标题" />
      </el-form-item>
      <el-form-item label="摘要">
        <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="留空则自动截取正文前200字" />
      </el-form-item>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="分类">
            <el-select v-model="form.categoryId" placeholder="选择分类" clearable style="width:100%">
              <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="标签">
            <el-select v-model="form.tagIds" multiple placeholder="选择标签" style="width:100%">
              <el-option v-for="tag in tags" :key="tag.id" :label="tag.name" :value="tag.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="封面">
            <el-input v-model="form.cover" placeholder="封面图片URL" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="置顶">
            <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="内容" prop="content">
        <MdEditor v-model="form.content" language="en-US" style="height:520px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="saveDraft">保存草稿</el-button>
        <el-button type="success" @click="publish">发布</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAdminArticleDetail, createArticle, updateArticle, getCategories, getTags } from '../api/index.js'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const formRef = ref(null)
const categories = ref([])
const tags = ref([])

const form = ref({
  title: '',
  content: '',
  summary: '',
  categoryId: null,
  tagIds: [],
  cover: '',
  isTop: 0,
  status: 0
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

onMounted(async () => {
  const [catRes, tagRes] = await Promise.all([getCategories(), getTags()])
  categories.value = catRes.data.data
  tags.value = tagRes.data.data

  if (route.params.id) {
    isEdit.value = true
    const res = await getAdminArticleDetail(route.params.id)
    const article = res.data.data
    if (article) {
      Object.assign(form.value, {
        title: article.title,
        content: article.content || '',
        summary: article.summary || '',
        categoryId: article.categoryId,
        tagIds: [],
        cover: article.cover || '',
        isTop: article.isTop || 0,
        status: article.status || 0
      })
    }
  }
})

async function save(goBack = false) {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  const data = { ...form.value }
  if (!data.summary && data.content) {
    data.summary = data.content.replace(/[#*>`\n\r]/g, '').substring(0, 200)
  }
  if (isEdit.value) {
    await updateArticle(route.params.id, data)
  } else {
    await createArticle(data)
  }
  if (goBack) router.push('/articles')
}

async function saveDraft() {
  form.value.status = 0
  await save()
}

async function publish() {
  form.value.status = 1
  await save(true)
}
</script>

<style scoped>
.article-edit { }
.page-title { font-family: 'JetBrains Mono', monospace; font-size: 16px; color: #e8eaed; font-weight: 500; margin: 0 0 20px; }
.edit-form { max-width: 960px; }
</style>
