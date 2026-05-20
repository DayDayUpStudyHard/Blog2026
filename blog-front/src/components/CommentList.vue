<template>
  <div class="comments">
    <h3 class="section-title">
      <span class="title-icon">/*</span>
      留言 ({{ total }})
      <span class="title-icon">*/</span>
    </h3>
    <div v-if="comments.length" class="comment-list">
      <div v-for="c in comments" :key="c.id" class="comment-item">
        <div class="comment-avatar">{{ c.author.charAt(0) }}</div>
        <div class="comment-body">
          <div class="comment-head">
            <span class="author">{{ c.author }}</span>
            <span class="time">{{ formatDate(c.createTime) }}</span>
          </div>
          <p class="content">{{ c.content }}</p>
        </div>
      </div>
    </div>
    <div v-else class="empty">
      <span class="empty-icon">~$</span> 暂无留言，来发表第一条吧
    </div>

    <div class="comment-form">
      <h4><span class="title-icon">#</span> 发表留言</h4>
      <div class="form-row">
        <n-input v-model:value="form.author" placeholder="昵称" class="form-input" />
        <n-input v-model:value="form.email" placeholder="邮箱（选填）" class="form-input" />
      </div>
      <n-input v-model:value="form.content" type="textarea" :rows="3" placeholder="写下你的想法..." />
      <n-button @click="submit" :loading="submitting" class="submit-btn" :disabled="!form.author || !form.content">
        <template #icon>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
        </template>
        提交留言
      </n-button>
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
  } finally {
    submitting.value = false
  }
}

function formatDate(d) { return d ? d.substring(0, 16) : '' }
</script>

<style scoped>
.comments { margin-top: 56px; }
.section-title { font-family: 'JetBrains Mono', monospace; font-size: 16px; color: #e8eaed; margin-bottom: 20px; font-weight: 500; }
.title-icon { color: #00d4aa; font-size: 14px; margin: 0 4px; }

.comment-item { display: flex; gap: 14px; padding: 20px 0; border-bottom: 1px solid rgba(255,255,255,0.04); }
.comment-avatar {
  width: 40px; height: 40px; border-radius: 50%; flex-shrink: 0;
  background: linear-gradient(135deg, rgba(0,212,170,0.2), rgba(124,58,237,0.2));
  border: 1px solid rgba(0,212,170,0.2);
  display: flex; align-items: center; justify-content: center;
  color: #00d4aa; font-family: 'JetBrains Mono', monospace; font-size: 16px; font-weight: 600;
}
.comment-body { flex: 1; }
.comment-head { display: flex; gap: 12px; align-items: center; margin-bottom: 6px; }
.author { font-weight: 600; color: #00d4aa; font-size: 14px; font-family: 'JetBrains Mono', monospace; }
.time { color: #555d6b; font-size: 11px; font-family: 'JetBrains Mono', monospace; }
.content { color: #b0b8c4; font-size: 14px; line-height: 1.65; }

.empty { color: #555d6b; font-size: 14px; padding: 32px 0; text-align: center; font-family: 'JetBrains Mono', monospace; }
.empty-icon { color: #00d4aa; }

.comment-form { margin-top: 36px; }
.comment-form h4 { font-family: 'JetBrains Mono', monospace; font-size: 14px; color: #e8eaed; margin-bottom: 16px; font-weight: 500; }
.form-row { display: flex; gap: 12px; margin-bottom: 12px; }
.form-input { flex: 1; }

.submit-btn {
  margin-top: 14px;
  --n-color: rgba(0,212,170,0.1) !important;
  --n-color-hover: rgba(0,212,170,0.18) !important;
  --n-border: 1px solid rgba(0,212,170,0.35) !important;
  --n-border-hover: 1px solid rgba(0,212,170,0.6) !important;
  --n-text-color: #00d4aa !important;
  --n-text-color-hover: #00e5c0 !important;
  transition: all 0.3s;
}
.submit-btn:hover { box-shadow: 0 0 20px rgba(0,212,170,0.15); }
</style>
