<template>
  <article class="card">
    <router-link :to="`/article/${article.id}`" class="title">{{ article.title }}</router-link>
    <p class="summary" v-if="article.summary">{{ article.summary }}</p>
    <div class="meta">
      <span>{{ formatDate(article.createTime) }}</span>
      <span>{{ article.viewCount }} 阅读</span>
      <n-tag v-if="article.isTop" type="success" size="small">置顶</n-tag>
    </div>
  </article>
</template>

<script setup>
defineProps({ article: Object })

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.substring(0, 10)
}
</script>

<style scoped>
.card { padding: 24px 0; border-bottom: 1px solid #eee; }
.title { font-size: 20px; font-weight: 600; color: #333; text-decoration: none; }
.title:hover { color: #5b8c5a; }
.summary { margin-top: 8px; color: #888; font-size: 14px; line-height: 1.6; }
.meta { margin-top: 12px; display: flex; gap: 16px; font-size: 13px; color: #bbb; align-items: center; }
</style>
