<template>
  <div class="dashboard">
    <h3>仪表盘</h3>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="8">
        <el-card shadow="hover"><div class="stat"><span class="num">{{ stats.articleCount }}</span><span class="label">文章</span></div></el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover"><div class="stat"><span class="num">{{ stats.categoryCount }}</span><span class="label">分类</span></div></el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover"><div class="stat"><span class="num">{{ stats.commentCount }}</span><span class="label">留言</span></div></el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminArticles, getCategories, getAdminComments } from '../api/index.js'

const stats = ref({ articleCount: 0, categoryCount: 0, commentCount: 0 })

onMounted(async () => {
  const [aRes, cRes, mRes] = await Promise.all([
    getAdminArticles({ page: 1, size: 1 }),
    getCategories(),
    getAdminComments({ page: 1, size: 1 })
  ])
  stats.value.articleCount = aRes.data.data.total
  stats.value.categoryCount = cRes.data.data.length
  stats.value.commentCount = mRes.data.data.total
})
</script>

<style scoped>
.dashboard h3 { font-size: 18px; color: #303133; }
.stat { text-align: center; padding: 20px 0; }
.num { display: block; font-size: 36px; font-weight: 700; color: #409EFF; }
.label { display: block; margin-top: 8px; color: #909399; font-size: 14px; }
</style>
