<template>
  <n-spin :show="loading">
    <article v-if="article" class="article-detail">
      <div class="article-header">
        <div class="header-top">
          <span v-if="article.isTop" class="top-badge">置顶</span>
        </div>
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="meta">
          <div class="meta-item">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
            {{ formatDate(article.createTime) }}
          </div>
          <span class="meta-sep">|</span>
          <div class="meta-item">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
            {{ article.viewCount }} 阅读
          </div>
          <span class="meta-sep">|</span>
          <div class="meta-item" v-if="readingTime">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            {{ readingTime }}
          </div>
        </div>
      </div>

      <div class="divider"></div>

      <div class="content markdown-body" v-html="renderMarkdown(article.content || '')"></div>

      <div class="article-footer" v-if="article.tags && article.tags.length">
        <div class="tags-row">
          <span class="tags-label">标签:</span>
          <span v-for="tag in article.tags" :key="tag.id" class="tag-item">#{{ tag.name }}</span>
        </div>
      </div>
    </article>

    <n-empty v-else-if="!loading" description="文章不存在" />

    <nav v-if="prevArticle || nextArticle" class="article-nav">
      <router-link v-if="prevArticle" :to="`/article/${prevArticle.id}`" class="nav-link prev">
        <span class="nav-label">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>
          上一篇
        </span>
        <span class="nav-title">{{ prevArticle.title }}</span>
      </router-link>
      <router-link v-if="nextArticle" :to="`/article/${nextArticle.id}`" class="nav-link next">
        <span class="nav-label">
          下一篇
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
        </span>
        <span class="nav-title">{{ nextArticle.title }}</span>
      </router-link>
    </nav>

    <CommentList v-if="article" :article-id="article.id" :comments="comments" :total="commentTotal" @refresh="fetchComments" />

    <BackToTop />
  </n-spin>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleDetail, getArticleNav, getComments } from '../api/index.js'
import CommentList from '../components/CommentList.vue'
import BackToTop from '../components/BackToTop.vue'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

marked.use({
  renderer: {
    code({ text, lang }) {
      const language = lang && hljs.getLanguage(lang) ? lang : 'plaintext'
      const highlighted = hljs.highlight(text, { language }).value
      return `<pre><code class="hljs language-${language}">${highlighted}</code></pre>`
    }
  }
})

const route = useRoute()
const article = ref(null)
const loading = ref(false)
const comments = ref([])
const commentTotal = ref(0)
const prevArticle = ref(null)
const nextArticle = ref(null)

const readingTime = computed(() => {
  const text = article.value?.content || ''
  if (!text) return ''
  const minutes = Math.max(1, Math.ceil(text.length / 400))
  return `约 ${minutes} 分钟`
})

onMounted(() => { fetchArticle(); fetchComments() })

watch(() => route.params.id, () => {
  fetchArticle()
  fetchComments()
  window.scrollTo(0, 0)
})

async function fetchArticle() {
  loading.value = true
  try {
    const [detailRes, navRes] = await Promise.all([
      getArticleDetail(route.params.id),
      getArticleNav(route.params.id)
    ])
    article.value = detailRes.data.data
    const nav = navRes.data.data
    prevArticle.value = nav.prev?.id ? nav.prev : null
    nextArticle.value = nav.next?.id ? nav.next : null
  }
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
.article-detail { padding: 24px 0; }

.article-header { text-align: center; padding: 20px 0 16px; }
.header-top { margin-bottom: 10px; }
.top-badge {
  display: inline-flex; align-items: center;
  font-size: 11px; font-weight: 500;
  color: #e6a23c; padding: 3px 10px; border-radius: 4px;
  background: #fdf6ec; border: 1px solid #faecd8;
}

.article-title {
  font-size: 30px; color: #303133; line-height: 1.4; margin: 0 0 14px;
  font-weight: 700;
  background: linear-gradient(135deg, #409EFF, #8b5cf6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.meta { display: flex; gap: 10px; justify-content: center; align-items: center; color: #909399; font-size: 13px; }
.meta-item { display: flex; align-items: center; gap: 5px; }
.meta-sep { color: #d0d5dd; }

.divider {
  padding: 0 0 28px; display: flex; justify-content: center;
}
.divider::after {
  content: ''; width: 120px; height: 2px; border-radius: 1px;
  background: linear-gradient(90deg, transparent, #409EFF, #10b981, transparent);
}

.article-footer { margin-top: 40px; padding-top: 20px; border-top: 1px solid #f0f2f5; }
.tags-row { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.tags-label { font-size: 12px; color: #909399; font-weight: 500; }
.tag-item {
  font-size: 12px; color: #409EFF;
  padding: 3px 10px; border-radius: 4px;
  background: #ecf5ff; border: 1px solid #d9ecff;
  transition: all 0.2s;
}
.tag-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64,158,255,0.15);
}

.article-nav {
  display: flex; justify-content: space-between; gap: 16px;
  margin-top: 40px; padding-top: 24px;
  border-top: 1px solid #f0f2f5;
}
.nav-link {
  flex: 1; text-decoration: none; padding: 16px 20px; border-radius: 10px;
  background: rgba(255,255,255,0.6);
  border: 1px solid rgba(255,255,255,0.5);
  transition: all 0.25s;
}
.nav-link:hover {
  background: rgba(255,255,255,0.85);
  box-shadow: 0 2px 4px rgba(0,0,0,0.04), 0 8px 20px rgba(0,0,0,0.06);
  transform: translateY(-1px);
}
.nav-link.next { text-align: right; }
.nav-label {
  display: flex; align-items: center; gap: 4px;
  font-size: 11px; color: #909399; font-weight: 500; margin-bottom: 6px;
}
.nav-link.next .nav-label { justify-content: flex-end; }
.nav-title {
  font-size: 14px; color: #303133; font-weight: 500; line-height: 1.5;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
}
</style>
