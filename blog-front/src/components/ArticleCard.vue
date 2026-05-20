<template>
  <article class="card" @mouseenter="hover = true" @mouseleave="hover = false">
    <div class="card-glow" :class="{ active: hover }"></div>
    <div class="card-top-line"></div>
    <div class="card-body">
      <n-tag v-if="article.isTop" size="small" :bordered="false" class="top-tag">置顶</n-tag>
      <router-link :to="`/article/${article.id}`" class="title">{{ article.title }}</router-link>
      <p class="summary" v-if="article.summary">{{ article.summary }}</p>
      <div class="meta">
        <span class="meta-item">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          {{ formatDate(article.createTime) }}
        </span>
        <span class="meta-item">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
          {{ article.viewCount }} 阅读
        </span>
      </div>
    </div>
  </article>
</template>

<script setup>
import { ref } from 'vue'
defineProps({ article: Object })

const hover = ref(false)

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.substring(0, 10)
}
</script>

<style scoped>
.card {
  position: relative;
  padding: 28px 0;
  border-bottom: 1px solid rgba(255,255,255,0.05);
  transition: transform 0.3s ease;
}
.card:hover { transform: translateX(4px); }

.card-glow {
  position: absolute; left: -2px; top: 0; bottom: 0; width: 2px;
  background: #00d4aa; opacity: 0; transition: opacity 0.3s, box-shadow 0.3s;
  border-radius: 1px;
}
.card-glow.active { opacity: 1; box-shadow: 0 0 20px rgba(0,212,170,0.3); }

.card-top-line {
  position: absolute; top: 0; left: 0; right: 0; height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0,212,170,0.1), transparent);
  opacity: 0; transition: opacity 0.3s;
}
.card:hover .card-top-line { opacity: 1; }

.card-body { position: relative; }

.top-tag {
  position: absolute; right: 0; top: 0;
  --n-text-color: #7c3aed !important;
  --n-border: 1px solid rgba(124,58,237,0.4) !important;
}

.title {
  font-size: 20px; font-weight: 600; color: #e8eaed; text-decoration: none;
  font-family: 'JetBrains Mono', monospace;
  transition: color 0.2s; display: inline-block;
}
.title:hover { color: #00d4aa; }

.summary {
  margin-top: 10px; color: #8b949e; font-size: 14px; line-height: 1.65;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
}

.meta { margin-top: 14px; display: flex; gap: 18px; font-size: 12px; color: #555d6b; align-items: center; font-family: 'JetBrains Mono', monospace; }
.meta-item { display: flex; align-items: center; gap: 5px; }
</style>
