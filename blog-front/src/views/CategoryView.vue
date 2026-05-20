<template>
  <div class="category-page">
    <h2 class="page-title">
      <span class="title-icon">##</span> 分类浏览
    </h2>
    <n-spin :show="loading">
      <div class="category-list" v-if="categories.length">
        <button
          v-for="cat in categories" :key="cat.id"
          class="cat-btn" :class="{ active: activeCat === cat.id }"
          @click="selectCategory(cat.id)"
        >
          <span class="cat-name">{{ cat.name }}</span>
          <span class="cat-desc" v-if="cat.description">{{ cat.description }}</span>
        </button>
      </div>
      <n-empty v-else description="暂无分类" />
    </n-spin>

    <div v-if="articles.length" class="result">
      <div class="result-header">
        <span class="result-count">找到 {{ articles.length }} 篇文章</span>
      </div>
      <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getArticles, getCategories } from '../api/index.js'
import ArticleCard from '../components/ArticleCard.vue'

const categories = ref([])
const articles = ref([])
const loading = ref(false)
const activeCat = ref(null)

onMounted(async () => {
  loading.value = true
  const res = await getCategories()
  categories.value = res.data.data
  loading.value = false
})

async function selectCategory(id) {
  activeCat.value = id
  loading.value = true
  try {
    const res = await getArticles({ categoryId: id, size: 50 })
    articles.value = res.data.data.records
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.category-page { padding: 32px 0; }
.page-title { font-family: 'JetBrains Mono', monospace; font-size: 22px; color: #e8eaed; margin-bottom: 28px; font-weight: 600; }
.title-icon { color: #00d4aa; margin-right: 8px; }

.category-list { display: flex; gap: 12px; flex-wrap: wrap; margin-bottom: 36px; }
.cat-btn {
  background: rgba(19,26,43,0.8); border: 1px solid rgba(255,255,255,0.08);
  padding: 12px 20px; border-radius: 10px; cursor: pointer;
  transition: all 0.3s; text-align: left; display: flex; flex-direction: column; gap: 4px;
}
.cat-btn:hover, .cat-btn.active {
  border-color: rgba(0,212,170,0.5);
  background: rgba(0,212,170,0.06);
  box-shadow: 0 0 20px rgba(0,212,170,0.08);
}
.cat-btn.active { box-shadow: 0 0 24px rgba(0,212,170,0.12); }
.cat-name { font-family: 'JetBrains Mono', monospace; font-size: 15px; color: #e8eaed; font-weight: 500; }
.cat-desc { font-size: 12px; color: #555d6b; }

.result { margin-top: 8px; }
.result-header { margin-bottom: 8px; }
.result-count { font-family: 'JetBrains Mono', monospace; font-size: 12px; color: #555d6b; }
</style>
