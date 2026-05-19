<template>
  <el-container class="layout">
    <el-aside width="200px">
      <div class="logo">Blog2026 后台</div>
      <el-menu :default-active="route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/"><el-icon><DataAnalysis /></el-icon>仪表盘</el-menu-item>
        <el-menu-item index="/articles"><el-icon><Document /></el-icon>文章管理</el-menu-item>
        <el-menu-item index="/categories"><el-icon><Collection /></el-icon>分类管理</el-menu-item>
        <el-menu-item index="/tags"><el-icon><PriceTag /></el-icon>标签管理</el-menu-item>
        <el-menu-item index="/comments"><el-icon><ChatLineSquare /></el-icon>留言管理</el-menu-item>
        <el-menu-item index="/settings"><el-icon><Setting /></el-icon>个人设置</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <span>{{ user.nickname || user.username }}</span>
        <el-button text @click="logout">退出</el-button>
      </el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserInfo } from '../api/index.js'

const route = useRoute()
const router = useRouter()
const user = ref({})

onMounted(async () => {
  try {
    const res = await getUserInfo()
    user.value = res.data.data
  } catch { logout() }
})

function logout() {
  localStorage.removeItem('blog-token')
  router.push('/login')
}
</script>

<style scoped>
.layout { height: 100vh; }
.el-aside { background: #304156; overflow: hidden; }
.logo { color: #fff; font-size: 18px; font-weight: 700; text-align: center; padding: 20px 0; letter-spacing: 1px; border-bottom: 1px solid #3a4e66; }
.el-header { background: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; justify-content: flex-end; gap: 16px; }
.el-main { background: #f0f2f5; }
</style>
