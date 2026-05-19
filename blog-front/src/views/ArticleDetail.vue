<template>
  <n-spin :show="loading">
    <article v-if="article" class="article-detail">
      <h1>{{ article.title }}</h1>
      <div class="meta">
        <span>{{ formatDate(article.createTime) }}</span>
        <span>{{ article.viewCount }} 阅读</span>
      </div>
      <div class="content markdown-body" v-html="renderMarkdown(article.content || '')"></div>
    </article>
    <n-empty v-else-if="!loading" description="文章不存在" />
    <CommentList v-if="article" :article-id="article.id" :comments="comments" :total="commentTotal" @refresh="fetchComments" />
  </n-spin>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleDetail, getComments } from '../api/index.js'
import CommentList from '../components/CommentList.vue'
import { marked } from 'marked'

const route = useRoute()
const article = ref(null)
const loading = ref(false)
const comments = ref([])
const commentTotal = ref(0)

onMounted(() => {
  fetchArticle()
  fetchComments()
})

async function fetchArticle() {
  loading.value = true
  try {
    const res = await getArticleDetail(route.params.id)
    article.value = res.data.data
  } finally {
    loading.value = false
  }
}

async function fetchComments() {
  const res = await getComments(route.params.id, { page: 1, size: 50 })
  comments.value = res.data.data.records
  commentTotal.value = res.data.data.total
}

function renderMarkdown(content) {
  return marked(content || '')
}

function formatDate(d) { return d ? d.substring(0, 10) : '' }
</script>

<style scoped>
.article-detail { padding: 32px 0; }
.article-detail h1 { font-size: 28px; color: #333; line-height: 1.4; }
.meta { margin-top: 16px; display: flex; gap: 16px; color: #bbb; font-size: 14px; }
.content { margin-top: 32px; font-size: 16px; line-height: 1.85; color: #444; }
.content :deep(h2) { margin: 28px 0 14px; font-size: 22px; }
.content :deep(h3) { margin: 22px 0 10px; font-size: 18px; }
.content :deep(p) { margin-bottom: 14px; }
.content :deep(code) { background: #f4f4f4; padding: 2px 6px; border-radius: 3px; font-size: 14px; }
.content :deep(pre) { background: #2d2d2d; color: #ccc; padding: 16px; border-radius: 6px; overflow-x: auto; margin: 16px 0; }
.content :deep(pre code) { background: none; padding: 0; color: inherit; }
.content :deep(img) { max-width: 100%; border-radius: 4px; }
.content :deep(blockquote) { border-left: 3px solid #5b8c5a; padding: 8px 16px; color: #888; margin: 16px 0; background: #f9f9f9; }
</style>
