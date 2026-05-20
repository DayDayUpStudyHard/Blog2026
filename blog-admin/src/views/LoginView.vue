<template>
  <div class="login-page">
    <div class="bg-grid"></div>
    <div class="login-card">
      <div class="card-glow"></div>
      <div class="logo">
        <span class="logo-bracket">&lt;</span>
        Blog2026
        <span class="logo-bracket">/&gt;</span>
      </div>
      <p class="subtitle">// 后台管理登录</p>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="doLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large">
            <template #prefix>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password @keyup.enter="doLogin">
            <template #prefix>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doLogin" :loading="loading" size="large" class="login-btn">
            <span v-if="!loading">登 录</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/index.js'

const router = useRouter()
const loading = ref(false)
const formRef = ref(null)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function doLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await login(form)
    localStorage.setItem('blog-token', res.data.data.token)
    router.push('/')
  } catch {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page { height: 100vh; display: flex; justify-content: center; align-items: center; position: relative; }
.bg-grid {
  position: absolute; inset: 0;
  background-image:
    linear-gradient(rgba(255,255,255,0.015) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.015) 1px, transparent 1px);
  background-size: 60px 60px;
}
.login-card {
  width: 400px; padding: 48px 40px; background: rgba(19,26,43,0.9);
  border: 1px solid rgba(255,255,255,0.06); border-radius: 16px;
  backdrop-filter: blur(20px); position: relative; z-index: 1;
  overflow: hidden;
}
.card-glow {
  position: absolute; top: 0; left: 0; right: 0; height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0,212,170,0.4), rgba(124,58,237,0.4), transparent);
}
.logo {
  text-align: center; font-family: 'JetBrains Mono', monospace; font-size: 24px; font-weight: 700;
  color: #e8eaed; margin-bottom: 8px; letter-spacing: 1px;
}
.logo-bracket { color: #00d4aa; font-size: 22px; }
.subtitle { text-align: center; color: #555d6b; font-family: 'JetBrains Mono', monospace; font-size: 13px; margin-bottom: 36px; }

.login-btn { width: 100%; }
</style>
