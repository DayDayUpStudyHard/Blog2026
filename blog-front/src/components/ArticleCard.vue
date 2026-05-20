<template>
  <article class="card" @mouseenter="hover = true" @mouseleave="hover = false">
    <div class="card-inner">
      <!-- Left glow bar -->
      <div class="card-glow-bar" :class="{ active: hover }"></div>

      <div class="card-body">
        <!-- Tags row -->
        <div class="card-tags">
          <span v-if="article.isTop" class="top-badge">
            <span class="top-dot"></span> PINNED
          </span>
        </div>

        <!-- Title -->
        <router-link :to="`/article/${article.id}`" class="title">
          {{ article.title }}
        </router-link>

        <!-- Summary -->
        <p class="summary" v-if="article.summary">{{ article.summary }}</p>

        <!-- Meta -->
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
          <span class="meta-arrow" :class="{ active: hover }">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
          </span>
        </div>
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
  position: relative; margin-bottom: 2px;
  animation: cardEnter 0.5s ease forwards;
  animation-delay: calc(var(--i, 0) * 0.05s);
  opacity: 0;
}
.card-inner {
  position: relative; padding: 28px 24px; border-radius: 12px;
  background: transparent;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}
.card-inner:hover {
  background: rgba(26,39,56,0.6);
  border-color: rgba(255,255,255,0.06);
  backdrop-filter: blur(12px);
}

/* Glow bar */
.card-glow-bar {
  position: absolute; left: 0; top: 12px; bottom: 12px; width: 2px;
  background: linear-gradient(180deg, transparent, #00d4aa, #7c3aed, transparent);
  border-radius: 1px; opacity: 0; transition: all 0.35s;
}
.card-glow-bar.active { opacity: 1; box-shadow: 0 0 16px rgba(0,212,170,0.3), 0 0 32px rgba(0,212,170,0.1); }

.card-body { position: relative; }

/* Tags */
.card-tags { margin-bottom: 10px; }
.top-badge {
  display: inline-flex; align-items: center; gap: 5px;
  font-family: 'JetBrains Mono', monospace; font-size: 9px; letter-spacing: 1px;
  color: #7c3aed; padding: 2px 8px; border-radius: 4px;
  background: rgba(124,58,237,0.08); border: 1px solid rgba(124,58,237,0.2);
}
.top-dot {
  width: 4px; height: 4px; border-radius: 50%; background: #7c3aed;
  box-shadow: 0 0 6px rgba(124,58,237,0.5);
}

/* Title */
.title {
  font-size: 21px; font-weight: 600; color: #e8eaed; text-decoration: none;
  font-family: 'JetBrains Mono', monospace;
  transition: color 0.25s; display: inline-block; line-height: 1.4;
  background: linear-gradient(to right, #e8eaed 50%, #00d4aa 50%);
  background-size: 200% 100%; background-position: 0 0;
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
}
.card-inner:hover .title {
  background-position: -100% 0;
}

/* Summary */
.summary {
  margin-top: 12px; color: #8b949e; font-size: 14px; line-height: 1.7;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
}

/* Meta */
.meta {
  margin-top: 16px; display: flex; align-items: center; gap: 10px;
  font-size: 12px; color: #6e7687; font-family: 'JetBrains Mono', monospace;
}
.meta-item { display: flex; align-items: center; gap: 5px; transition: color 0.2s; }
.card-inner:hover .meta-item { color: #7d8790; }
.meta-divider { width: 3px; height: 3px; border-radius: 50%; background: #3a4555; }
.meta-arrow {
  margin-left: auto; color: #4a5060; transition: all 0.35s; display: flex;
}
.meta-arrow.active { color: #00d4aa; transform: translateX(4px); }

/* Divider between cards */
.card:not(:last-child)::after {
  content: '';
  position: absolute; bottom: 0; left: 20px; right: 20px; height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.06), transparent);
}

@keyframes cardEnter {
  0% { opacity: 0; transform: translateY(16px); }
  100% { opacity: 1; transform: translateY(0); }
}
</style>
