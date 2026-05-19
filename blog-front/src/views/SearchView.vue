<template>
  <div class="search-page">
    <div class="search-box">
      <n-input v-model:value="keyword" placeholder="输入关键词搜索文章..." size="large" clearable
               @keyup.enter="search" />
      <n-button type="primary" @click="search" size="large" style="margin-left:12px">搜索</n-button>
    </div>
    <n-spin :show="loading">
      <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
    </n-spin>
    <n-empty v-if="!loading && searched && articles.length === 0" description="未找到相关文章" />
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
.search-page { padding: 48px 0; }
.search-box { display: flex; margin-bottom: 32px; }
</style>
