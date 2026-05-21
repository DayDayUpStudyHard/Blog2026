<template>
  <transition name="fade">
    <button v-show="visible" class="back-to-top" @click="scrollToTop" title="回到顶部">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <polyline points="18 15 12 9 6 15"/>
      </svg>
    </button>
  </transition>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const visible = ref(false)

function onScroll() {
  visible.value = window.scrollY > 400
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.back-to-top {
  position: fixed; bottom: 32px; right: 32px; z-index: 100;
  width: 40px; height: 40px; border-radius: 50%;
  border: 1px solid rgba(64,158,255,0.2);
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  color: #409EFF; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06), 0 4px 16px rgba(0,0,0,0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.back-to-top:hover {
  transform: translateY(-2px);
  background: rgba(64,158,255,0.08);
  box-shadow: 0 4px 12px rgba(64,158,255,0.15), 0 8px 24px rgba(0,0,0,0.08);
}

.fade-enter-active, .fade-leave-active { transition: all 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(8px); }
</style>
