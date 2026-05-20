<template>
  <div class="comments">
    <!-- Section title -->
    <div class="section-head">
      <h3 class="section-title">
        <span class="title-icon">/*</span>
        留言
        <span class="title-count">({{ total }})</span>
        <span class="title-icon">*/</span>
      </h3>
      <div class="section-line"></div>
    </div>

    <!-- Comment list -->
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
    <div v-else class="empty-state">
      <span class="empty-prompt">~$</span> 暂无留言，来发表第一条吧
    </div>

    <!-- Comment form -->
    <div class="comment-form">
      <div class="form-head">
        <h4><span class="title-icon">#</span> 发表留言</h4>
        <span class="form-hint">// 畅所欲言</span>
      </div>
      <div class="form-card">
        <div class="form-row">
          <n-input v-model:value="form.author" placeholder="昵称" class="form-input" :input-props="{ autocomplete: 'off' }" />
          <n-input v-model:value="form.email" placeholder="邮箱（选填）" class="form-input" :input-props="{ autocomplete: 'off' }" />
        </div>
        <n-input v-model:value="form.content" type="textarea" :rows="3" placeholder="写下你的想法..." />
        <n-button @click="submit" :loading="submitting" class="submit-btn" :disabled="!form.author || !form.content">
          <template #icon>
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
          </template>
          SEND
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

/* Section head */
.section-head { margin-bottom: 24px; }
.section-title { font-family: 'JetBrains Mono', monospace; font-size: 16px; color: #e8eaed; margin-bottom: 12px; font-weight: 500; }
.title-icon { color: #00d4aa; font-size: 14px; margin: 0 5px; }
.title-count { color: #6e7687; font-size: 14px; }
.section-line { height: 1px; background: linear-gradient(90deg, rgba(0,212,170,0.3), transparent); }

/* Comment items */
.comment-item {
  display: flex; gap: 14px; padding: 20px 0;
  border-bottom: 1px solid rgba(255,255,255,0.05);
  animation: commentEnter 0.4s ease forwards;
  animation-delay: calc(var(--i) * 0.06s);
  opacity: 0;
}
.comment-avatar {
  width: 42px; height: 42px; border-radius: 12px; flex-shrink: 0;
  background: linear-gradient(135deg, rgba(0,212,170,0.2), rgba(124,58,237,0.15));
  border: 1px solid rgba(0,212,170,0.2);
  display: flex; align-items: center; justify-content: center;
  position: relative; overflow: hidden;
}
.avatar-text {
  color: #00d4aa; font-family: 'JetBrains Mono', monospace; font-size: 16px; font-weight: 700;
}
.comment-body { flex: 1; }
.comment-head { display: flex; gap: 12px; align-items: center; margin-bottom: 6px; }
.author {
  font-weight: 600; color: #00d4aa; font-size: 14px; font-family: 'JetBrains Mono', monospace;
}
.time { color: #6e7687; font-size: 11px; font-family: 'JetBrains Mono', monospace; }
.content { color: #b0b8c4; font-size: 14px; line-height: 1.7; }

/* Empty */
.empty-state {
  color: #5a6070; font-size: 14px; padding: 32px 0; text-align: center;
  font-family: 'JetBrains Mono', monospace;
}
.empty-prompt { color: #00d4aa; margin-right: 6px; }

/* Form */
.comment-form { margin-top: 44px; }
.form-head { display: flex; align-items: center; gap: 10px; margin-bottom: 16px; }
.form-head h4 { font-family: 'JetBrains Mono', monospace; font-size: 14px; color: #e8eaed; font-weight: 500; margin: 0; }
.form-hint { font-family: 'JetBrains Mono', monospace; font-size: 11px; color: #6e7687; }
.form-card {
  background: rgba(26,39,56,0.4); border: 1px solid rgba(255,255,255,0.04);
  border-radius: 12px; padding: 20px;
  backdrop-filter: blur(8px);
}
.form-row { display: flex; gap: 12px; margin-bottom: 14px; }
.form-input { flex: 1; }

.submit-btn {
  margin-top: 16px;
  --n-color: rgba(0,212,170,0.1) !important;
  --n-color-hover: rgba(0,212,170,0.18) !important;
  --n-border: 1px solid rgba(0,212,170,0.35) !important;
  --n-border-hover: 1px solid rgba(0,212,170,0.6) !important;
  --n-text-color: #00d4aa !important;
  --n-text-color-hover: #00e5c0 !important;
  font-family: 'JetBrains Mono', monospace !important;
  font-size: 12px !important; letter-spacing: 1px;
  transition: all 0.3s;
}
.submit-btn:hover { box-shadow: 0 0 24px rgba(0,212,170,0.18); transform: translateY(-1px); }

@keyframes commentEnter {
  0% { opacity: 0; transform: translateX(-8px); }
  100% { opacity: 1; transform: translateX(0); }
}
</style>
