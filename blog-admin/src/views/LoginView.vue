<template>
  <div class="login-page">
    <div class="login-card">
      <div class="card-inner">
        <div class="logo">
          <span class="logo-icon">B</span>
          <span class="logo-text">Blog2026</span>
        </div>
        <p class="subtitle">管理后台</p>

        <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="doLogin" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="form.username" placeholder="用户名" size="large"
              :prefix-icon="UserIcon"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password" type="password" placeholder="密码" size="large"
              show-password :prefix-icon="LockIcon"
              @keyup.enter="doLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="doLogin" :loading="loading" size="large" class="login-btn">
              <span v-if="!loading">登 录</span>
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/index.js'

const UserIcon = h('svg', { width: 16, height: 16, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2, innerHTML: '<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>' })
const LockIcon = h('svg', { width: 16, height: 16, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2, innerHTML: '<rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>' })

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
  } catch {} finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh; display: flex; justify-content: center; align-items: center;
}

.login-card {
  width: 400px;
}
.card-inner {
  background: rgba(255,255,255,0.75);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255,255,255,0.5);
  border-radius: 18px;
  padding: 48px 40px 40px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.04), 0 12px 40px rgba(0,0,0,0.08);
}

/* Logo */
.logo {
  text-align: center;
  display: flex; align-items: center; justify-content: center; gap: 8px;
  margin-bottom: 8px;
}
.logo-icon {
  width: 36px; height: 36px; border-radius: 10px;
  background: linear-gradient(135deg, #409EFF, #66b1ff); color: #fff;
  font-size: 18px; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
}
.logo-text { font-size: 22px; font-weight: 700; color: #303133; }

.subtitle {
  text-align: center; color: #909399; font-size: 13px;
  margin-bottom: 36px;
}

/* Form */
.login-form :deep(.el-form-item) { margin-bottom: 20px; }

.login-btn {
  width: 100%; height: 44px !important;
  font-size: 15px !important; font-weight: 600 !important;
  border-radius: 8px !important;
  margin-top: 4px;
  box-shadow: 0 4px 14px rgba(64,158,255,0.25);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.login-btn:hover {
  box-shadow: 0 6px 20px rgba(64,158,255,0.35);
  transform: translateY(-1px);
}
</style>
