<template>
  <div class="tools-hub">
    <div class="hub-header">
      <h1 class="hub-title">小工具</h1>
      <p class="hub-subtitle">实用工具集合，持续更新中</p>
    </div>

    <div class="tools-grid">
      <div
        v-for="tool in tools"
        :key="tool.id"
        class="tool-card glass-card"
        @click="$router.push(`/tools/${tool.id}`)"
      >
        <div class="tool-icon">{{ tool.icon }}</div>
        <div class="tool-body">
          <h3 class="tool-name">{{ tool.name }}</h3>
          <p class="tool-desc">{{ tool.description }}</p>
          <div class="tool-tags">
            <span v-for="tag in tool.tags" :key="tag" class="tool-tag">{{ tag }}</span>
          </div>
        </div>
        <div class="tool-arrow">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
        </div>
      </div>
    </div>

    <n-empty v-if="tools.length === 0" description="更多工具即将上线" class="empty-state" />
  </div>
</template>

<script setup>
import { tools } from '../config/tools.js'
</script>

<style scoped>
.tools-hub { padding: 16px 0; }

.hub-header { text-align: center; padding: 24px 0 32px; }
.hub-title {
  font-size: 32px; font-weight: 700; color: #303133;
  background: linear-gradient(135deg, #409EFF, #8b5cf6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.hub-subtitle { color: #909399; font-size: 14px; margin-top: 8px; }

.tools-grid { display: flex; flex-direction: column; gap: 12px; }

.tool-card {
  display: flex; align-items: center; gap: 16px;
  padding: 20px 24px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.tool-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.06), 0 8px 28px rgba(0,0,0,0.08);
}

.tool-icon { font-size: 36px; flex-shrink: 0; width: 56px; text-align: center; }
.tool-body { flex: 1; min-width: 0; }
.tool-name { font-size: 17px; font-weight: 600; color: #303133; margin: 0 0 4px; }
.tool-desc { font-size: 13px; color: #909399; line-height: 1.5; }
.tool-tags { margin-top: 8px; display: flex; gap: 6px; }

.tool-tag {
  font-size: 11px; color: #409EFF;
  padding: 2px 8px; border-radius: 4px;
  background: #ecf5ff; border: 1px solid #d9ecff;
}

.tool-arrow { color: #c0c4cc; flex-shrink: 0; transition: all 0.2s; }
.tool-card:hover .tool-arrow { color: #409EFF; transform: translateX(3px); }

.empty-state { margin-top: 60px; }
</style>
