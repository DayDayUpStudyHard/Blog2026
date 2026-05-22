<template>
  <div class="search-page">
    <div class="page-head">
      <h2 class="page-title">搜索文章</h2>
      <div class="page-line"></div>
    </div>

    <div class="search-box">
      <div class="search-input-wrap">
        <div class="search-prefix">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        </div>
        <n-input
          v-model:value="keyword" placeholder="输入关键词搜索文章..."
          size="large" clearable @keyup.enter="search"
          class="search-input"
        />
        <n-button @click="search" size="large" class="search-btn">
          <template #icon>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 10 4 15 9 20"/><path d="M20 4v7a4 4 0 0 1-4 4H4"/></svg>
          </template>
          搜索
        </n-button>
      </div>
    </div>

    <n-spin :show="loading">
      <transition-group name="list" tag="div">
        <ArticleCard v-for="(article, i) in articles" :key="article.id" :article="article" :style="{ '--i': i }" />
      </transition-group>
    </n-spin>

    <div v-if="!loading && searched && articles.length === 0" class="no-result">
      未找到相关文章
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
.search-page { padding: 28px 0; }
.page-head { margin-bottom: 24px; }
.page-title { font-size: 22px; color: #303133; margin-bottom: 10px; font-weight: 600; }
.page-line { height: 1px; background: #e8ecf0; }

.search-box { margin-bottom: 32px; }
.search-input-wrap {
  display: flex; gap: 0; align-items: center;
  background: rgba(255,255,255,0.7);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255,255,255,0.5);
  border-radius: 10px; overflow: hidden;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1); max-width: 560px; padding-left: 16px;
}
.search-input-wrap:focus-within {
  border-color: rgba(64,158,255,0.4);
  box-shadow: 0 0 0 3px rgba(64,158,255,0.12);
}
.search-prefix { color: #909399; display: flex; flex-shrink: 0; }
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
  --n-color: #ecf5ff !important;
  --n-color-hover: #d9ecff !important;
  --n-border: 1px solid #409EFF !important;
  --n-border-hover: 1px solid #66b1ff !important;
  --n-text-color: #409EFF !important;
  font-size: 12px !important;
  border-radius: 0 10px 10px 0 !important; height: 42px !important;
  transition: all 0.2s;
}

.no-result { text-align: center; padding: 48px 0; color: #909399; font-size: 14px; }

.list-enter-active { transition: all 0.4s ease; }
.list-enter-from { opacity: 0; transform: translateY(20px); }
</style>
