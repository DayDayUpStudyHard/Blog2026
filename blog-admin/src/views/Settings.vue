<template>
  <div class="settings">
    <h3>个人设置</h3>
    <el-tabs v-model="activeTab" style="margin-top:16px;max-width:500px">
      <el-tab-pane label="基本资料" name="profile">
        <el-form :model="profile" label-width="80px">
          <el-form-item label="昵称">
            <el-input v-model="profile.nickname" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="profile.email" />
          </el-form-item>
          <el-form-item label="头像URL">
            <el-input v-model="profile.avatar" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="updateProfile" :loading="saving">保存</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="修改密码" name="password">
        <el-form :model="passwordForm" label-width="80px">
          <el-form-item label="旧密码">
            <el-input v-model="passwordForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码">
            <el-input v-model="passwordForm.newPassword" type="password" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="updatePassword" :loading="saving">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo } from '../api/index.js'
import api from '../api/index.js'

const activeTab = ref('profile')
const saving = ref(false)
const profile = ref({ nickname: '', email: '', avatar: '' })
const passwordForm = ref({ oldPassword: '', newPassword: '' })

onMounted(async () => {
  try {
    const res = await getUserInfo()
    const user = res.data.data
    profile.value = { nickname: user.nickname || '', email: user.email || '', avatar: user.avatar || '' }
  } catch {}
})

async function updateProfile() {
  saving.value = true
  try {
    await api.put('/api/auth/profile', profile.value)
    ElMessage.success('保存成功')
  } finally {
    saving.value = false
  }
}

async function updatePassword() {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword) return
  saving.value = true
  try {
    await api.put('/api/auth/password', passwordForm.value)
    ElMessage.success('密码修改成功')
    passwordForm.value = { oldPassword: '', newPassword: '' }
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.settings h3 { font-size: 18px; color: #303133; }
</style>
