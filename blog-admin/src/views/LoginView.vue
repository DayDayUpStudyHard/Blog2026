<template>
  <div class="login-page">
    <div class="login-card">
      <h2>Blog2026 后台</h2>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="doLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doLogin" :loading="loading" size="large" style="width:100%">登 录</el-button>
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
.login-page { height: 100vh; display: flex; justify-content: center; align-items: center; background: #f0f2f5; }
.login-card { width: 380px; padding: 40px; background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,.08); }
.login-card h2 { text-align: center; margin-bottom: 32px; color: #303133; font-size: 22px; }
</style>
