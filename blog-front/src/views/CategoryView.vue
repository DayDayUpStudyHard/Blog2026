<template>
  <div class="category-page">
    <h2>分类</h2>
    <n-spin :show="loading">
      <div class="category-list" v-if="categories.length">
        <n-tag v-for="cat in categories" :key="cat.id" type="success" size="large" class="cat-tag"
               @click="selectCategory(cat.id)" :bordered="false">
          {{ cat.name }}
        </n-tag>
      </div>
      <n-empty v-else description="暂无分类" />
    </n-spin>

    <div v-if="articles.length" class="result">
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

onMounted(async () => {
  loading.value = true
  const res = await getCategories()
  categories.value = res.data.data
  loading.value = false
})

async function selectCategory(id) {
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
.category-page h2 { font-size: 24px; margin-bottom: 24px; }
.category-list { display: flex; gap: 12px; flex-wrap: wrap; margin-bottom: 32px; }
.cat-tag { cursor: pointer; font-size: 15px; padding: 4px 16px; }
.result { margin-top: 16px; }
</style>
