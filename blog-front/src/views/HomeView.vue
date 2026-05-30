<template>
  <div>
    <div class="hero" @mousemove="onMouseMove" ref="heroRef">
      <div class="hero-spot" :style="spotStyle"></div>
      <div class="hero-content">
        <n-avatar v-if="siteInfo.avatar" :src="siteInfo.avatar" :size="72" class="hero-avatar" />
        <n-avatar v-else :size="72" class="hero-avatar" :style="{ background: 'linear-gradient(135deg, #409EFF, #8b5cf6)' }">
          <span style="font-size:28px;font-weight:700">{{ (siteInfo.nickname || 'B').charAt(0) }}</span>
        </n-avatar>
        <h1 class="hero-title">{{ siteInfo.nickname || 'Blog2026' }}</h1>
        <p class="hero-subtitle">{{ siteInfo.bio || '记录思考与生活' }}</p>
        <div class="hero-social" v-if="socialLinks.length > 0">
          <a v-for="(link, i) in socialLinks" :key="i" :href="link.url" :title="link.name" target="_blank" class="social-link">
            <svg v-if="link.icon === 'github'" width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M12 0C5.37 0 0 5.37 0 12c0 5.3 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61-.546-1.385-1.335-1.755-1.335-1.755-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.605-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 21.795 24 17.295 24 12 24 5.37 18.63 0 12 0z"/></svg>
            <svg v-else-if="link.icon === 'email'" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>
            <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/><polyline points="15 3 21 3 21 9"/><line x1="10" y1="14" x2="21" y2="3"/></svg>
          </a>
        </div>
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

    <div class="moments-side" v-if="recentMoments.length > 0">
      <div class="moments-head">
        <router-link to="/moments" class="moments-title-link">最新说说</router-link>
      </div>
      <div class="moments-list">
        <div v-for="m in recentMoments" :key="m.id" class="moment-card">
          <p class="moment-content">{{ m.content }}</p>
          <span class="moment-time">{{ formatDate(m.createTime) }}</span>
        </div>
      </div>
    </div>

    <BackToTop />
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticles, getSiteInfo, getMoments } from '../api/index.js'
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
const siteInfo = ref({})
const socialLinks = ref([])
const recentMoments = ref([])

const featuredArticle = computed(() => articles.value[0] || null)

const spotStyle = computed(() => ({
  '--spot-x': `${mousePos.value.x * 100}%`,
  '--spot-y': `${mousePos.value.y * 100}%`,
}))

function formatDate(t) {
  if (!t) return ''
  return t.substring(0, 10)
}

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
  fetchSiteInfo()
  fetchMoments()
})

watch(() => route.query.keyword, (val) => {
  keyword.value = val || ''
  page.value = 1
  fetchData()
})

async function fetchSiteInfo() {
  try {
    const res = await getSiteInfo()
    siteInfo.value = res.data.data
    try { socialLinks.value = JSON.parse(res.data.data.socialLinks || '[]') } catch { socialLinks.value = [] }
  } catch {}
}

async function fetchMoments() {
  try {
    const res = await getMoments({ page: 1, size: 3 })
    recentMoments.value = res.data.data.records
  } catch {}
}

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

.hero-spot {
  position: absolute; inset: 0; pointer-events: none; z-index: 0;
  background: radial-gradient(circle 300px at var(--spot-x, 50%) var(--spot-y, 50%), rgba(64,158,255,0.12) 0%, rgba(139,92,246,0.06) 40%, transparent 70%);
  transition: background 0.1s linear;
}

.hero-content { position: relative; z-index: 1; }

.hero-avatar { margin-bottom: 14px; box-shadow: 0 4px 16px rgba(64,158,255,0.2); }

.hero-title {
  font-size: 36px; font-weight: 800;
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
  color: #909399; font-size: 16px; margin-top: 8px;
}

.hero-social { display: flex; gap: 10px; justify-content: center; margin-top: 14px; }
.social-link {
  color: #909399; transition: all 0.2s;
  display: flex; align-items: center; justify-content: center;
  width: 34px; height: 34px; border-radius: 8px;
  background: rgba(255,255,255,0.6);
  border: 1px solid #e8ecf0;
}
.social-link:hover { color: #409EFF; border-color: #409EFF; transform: translateY(-2px); }

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
.hero-featured {
  margin: 40px auto 0; max-width: 480px;
  position: relative; z-index: 2;
  transition: transform 0.25s ease;
  will-change: transform;
}
.hero-featured:hover .featured-card {
  box-shadow: 0 4px 12px rgba(0,0,0,0.06), 0 16px 48px rgba(0,0,0,0.12);
}
.featured-cover { height: 140px; overflow: hidden; }
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

.moments-side {
  margin-top: 48px;
  background: rgba(255,255,255,0.55);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255,255,255,0.5);
  border-radius: 14px; padding: 20px 24px;
}
.moments-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.moments-title-link { font-size: 16px; font-weight: 600; color: #303133; text-decoration: none; }
.moments-title-link:hover { color: #409EFF; }
.moments-list { display: flex; flex-direction: column; gap: 10px; }
.moment-card {
  padding: 12px 14px; border-radius: 10px;
  background: rgba(255,255,255,0.5); border: 1px solid #f0f2f5;
  transition: all 0.2s;
}
.moment-card:hover { background: #fff; border-color: #e0e4ea; }
.moment-content { font-size: 14px; color: #303133; line-height: 1.6; margin: 0; }
.moment-time { font-size: 12px; color: #c0c4cc; margin-top: 6px; display: block; }
</style>
