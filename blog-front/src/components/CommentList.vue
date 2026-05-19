<template>
  <div class="comments">
    <h3>留言 ({{ total }})</h3>
    <div v-if="comments.length" class="comment-list">
      <div v-for="c in comments" :key="c.id" class="comment-item">
        <div class="comment-head">
          <span class="author">{{ c.author }}</span>
          <span class="time">{{ formatDate(c.createTime) }}</span>
        </div>
        <p class="content">{{ c.content }}</p>
      </div>
    </div>
    <div v-else class="empty">暂无留言</div>

    <div class="comment-form">
      <h4>发表留言</h4>
      <n-input v-model:value="form.author" placeholder="昵称" style="margin-bottom:12px" />
      <n-input v-model:value="form.email" placeholder="邮箱（选填）" style="margin-bottom:12px" />
      <n-input v-model:value="form.content" type="textarea" :rows="3" placeholder="写下你的想法..." />
      <n-button type="primary" @click="submit" :loading="submitting" style="margin-top:12px">提交留言</n-button>
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
.comments { margin-top: 48px; }
.comments h3 { font-size: 18px; margin-bottom: 20px; }
.comment-item { padding: 16px 0; border-bottom: 1px solid #f0f0f0; }
.comment-head { display: flex; gap: 12px; align-items: center; margin-bottom: 6px; }
.author { font-weight: 600; color: #5b8c5a; font-size: 14px; }
.time { color: #bbb; font-size: 12px; }
.content { color: #555; font-size: 14px; line-height: 1.6; }
.empty { color: #bbb; font-size: 14px; }
.comment-form { margin-top: 32px; }
.comment-form h4 { font-size: 16px; margin-bottom: 12px; }
</style>
