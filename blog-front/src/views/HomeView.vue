<template>
  <div>
    <!-- Hero -->
    <div class="hero">
      <div class="hero-ring hero-ring-1"></div>
      <div class="hero-ring hero-ring-2"></div>
      <div class="hero-badge">
        <span class="badge-dot"></span>
        v2.0.1
      </div>
      <h1 class="hero-title">
        <span class="hero-line">
          Blog<span class="hero-accent">2026</span>
        </span>
      </h1>
      <p class="hero-subtitle">
        <span class="cursor-blk">▋</span>
        记录思考与生活
      </p>
      <div class="hero-divider">
        <span class="divider-icon">▲</span>
      </div>
    </div>

    <!-- Article List -->
    <n-spin :show="loading">
      <transition-group name="list" tag="div">
        <ArticleCard v-for="(article, i) in articles" :key="article.id" :article="article" :style="{ '--i': i }" />
      </transition-group>
    </n-spin>

    <div class="pagination" v-if="total > size">
      <n-pagination v-model:page="page" :page-size="size" :item-count="total" @update:page="fetchData" />
    </div>

    <n-empty v-if="!loading && articles.length === 0" description="暂无文章" class="empty-state" />
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
    const res = await getArticles({ page: page.value, size: size })
    articles.value = res.data.data.records
    total.value = res.data.data.total
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ═══ Hero ═══ */
.hero {
  text-align: center; padding: 64px 0 36px; position: relative;
}
.hero-ring {
  position: absolute; left: 50%; top: 50%; transform: translate(-50%, -50%);
  border-radius: 50%; border: 1px solid rgba(0,212,170,0.1); pointer-events: none;
}
.hero-ring-1 { width: 260px; height: 260px; animation: ringSpin 20s linear infinite; }
.hero-ring-2 { width: 200px; height: 200px; border-color: rgba(124,58,237,0.08); animation: ringSpin 15s linear infinite reverse; }

.hero-badge {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 4px 14px; border-radius: 20px; font-size: 11px;
  font-family: 'JetBrains Mono', monospace; color: #00d4aa;
  border: 1px solid rgba(0,212,170,0.25); background: rgba(0,212,170,0.05);
  margin-bottom: 24px; letter-spacing: 1px;
}
.badge-dot {
  width: 5px; height: 5px; border-radius: 50%; background: #00d4aa;
  box-shadow: 0 0 6px rgba(0,212,170,0.5);
}

.hero-title {
  font-size: 56px; font-weight: 700; margin: 0 0 16px;
  font-family: 'JetBrains Mono', monospace; letter-spacing: 2px;
  position: relative;
}
.hero-line {
  background: linear-gradient(135deg, #e8eaed 30%, #00d4aa 70%, #7c3aed 100%);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  display: inline-block;
}

.hero-subtitle {
  color: #6e7687; font-size: 17px; font-family: 'JetBrains Mono', monospace;
  display: flex; align-items: center; justify-content: center; gap: 10px;
}
.cursor-blk { color: #00d4aa; font-size: 14px; animation: blink 1s step-end infinite; }

.hero-divider {
  margin-top: 36px; position: relative;
}
.divider-icon {
  color: #00d4aa; font-size: 10px; opacity: 0.4;
  animation: neonPulse 2s ease-in-out infinite;
}

/* ═══ Pagination ═══ */
.pagination { margin-top: 44px; display: flex; justify-content: center; }
.empty-state { margin-top: 40px; }

/* ═══ List Transition ═══ */
.list-enter-active { transition: all 0.45s ease; }
.list-enter-from { opacity: 0; transform: translateY(24px) scale(0.96); }

/* ═══ Keyframes ═══ */
@keyframes ringSpin {
  0% { transform: translate(-50%, -50%) rotate(0deg); }
  100% { transform: translate(-50%, -50%) rotate(360deg); }
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}
@keyframes neonPulse {
  0%, 100% { opacity: 0.4; }
  50% { opacity: 1; }
}
</style>
