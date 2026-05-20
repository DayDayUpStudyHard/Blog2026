<template>
  <n-spin :show="loading">
    <article v-if="article" class="article-detail">
      <!-- Header -->
      <div class="article-header">
        <div class="header-top">
          <span v-if="article.isTop" class="top-badge">
            <span class="top-dot"></span> PINNED
          </span>
        </div>
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="meta">
          <div class="meta-item">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
            {{ formatDate(article.createTime) }}
          </div>
          <span class="meta-sep">|</span>
          <div class="meta-item">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
            {{ article.viewCount }} 阅读
          </div>
        </div>
      </div>

      <!-- Gradient divider -->
      <div class="divider">
        <div class="divider-glow"></div>
      </div>

      <!-- Content -->
      <div class="content markdown-body" v-html="renderMarkdown(article.content || '')"></div>

      <!-- Tags -->
      <div class="article-footer" v-if="article.tags && article.tags.length">
        <div class="tags-row">
          <span class="tags-label">TAGS:</span>
          <span v-for="tag in article.tags" :key="tag.id" class="tag-item">#{{ tag.name }}</span>
        </div>
      </div>
    </article>

    <n-empty v-else-if="!loading" description="文章不存在" />

    <!-- Comments -->
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

onMounted(() => { fetchArticle(); fetchComments() })

async function fetchArticle() {
  loading.value = true
  try { article.value = (await getArticleDetail(route.params.id)).data.data }
  finally { loading.value = false }
}

async function fetchComments() {
  const res = await getComments(route.params.id, { page: 1, size: 50 })
  comments.value = res.data.data.records
  commentTotal.value = res.data.data.total
}

function renderMarkdown(content) { return marked(content || '') }
function formatDate(d) { return d ? d.substring(0, 10) : '' }
</script>

<style scoped>
.article-detail { padding: 32px 0; }

/* Header */
.article-header { text-align: center; padding: 24px 0 20px; }
.header-top { margin-bottom: 12px; }
.top-badge {
  display: inline-flex; align-items: center; gap: 5px;
  font-family: 'JetBrains Mono', monospace; font-size: 9px; letter-spacing: 1px;
  color: #7c3aed; padding: 3px 10px; border-radius: 4px;
  background: rgba(124,58,237,0.08); border: 1px solid rgba(124,58,237,0.2);
}
.top-dot { width: 4px; height: 4px; border-radius: 50%; background: #7c3aed; box-shadow: 0 0 6px rgba(124,58,237,0.5); }

.article-title {
  font-size: 32px; color: #e8eaed; line-height: 1.45; margin: 0 0 18px;
  font-family: 'JetBrains Mono', monospace; font-weight: 700;
  background: linear-gradient(135deg, #fff 0%, #e8eaed 50%, #00d4aa 100%);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
}

.meta { display: flex; gap: 10px; justify-content: center; align-items: center; color: #6e7687; font-size: 12px; font-family: 'JetBrains Mono', monospace; }
.meta-item { display: flex; align-items: center; gap: 5px; }
.meta-sep { color: #3a4555; }

/* Divider */
.divider { padding: 0 0 32px; display: flex; justify-content: center; }
.divider-glow {
  width: 80px; height: 2px; border-radius: 1px;
  background: linear-gradient(90deg, transparent, #00d4aa, #7c3aed, transparent);
}

/* Article footer */
.article-footer { margin-top: 40px; padding-top: 24px; border-top: 1px solid rgba(255,255,255,0.05); }
.tags-row { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.tags-label { font-family: 'JetBrains Mono', monospace; font-size: 10px; color: #6e7687; letter-spacing: 1px; }
.tag-item {
  font-family: 'JetBrains Mono', monospace; font-size: 12px; color: #00d4aa;
  padding: 3px 10px; border-radius: 4px;
  background: rgba(0,212,170,0.06); border: 1px solid rgba(0,212,170,0.15);
}
</style>
