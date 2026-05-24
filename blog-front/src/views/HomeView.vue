<template>
  <div>
    <div class="hero" @mousemove="onMouseMove" ref="heroRef">
      <div class="hero-spot" :style="spotStyle"></div>
      <div class="hero-content">
        <h1 class="hero-title">Blog2026</h1>
        <p class="hero-subtitle">记录思考与生活</p>
      </div>
      <div class="hero-featured" v-if="featuredArticle"
        :style="{ transform: `perspective(1000px) rotateX(${tiltY}deg) rotateY(${tiltX}deg)` }"
        @mousemove.stop="onCardTilt"
        @mouseleave="resetTilt">
        <div class="featured-card">
          <div class="featured-cover" v-if="featuredArticle.cover">
            <img :src="featuredArticle.cover" :alt="featuredArticle.title" />
          </div>
          <div class="featured-cover placeholder" v-else>
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
          </div>
          <div class="featured-body">
            <span class="featured-label">最新文章</span>
            <h3 class="featured-title">{{ featuredArticle.title }}</h3>
            <p class="featured-summary">{{ featuredArticle.summary || '暂无摘要' }}</p>
          </div>
        </div>
      </div>
    </div>

    <div class="search-hint" v-if="keyword">
      <span>搜索 "{{ keyword }}" 的结果</span>
      <button class="clear-search" @click="keyword = ''; page = 1; fetchData(); router.replace({ path: '/' })">清除</button>
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
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticles } from '../api/index.js'
import ArticleCard from '../components/ArticleCard.vue'
import BackToTop from '../components/BackToTop.vue'

const route = useRoute()
const router = useRouter()
const articles = ref([])
const loading = ref(false)
const page = ref(1)
const size = 10
const total = ref(0)
const keyword = ref('')
const heroRef = ref(null)
const mousePos = ref({ x: 0.5, y: 0.5 })
const tiltX = ref(0)
const tiltY = ref(0)

const featuredArticle = computed(() => articles.value[0] || null)

const spotStyle = computed(() => ({
  '--spot-x': `${mousePos.value.x * 100}%`,
  '--spot-y': `${mousePos.value.y * 100}%`,
}))

function onMouseMove(e) {
  if (!heroRef.value) return
  const rect = heroRef.value.getBoundingClientRect()
  mousePos.value = {
    x: (e.clientX - rect.left) / rect.width,
    y: (e.clientY - rect.top) / rect.height,
  }
}

function onCardTilt(e) {
  const card = e.currentTarget
  const rect = card.getBoundingClientRect()
  const x = (e.clientX - rect.left) / rect.width - 0.5
  const y = (e.clientY - rect.top) / rect.height - 0.5
  tiltX.value = x * 8
  tiltY.value = y * -8
}

function resetTilt() {
  tiltX.value = 0
  tiltY.value = 0
}

onMounted(() => {
  keyword.value = route.query.keyword || ''
  fetchData()
})

watch(() => route.query.keyword, (val) => {
  keyword.value = val || ''
  page.value = 1
  fetchData()
})

async function fetchData() {
  loading.value = true
  try {
    const params = { page: page.value, size: size }
    if (keyword.value) params.keyword = keyword.value
    const res = await getArticles(params)
    articles.value = res.data.data.records
    total.value = res.data.data.total
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.hero {
  position: relative; text-align: center;
  padding: 72px 0 36px;
  overflow: hidden;
  border-radius: 20px;
  margin-bottom: 12px;
}

/* Mouse-follow glow spot */
.hero-spot {
  position: absolute; inset: 0; pointer-events: none; z-index: 0;
  background: radial-gradient(circle 300px at var(--spot-x, 50%) var(--spot-y, 50%), rgba(64,158,255,0.12) 0%, rgba(139,92,246,0.06) 40%, transparent 70%);
  transition: background 0.1s linear;
}

.hero-content { position: relative; z-index: 1; }

.hero-title {
  font-size: 52px; font-weight: 800;
  background: linear-gradient(135deg, #409EFF 0%, #8b5cf6 50%, #10b981 100%);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradientShift 4s ease infinite;
  letter-spacing: -1.5px; line-height: 1.15;
}

@keyframes gradientShift {
  0% { background-position: 0% center; }
  50% { background-position: 100% center; }
  100% { background-position: 0% center; }
}

.hero-subtitle {
  color: #909399; font-size: 17px; margin-top: 10px;
}

/* Featured article 3D card */
.hero-featured {
  margin: 40px auto 0; max-width: 480px;
  position: relative; z-index: 2;
  transition: transform 0.25s ease;
  will-change: transform;
}

.featured-card {
  display: flex; flex-direction: column;
  background: rgba(255,255,255,0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255,255,255,0.6);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 6px rgba(0,0,0,0.04), 0 8px 28px rgba(0,0,0,0.08);
  transition: box-shadow 0.3s;
}
.hero-featured:hover .featured-card {
  box-shadow: 0 4px 12px rgba(0,0,0,0.06), 0 16px 48px rgba(0,0,0,0.12);
}

.featured-cover {
  height: 140px; overflow: hidden;
}
.featured-cover img {
  width: 100%; height: 100%; object-fit: cover;
  transition: transform 0.6s ease;
}
.hero-featured:hover .featured-cover img { transform: scale(1.05); }

.featured-cover.placeholder {
  height: 100px;
  background: linear-gradient(135deg, #ecf5ff, #f0e6ff, #ecfdf5);
  display: flex; align-items: center; justify-content: center;
  color: #c0c4cc;
}

.featured-body { padding: 18px 24px 22px; text-align: left; }
.featured-label {
  font-size: 11px; color: #409EFF; font-weight: 600;
  text-transform: uppercase; letter-spacing: 0.5px;
}
.featured-title {
  font-size: 18px; font-weight: 700; color: #303133;
  margin-top: 6px;
  overflow: hidden; white-space: nowrap; text-overflow: ellipsis;
}
.featured-summary {
  font-size: 13px; color: #909399; margin-top: 6px;
  overflow: hidden; display: -webkit-box;
  -webkit-line-clamp: 2; -webkit-box-orient: vertical;
}

.search-hint {
  display: flex; align-items: center; justify-content: center; gap: 12px;
  padding: 12px 0 8px; font-size: 14px; color: #606266;
}
.clear-search {
  background: none; border: none; color: #409EFF; cursor: pointer;
  font-size: 12px; padding: 2px 8px; border-radius: 4px;
  transition: background 0.2s;
}
.clear-search:hover { background: #ecf5ff; }

.pagination { margin-top: 40px; display: flex; justify-content: center; }
.empty-state { margin-top: 40px; }

.list-enter-active { transition: all 0.4s ease; }
.list-enter-from { opacity: 0; transform: translateY(20px); }
</style>
