<template>
  <article class="card" @mouseenter="hover = true" @mouseleave="hover = false">
    <div class="card-inner" :class="{ hover }">
      <div class="card-cover" v-if="article.cover">
        <img :src="article.cover" :alt="article.title" />
      </div>
      <div class="card-cover placeholder" v-else>
        <div class="cover-gradient" :style="{ background: gradients[i % gradients.length] }">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.7)" stroke-width="1.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
        </div>
      </div>
      <div class="card-body">
        <div class="card-tags">
          <span v-if="article.isTop" class="top-badge">置顶</span>
          <span v-for="tag in article.tags" :key="tag.id" class="tag-dot">#{{ tag.name }}</span>
        </div>

        <router-link :to="`/article/${article.id}`" class="title">
          {{ article.title }}
        </router-link>

        <p class="summary" v-if="article.summary">{{ article.summary }}</p>

        <div class="meta">
          <span class="meta-item">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
            {{ formatDate(article.createTime) }}
          </span>
          <span class="meta-divider"></span>
          <span class="meta-item">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
            {{ article.viewCount }}
          </span>
          <span class="meta-divider"></span>
          <span class="meta-item" v-if="readingTime">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            {{ readingTime }}
          </span>
          <span class="meta-arrow" :class="{ active: hover }">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
          </span>
        </div>
      </div>
    </div>
  </article>
</template>

<script setup>
import { ref, computed } from 'vue'
const props = defineProps({ article: Object })
const hover = ref(false)

const gradients = [
  'linear-gradient(135deg, #667eea, #764ba2)',
  'linear-gradient(135deg, #f093fb, #f5576c)',
  'linear-gradient(135deg, #4facfe, #00f2fe)',
  'linear-gradient(135deg, #43e97b, #38f9d7)',
  'linear-gradient(135deg, #fa709a, #fee140)',
  'linear-gradient(135deg, #a18cd1, #fbc2eb)',
]
const i = computed(() => (props.article?.id || 0) % gradients.length)

const readingTime = computed(() => {
  const text = props.article?.content || ''
  if (!text) return ''
  const minutes = Math.max(1, Math.ceil(text.length / 400))
  return `约 ${minutes} 分钟`
})

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.substring(0, 10)
}
</script>

<style scoped>
.card {
  margin-bottom: 2px;
  animation: cardEnter 0.4s ease forwards;
  animation-delay: calc(var(--i, 0) * 0.04s);
  opacity: 0;
}
.card-inner {
  padding: 24px 20px; border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.card-inner.hover {
  background: rgba(255,255,255,0.75);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255,255,255,0.5);
  box-shadow: 0 2px 4px rgba(0,0,0,0.04), 0 8px 24px rgba(0,0,0,0.06);
  transform: translateY(-2px);
}

.card-body { position: relative; }

.card-cover {
  height: 160px; overflow: hidden; border-radius: 10px;
  margin-bottom: 16px;
}
.card-cover img {
  width: 100%; height: 100%; object-fit: cover;
  transition: transform 0.5s ease;
}
.card-inner.hover .card-cover img { transform: scale(1.04); }

.card-cover.placeholder { height: 100px; }
.cover-gradient {
  width: 100%; height: 100%; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
}

.card-tags { margin-bottom: 8px; }
.top-badge {
  display: inline-flex; align-items: center; gap: 4px;
  font-size: 10px; font-weight: 500;
  color: #d97706; padding: 2px 8px; border-radius: 4px;
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  border: 1px solid #fcd34d;
}
.tag-dot {
  font-size: 11px; color: #409EFF; font-weight: 400;
}

.title {
  font-size: 20px; font-weight: 600; color: #303133; text-decoration: none;
  transition: all 0.3s; line-height: 1.4;
}
.card-inner.hover .title {
  background: linear-gradient(135deg, #409EFF, #10b981);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.summary {
  margin-top: 10px; color: #909399; font-size: 14px; line-height: 1.7;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
}

.meta {
  margin-top: 14px; display: flex; align-items: center; gap: 10px;
  font-size: 12px; color: #909399;
}
.meta-item { display: flex; align-items: center; gap: 5px; transition: color 0.2s; }
.meta-divider { width: 3px; height: 3px; border-radius: 50%; background: #d0d5dd; }
.meta-arrow {
  margin-left: auto; color: #c0c4cc; transition: all 0.25s; display: flex;
}
.meta-arrow.active { color: #409EFF; transform: translateX(3px); }

.card:not(:last-child)::after {
  content: '';
  position: absolute; bottom: 0; left: 20px; right: 20px; height: 1px;
  background: linear-gradient(90deg, transparent, #e8ecf0 20%, #e8ecf0 80%, transparent);
}

@keyframes cardEnter {
  0% { opacity: 0; transform: translateY(12px); }
  100% { opacity: 1; transform: translateY(0); }
}
</style>
