<template>
  <div>
    <div class="hero">
      <h1 class="hero-title">Blog2026</h1>
      <p class="hero-subtitle">记录思考与生活</p>
    </div>

    <n-spin :show="loading">
      <transition-group name="list" tag="div">
        <ArticleCard v-for="(article, i) in articles" :key="article.id" :article="article" :style="{ '--i': i }" />
      </transition-group>
    </n-spin>

    <div class="pagination" v-if="total > size">
      <n-pagination v-model:page="page" :page-size="size" :item-count="total" @update:page="fetchData" />
    </div>

    <n-empty v-if="!loading && articles.length === 0" description="暂无文章" class="empty-state" />

    <BackToTop />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getArticles } from '../api/index.js'
import ArticleCard from '../components/ArticleCard.vue'
import BackToTop from '../components/BackToTop.vue'

const articles = ref([])
const loading = ref(false)
const page = ref(1)
const size = 10
const total = ref(0)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getArticles({ page: page.value, size: size })
    articles.value = res.data.data.records
    total.value = res.data.data.total
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.hero {
  text-align: center; padding: 64px 0 48px;
}
.hero-title {
  font-size: 48px; font-weight: 700;
  background: linear-gradient(135deg, #409EFF 0%, #8b5cf6 50%, #10b981 100%);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradientShift 4s ease infinite;
  letter-spacing: -1px;
}
@keyframes gradientShift {
  0% { background-position: 0% center; }
  50% { background-position: 100% center; }
  100% { background-position: 0% center; }
}
.hero-subtitle {
  color: #909399; font-size: 17px; margin-top: 8px;
}

.pagination { margin-top: 40px; display: flex; justify-content: center; }
.empty-state { margin-top: 40px; }

.list-enter-active { transition: all 0.4s ease; }
.list-enter-from { opacity: 0; transform: translateY(20px); }
</style>
