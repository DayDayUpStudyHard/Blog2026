<template>
  <div>
    <div class="page-header">
      <div class="hero-badge">v2.0</div>
      <h1 class="hero-title">
        <span class="hero-line">Blog<span class="hero-accent">2026</span></span>
      </h1>
      <p class="subtitle">
        <span class="cursor">▋</span> 记录思考与生活
      </p>
    </div>
    <n-spin :show="loading">
      <transition-group name="list" tag="div">
        <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
      </transition-group>
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
.page-header { text-align: center; padding: 56px 0 40px; position: relative; }
.hero-badge {
  display: inline-block; padding: 3px 12px; border-radius: 20px; font-size: 11px;
  font-family: 'JetBrains Mono', monospace; color: #00d4aa;
  border: 1px solid rgba(0,212,170,0.3); background: rgba(0,212,170,0.06);
  margin-bottom: 20px; letter-spacing: 1px;
}
.hero-title { font-size: 48px; font-weight: 700; margin: 0 0 12px; font-family: 'JetBrains Mono', monospace; }
.hero-line { display: block; }
.hero-accent { color: #00d4aa; }
.subtitle {
  color: #555d6b; font-size: 16px; font-family: 'JetBrains Mono', monospace;
  display: flex; align-items: center; justify-content: center; gap: 8px;
}
.cursor { color: #00d4aa; animation: blink 1s infinite; }
@keyframes blink { 0%, 100% { opacity: 1; } 50% { opacity: 0; } }

.pagination { margin-top: 40px; display: flex; justify-content: center; }

.list-enter-active { transition: all 0.4s ease; }
.list-enter-from { opacity: 0; transform: translateY(20px); }
</style>
