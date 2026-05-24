<template>
  <div class="tool-runner">
    <div class="runner-topbar">
      <button class="back-btn" @click="$router.push('/tools')">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"/></svg>
        <span>返回工具中心</span>
      </button>
      <span class="runner-title">{{ tool?.name || '加载中...' }}</span>
      <div class="runner-spacer"></div>
    </div>

    <div v-if="tool" class="iframe-wrap">
      <div class="loading-overlay" v-if="iframeLoading">
        <n-spin :show="true" />
      </div>
      <iframe
        :src="tool.devUrl"
        class="tool-iframe"
        ref="iframeRef"
        @load="iframeLoading = false"
        frameborder="0"
      ></iframe>
    </div>

    <n-empty v-else description="工具不存在" class="empty-state" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { tools } from '../config/tools.js'

const route = useRoute()
const iframeRef = ref(null)
const iframeLoading = ref(true)

const tool = computed(() => tools.find(t => t.id === route.params.toolId))
</script>

<style scoped>
.tool-runner {
  display: flex; flex-direction: column;
  height: calc(100vh - 120px);
}

.runner-topbar {
  display: flex; align-items: center; gap: 12px;
  padding: 0 0 16px;
  flex-shrink: 0;
}
.back-btn {
  display: flex; align-items: center; gap: 4px;
  background: none; border: 1px solid #e4e7ed; border-radius: 6px;
  padding: 6px 12px; font-size: 12px; color: #606266; cursor: pointer;
  transition: all 0.2s; font-family: inherit;
}
.back-btn:hover { color: #409EFF; border-color: #409EFF; background: #f0f7ff; }
.runner-title { font-size: 15px; font-weight: 600; color: #303133; }
.runner-spacer { flex: 1; }

.iframe-wrap {
  flex: 1; position: relative; border-radius: 12px; overflow: hidden;
  border: 1px solid #e8ecf0;
  background: #fff;
}
.loading-overlay {
  position: absolute; inset: 0; z-index: 2;
  display: flex; align-items: center; justify-content: center;
  background: rgba(255,255,255,0.9);
}
.tool-iframe {
  width: 100%; height: 100%; border: none;
}
.empty-state { margin-top: 60px; }
</style>
