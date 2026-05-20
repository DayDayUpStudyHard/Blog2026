<template>
  <n-spin :show="loading">
    <article v-if="article" class="article-detail">
      <div class="article-header">
        <n-tag v-if="article.isTop" :bordered="false" size="small" class="top-badge">置顶</n-tag>
        <h1>{{ article.title }}</h1>
        <div class="meta">
          <span class="meta-item">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
            {{ formatDate(article.createTime) }}
          </span>
          <span class="meta-item">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
            {{ article.viewCount }} 阅读
          </span>
        </div>
      </div>
      <div class="divider"></div>
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

.article-header { text-align: center; padding-bottom: 16px; }
.article-header h1 {
  font-size: 30px; color: #e8eaed; line-height: 1.45; margin: 12px 0 16px;
  font-family: 'JetBrains Mono', monospace; font-weight: 700;
}
.top-badge {
  --n-text-color: #7c3aed !important;
  --n-border: 1px solid rgba(124,58,237,0.35) !important;
}
.meta { display: flex; gap: 20px; justify-content: center; color: #555d6b; font-size: 13px; font-family: 'JetBrains Mono', monospace; }
.meta-item { display: flex; align-items: center; gap: 5px; }

.divider {
  height: 1px; margin: 24px 0 32px;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.08), transparent);
}
</style>
