<template>
  <div class="search-page">
    <h2 class="page-title">
      <span class="title-icon">$</span> 搜索文章
    </h2>
    <div class="search-box">
      <n-input
        v-model:value="keyword" placeholder="输入关键词搜索文章..."
        size="large" clearable @keyup.enter="search"
        class="search-input"
      >
        <template #prefix>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        </template>
      </n-input>
      <n-button @click="search" size="large" class="search-btn">
        <template #icon>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 10 4 15 9 20"/><path d="M20 4v7a4 4 0 0 1-4 4H4"/></svg>
        </template>
        搜索
      </n-button>
    </div>
    <n-spin :show="loading">
      <transition-group name="list" tag="div">
        <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
      </transition-group>
    </n-spin>
    <div v-if="!loading && searched && articles.length === 0" class="no-result">
      <span class="no-result-icon">~$</span> 未找到相关文章
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getArticles } from '../api/index.js'
import ArticleCard from '../components/ArticleCard.vue'

const keyword = ref('')
const articles = ref([])
const loading = ref(false)
const searched = ref(false)

async function search() {
  if (!keyword.value.trim()) return
  loading.value = true
  searched.value = true
  try {
    const res = await getArticles({ keyword: keyword.value, size: 50 })
    articles.value = res.data.data.records
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.search-page { padding: 32px 0; }
.page-title { font-family: 'JetBrains Mono', monospace; font-size: 22px; color: #e8eaed; margin-bottom: 24px; font-weight: 600; }
.title-icon { color: #00d4aa; margin-right: 8px; }

.search-box { display: flex; gap: 12px; margin-bottom: 36px; }
.search-input { flex: 1; max-width: 480px; }

.search-btn {
  --n-color: rgba(0,212,170,0.1) !important;
  --n-color-hover: rgba(0,212,170,0.18) !important;
  --n-border: 1px solid rgba(0,212,170,0.35) !important;
  --n-border-hover: 1px solid rgba(0,212,170,0.6) !important;
  --n-text-color: #00d4aa !important;
  transition: all 0.3s;
}
.search-btn:hover { box-shadow: 0 0 20px rgba(0,212,170,0.15); }

.no-result { text-align: center; padding: 48px 0; color: #555d6b; font-family: 'JetBrains Mono', monospace; font-size: 14px; }
.no-result-icon { color: #00d4aa; margin-right: 6px; }

.list-enter-active { transition: all 0.4s ease; }
.list-enter-from { opacity: 0; transform: translateY(20px); }
</style>
