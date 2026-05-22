<template>
  <div class="comments">
    <div class="section-head">
      <h3 class="section-title">留言 <span class="title-count">({{ total }})</span></h3>
      <div class="section-line"></div>
    </div>

    <div v-if="comments.length" class="comment-list">
      <div v-for="(c, i) in comments" :key="c.id" class="comment-item" :style="{ '--i': i }">
        <div class="comment-avatar">
          <span class="avatar-text">{{ c.author.charAt(0) }}</span>
        </div>
        <div class="comment-body">
          <div class="comment-head">
            <span class="author">{{ c.author }}</span>
            <span class="time">{{ formatDate(c.createTime) }}</span>
          </div>
          <p class="content">{{ c.content }}</p>
        </div>
      </div>
    </div>
    <div v-else class="empty-state">暂无留言，来发表第一条吧</div>

    <div class="comment-form">
      <div class="form-head">
        <h4>发表留言</h4>
        <span class="form-hint">畅所欲言</span>
      </div>
      <div class="form-card">
        <div class="form-row">
          <n-input v-model:value="form.author" placeholder="昵称" class="form-input" :input-props="{ autocomplete: 'off' }" />
          <n-input v-model:value="form.email" placeholder="邮箱（选填）" class="form-input" :input-props="{ autocomplete: 'off' }" />
        </div>
        <n-input v-model:value="form.content" type="textarea" :rows="3" placeholder="写下你的想法..." />
        <n-button @click="submit" :loading="submitting" class="submit-btn" :disabled="!form.author || !form.content">
          <template #icon>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
          </template>
          发送
        </n-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { addComment } from '../api/index.js'

const props = defineProps({ articleId: Number, comments: Array, total: Number })
const emit = defineEmits(['refresh'])
const form = ref({ author: '', email: '', content: '' })
const submitting = ref(false)

async function submit() {
  if (!form.value.author || !form.value.content) return
  submitting.value = true
  try {
    await addComment(props.articleId, form.value)
    form.value = { author: '', email: '', content: '' }
    emit('refresh')
  } finally { submitting.value = false }
}

function formatDate(d) { return d ? d.substring(0, 16) : '' }
</script>

<style scoped>
.comments { margin-top: 56px; }

.section-head { margin-bottom: 20px; }
.section-title { font-size: 17px; color: #303133; margin-bottom: 10px; font-weight: 600; }
.title-count { color: #909399; font-size: 14px; font-weight: 400; }
.section-line { height: 1px; background: linear-gradient(90deg, #e8ecf0, #d0d5dd, #e8ecf0); }

.comment-item {
  display: flex; gap: 14px; padding: 18px 0;
  border-bottom: 1px solid #f0f2f5;
  animation: commentEnter 0.3s ease forwards;
  animation-delay: calc(var(--i) * 0.05s);
  opacity: 0;
}
.comment-avatar {
  width: 40px; height: 40px; border-radius: 10px; flex-shrink: 0;
  background: linear-gradient(135deg, #ecf5ff, #d9ecff);
  display: flex; align-items: center; justify-content: center;
}
.avatar-text { color: #409EFF; font-size: 16px; font-weight: 700; }
.comment-body { flex: 1; }
.comment-head { display: flex; gap: 12px; align-items: center; margin-bottom: 6px; }
.author { font-weight: 600; color: #409EFF; font-size: 14px; }
.time { color: #c0c4cc; font-size: 12px; }
.content { color: #606266; font-size: 14px; line-height: 1.7; }

.empty-state {
  color: #c0c4cc; font-size: 14px; padding: 32px 0; text-align: center;
}

.comment-form { margin-top: 40px; }
.form-head { display: flex; align-items: center; gap: 10px; margin-bottom: 14px; }
.form-head h4 { font-size: 15px; color: #303133; font-weight: 600; margin: 0; }
.form-hint { font-size: 12px; color: #c0c4cc; }
.form-card {
  background: rgba(255,255,255,0.6);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255,255,255,0.5);
  border-radius: 12px; padding: 20px;
}
.form-row { display: flex; gap: 12px; margin-bottom: 14px; }
.form-input { flex: 1; }

.submit-btn {
  margin-top: 14px;
  --n-color: #ecf5ff !important;
  --n-color-hover: #d9ecff !important;
  --n-border: 1px solid #409EFF !important;
  --n-border-hover: 1px solid #66b1ff !important;
  --n-text-color: #409EFF !important;
  --n-text-color-hover: #409EFF !important;
  font-size: 12px !important;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(64,158,255,0.12);
}
.submit-btn:hover {
  box-shadow: 0 4px 14px rgba(64,158,255,0.2);
  transform: translateY(-1px);
}

@keyframes commentEnter {
  0% { opacity: 0; transform: translateX(-6px); }
  100% { opacity: 1; transform: translateX(0); }
}
</style>
