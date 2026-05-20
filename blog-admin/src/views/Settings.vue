<template>
  <div class="settings">
    <div class="page-header">
      <h3 class="page-title">// settings</h3>
      <span class="page-badge">CONFIG</span>
    </div>

    <div class="settings-card">
      <el-tabs v-model="activeTab" class="settings-tabs">
        <el-tab-pane name="profile">
          <template #label>
            <span class="tab-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              PROFILE
            </span>
          </template>
          <el-form :model="profile" label-width="60px" class="settings-form">
            <el-form-item label="昵称">
              <el-input v-model="profile.nickname" placeholder="你的昵称" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profile.email" placeholder="email@example.com" />
            </el-form-item>
            <el-form-item label="头像">
              <el-input v-model="profile.avatar" placeholder="https://..." />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateProfile" :loading="saving">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/><polyline points="17 21 17 13 7 13 7 21"/><polyline points="7 3 7 8 15 8"/></svg>
                SAVE
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane name="password">
          <template #label>
            <span class="tab-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              PASSWORD
            </span>
          </template>
          <el-form :model="passwordForm" label-width="60px" class="settings-form">
            <el-form-item label="旧密码">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="输入当前密码" />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="输入新密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updatePassword" :loading="saving">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                UPDATE
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
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
  } finally { saving.value = false }
}

async function updatePassword() {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword) return
  saving.value = true
  try {
    await api.put('/api/auth/password', passwordForm.value)
    ElMessage.success('密码修改成功')
    passwordForm.value = { oldPassword: '', newPassword: '' }
  } finally { saving.value = false }
}
</script>

<style scoped>
.page-header { display: flex; align-items: center; gap: 14px; margin-bottom: 24px; }
.page-title { font-family: 'JetBrains Mono', monospace; font-size: 16px; color: #e8eaed; font-weight: 500; margin: 0; }
.page-badge {
  font-family: 'JetBrains Mono', monospace; font-size: 9px; color: #7c3aed;
  padding: 2px 10px; border-radius: 20px;
  border: 1px solid rgba(124,58,237,0.25); letter-spacing: 2px;
}

.settings-card {
  background: rgba(17,24,39,0.4); border: 1px solid rgba(255,255,255,0.04);
  border-radius: 16px; padding: 8px 32px 32px;
  backdrop-filter: blur(12px); max-width: 560px;
}

.settings-tabs :deep(.el-tabs__header) { margin-bottom: 8px; }
.tab-label {
  display: flex; align-items: center; gap: 7px;
  font-family: 'JetBrains Mono', monospace; font-size: 12px; letter-spacing: 1px;
  color: inherit;
}

.settings-form { margin-top: 16px; }
.settings-form :deep(.el-form-item__label) {
  font-family: 'JetBrains Mono', monospace; font-size: 11px;
  color: #8b949e; text-transform: uppercase; letter-spacing: 0.5px;
}
.settings-form :deep(.el-button--primary) {
  display: flex; align-items: center; gap: 6px;
}
</style>
