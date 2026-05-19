<template>
  <div>
    <div class="page-header">
      <h1>Blog2026</h1>
      <p class="subtitle">记录思考与生活</p>
    </div>
    <n-spin :show="loading">
      <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
    </n-spin>
    <div class="pagination" v-if="total > size">
      <n-pagination v-model:page="page" :page-size="size" :item-count="total" @update:page="fetchData" />
    </div>
    <n-empty v-if="!loading && articles.length === 0" description="暂无文章" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getArticles } from '../api/index.js'
import ArticleCard from '../components/ArticleCard.vue'

const articles = ref([])
const loading = ref(false)
const page = ref(1)
const size = 10
const total = ref(0)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getArticles({ page: page.value, size: size.value })
    articles.value = res.data.data.records
    total.value = res.data.data.total
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page-header { text-align: center; padding: 48px 0 32px; }
.page-header h1 { font-size: 32px; color: #5b8c5a; font-weight: 700; letter-spacing: 2px; }
.subtitle { color: #aaa; margin-top: 8px; font-size: 15px; }
.pagination { margin-top: 32px; display: flex; justify-content: center; }
</style>
