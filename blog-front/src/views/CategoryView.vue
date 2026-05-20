<template>
  <div class="category-page">
    <div class="page-head">
      <h2 class="page-title">
        <span class="title-icon">##</span> 分类浏览
      </h2>
      <div class="page-line"></div>
    </div>

    <n-spin :show="loading">
      <div class="category-grid" v-if="categories.length">
        <button
          v-for="cat in categories" :key="cat.id"
          class="cat-btn" :class="{ active: activeCat === cat.id }"
          @click="selectCategory(cat.id)"
        >
          <div class="cat-glow"></div>
          <div class="cat-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>
          </div>
          <div class="cat-info">
            <span class="cat-name">{{ cat.name }}</span>
            <span class="cat-desc" v-if="cat.description">{{ cat.description }}</span>
          </div>
          <span class="cat-arrow">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
          </span>
        </button>
      </div>
      <n-empty v-else description="暂无分类" />
    </n-spin>

    <div v-if="articles.length" class="result">
      <div class="result-head">
        <span class="result-prompt">$</span>
        <span class="result-count">找到 {{ articles.length }} 篇文章</span>
      </div>
      <ArticleCard v-for="(article, i) in articles" :key="article.id" :article="article" :style="{ '--i': i }" />
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
  categories.value = (await getCategories()).data.data
  loading.value = false
})

async function selectCategory(id) {
  activeCat.value = id
  loading.value = true
  try {
    articles.value = (await getArticles({ categoryId: id, size: 50 })).data.data.records
  } finally { loading.value = false }
}
</script>

<style scoped>
.category-page { padding: 32px 0; }
.page-head { margin-bottom: 28px; }
.page-title { font-family: 'JetBrains Mono', monospace; font-size: 22px; color: #e8eaed; margin-bottom: 12px; font-weight: 600; }
.title-icon { color: #00d4aa; margin-right: 8px; }
.page-line { height: 1px; background: linear-gradient(90deg, rgba(0,212,170,0.25), transparent); }

.category-grid { display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 36px; }
.cat-btn {
  position: relative; display: flex; align-items: center; gap: 12px;
  background: rgba(26,39,56,0.6); border: 1px solid rgba(255,255,255,0.05);
  padding: 14px 18px; border-radius: 12px; cursor: pointer;
  transition: all 0.3s; text-align: left; overflow: hidden;
  backdrop-filter: blur(8px);
}
.cat-btn:hover, .cat-btn.active {
  border-color: rgba(0,212,170,0.4);
  background: rgba(0,212,170,0.08);
  box-shadow: 0 0 24px rgba(0,212,170,0.1);
  transform: translateY(-2px);
}
.cat-btn.active {
  border-color: rgba(0,212,170,0.6);
  box-shadow: 0 0 30px rgba(0,212,170,0.15);
}
.cat-glow {
  position: absolute; inset: -1px; border-radius: 12px; padding: 1px;
  background: linear-gradient(135deg, transparent, rgba(0,212,170,0.1), transparent);
  opacity: 0; transition: opacity 0.3s;
}
.cat-btn.active .cat-glow { opacity: 1; }

.cat-icon { color: #6e7687; transition: color 0.3s; flex-shrink: 0; }
.cat-btn.active .cat-icon { color: #00d4aa; }
.cat-info { display: flex; flex-direction: column; gap: 2px; }
.cat-name { font-family: 'JetBrains Mono', monospace; font-size: 15px; color: #e8eaed; font-weight: 500; }
.cat-desc { font-size: 12px; color: #6e7687; }
.cat-arrow {
  color: #4a5060; transition: all 0.3s; display: flex; margin-left: auto;
}
.cat-btn.active .cat-arrow { color: #00d4aa; transform: translateX(2px); }

/* Results */
.result { margin-top: 8px; }
.result-head { margin-bottom: 12px; display: flex; gap: 8px; align-items: center; }
.result-prompt { color: #00d4aa; font-family: 'JetBrains Mono', monospace; font-size: 13px; }
.result-count { font-family: 'JetBrains Mono', monospace; font-size: 12px; color: #6e7687; }
</style>
