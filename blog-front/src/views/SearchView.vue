<template>
  <div class="search-page">
    <div class="page-head">
      <h2 class="page-title">
        <span class="title-icon">$</span> 搜索文章
      </h2>
      <div class="page-line"></div>
    </div>

    <div class="search-box">
      <div class="search-input-wrap">
        <div class="search-prefix">
          <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        </div>
        <n-input
          v-model:value="keyword" placeholder="输入关键词搜索文章..."
          size="large" clearable @keyup.enter="search"
          class="search-input"
        />
        <n-button @click="search" size="large" class="search-btn">
          <template #icon>
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 10 4 15 9 20"/><path d="M20 4v7a4 4 0 0 1-4 4H4"/></svg>
          </template>
          SEARCH
        </n-button>
      </div>
    </div>

    <n-spin :show="loading">
      <transition-group name="list" tag="div">
        <ArticleCard v-for="(article, i) in articles" :key="article.id" :article="article" :style="{ '--i': i }" />
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
  loading.value = true; searched.value = true
  try {
    articles.value = (await getArticles({ keyword: keyword.value, size: 50 })).data.data.records
  } finally { loading.value = false }
}
</script>

<style scoped>
.search-page { padding: 32px 0; }
.page-head { margin-bottom: 28px; }
.page-title { font-family: 'JetBrains Mono', monospace; font-size: 22px; color: #e8eaed; margin-bottom: 12px; font-weight: 600; }
.title-icon { color: #00d4aa; margin-right: 8px; }
.page-line { height: 1px; background: linear-gradient(90deg, rgba(0,212,170,0.25), transparent); }

.search-box { margin-bottom: 36px; }
.search-input-wrap {
  display: flex; gap: 0; align-items: center;
  background: rgba(26,39,56,0.5); border: 1px solid rgba(255,255,255,0.06);
  border-radius: 14px; overflow: hidden;
  transition: all 0.3s; max-width: 560px; padding-left: 16px;
}
.search-input-wrap:focus-within {
  border-color: rgba(0,212,170,0.4);
  box-shadow: 0 0 24px rgba(0,212,170,0.08);
}
.search-prefix { color: #6e7687; display: flex; flex-shrink: 0; }
.search-input {
  flex: 1;
  --n-border: none !important;
  --n-border-hover: none !important;
  --n-border-focus: none !important;
  --n-box-shadow-focus: none !important;
}
.search-input :deep(.n-input__input-el) { background: transparent !important; }
.search-input :deep(.n-input__border) { display: none !important; }
.search-input :deep(.n-input__state-border) { display: none !important; }

.search-btn {
  flex-shrink: 0;
  --n-color: rgba(0,212,170,0.1) !important;
  --n-color-hover: rgba(0,212,170,0.18) !important;
  --n-border: 1px solid rgba(0,212,170,0.3) !important;
  --n-border-hover: 1px solid rgba(0,212,170,0.6) !important;
  --n-text-color: #00d4aa !important;
  font-family: 'JetBrains Mono', monospace !important; font-size: 12px !important;
  letter-spacing: 1px; border-radius: 0 14px 14px 0 !important; height: 42px !important;
  transition: all 0.3s;
}
.search-btn:hover { box-shadow: 0 0 20px rgba(0,212,170,0.2); }

.no-result { text-align: center; padding: 48px 0; color: #6e7687; font-family: 'JetBrains Mono', monospace; font-size: 14px; }
.no-result-icon { color: #00d4aa; margin-right: 6px; }

.list-enter-active { transition: all 0.45s ease; }
.list-enter-from { opacity: 0; transform: translateY(24px) scale(0.96); }
</style>
