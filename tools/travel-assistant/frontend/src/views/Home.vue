<template>
  <div class="home-container">
    <div class="page-header">
      <h1 class="page-title">✈️ 智能旅行助手</h1>
      <p class="page-subtitle">基于 AI 的个性化旅行规划</p>
    </div>

    <a-card class="form-card">
      <a-form :model="formData" layout="vertical" @finish="handleSubmit">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="目的地城市" name="city" :rules="[{ required: true, message: '请输入目的地' }]">
              <a-input v-model:value="formData.city" placeholder="例如：北京" size="large" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="旅行天数">
              <a-input-number v-model:value="formData.days" :min="1" :max="14" size="large" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="开始日期" :rules="[{ required: true }]">
              <a-date-picker v-model:value="startDate" size="large" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="结束日期" :rules="[{ required: true }]">
              <a-date-picker v-model:value="endDate" size="large" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="旅行偏好">
              <a-select v-model:value="formData.preferences" size="large">
                <a-select-option value="历史文化">历史文化</a-select-option>
                <a-select-option value="自然风光">自然风光</a-select-option>
                <a-select-option value="美食之旅">美食之旅</a-select-option>
                <a-select-option value="购物娱乐">购物娱乐</a-select-option>
                <a-select-option value="亲子游玩">亲子游玩</a-select-option>
                <a-select-option value="休闲度假">休闲度假</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="预算等级">
              <a-select v-model:value="formData.budget" size="large">
                <a-select-option value="经济实惠">经济实惠</a-select-option>
                <a-select-option value="中等">中等</a-select-option>
                <a-select-option value="舒适享受">舒适享受</a-select-option>
                <a-select-option value="豪华体验">豪华体验</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="交通方式">
              <a-select v-model:value="formData.transportation" size="large">
                <a-select-option value="公共交通">公共交通</a-select-option>
                <a-select-option value="自驾">自驾</a-select-option>
                <a-select-option value="打车+公共交通">打车+公共交通</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="住宿类型">
              <a-select v-model:value="formData.accommodation" size="large">
                <a-select-option value="经济型酒店">经济型酒店</a-select-option>
                <a-select-option value="舒适型酒店">舒适型酒店</a-select-option>
                <a-select-option value="豪华型酒店">豪华型酒店</a-select-option>
                <a-select-option value="民宿">民宿</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item>
          <a-button type="primary" html-type="submit" size="large" :loading="loading" block>
            {{ loading ? '正在规划中...' : '🚀 开始规划' }}
          </a-button>
        </a-form-item>

        <div v-if="loading" class="progress-section">
          <a-progress :percent="loadingProgress" status="active" />
          <p class="progress-status">{{ loadingStatus }}</p>
        </div>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { generateTripPlan } from '../services/api'
import type { TripPlanRequest } from '../types'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const loadingProgress = ref(0)
const loadingStatus = ref('')

const formData = reactive<TripPlanRequest>({
  city: '',
  start_date: '',
  end_date: '',
  days: 3,
  preferences: '历史文化',
  budget: '中等',
  transportation: '公共交通',
  accommodation: '经济型酒店',
})

const startDate = ref<dayjs.Dayjs | null>(null)
const endDate = ref<dayjs.Dayjs | null>(null)

watch(startDate, (val) => {
  if (val) {
    formData.start_date = val.format('YYYY-MM-DD')
    if (endDate.value) {
      const diff = endDate.value.diff(val, 'day') + 1
      if (diff > 0) formData.days = diff
    }
  }
})

watch(endDate, (val) => {
  if (val) {
    formData.end_date = val.format('YYYY-MM-DD')
    if (startDate.value) {
      const diff = val.diff(startDate.value, 'day') + 1
      if (diff > 0) formData.days = diff
    }
  }
})

async function handleSubmit() {
  if (!formData.city) { message.error('请输入目的地城市'); return }
  if (!formData.start_date) { message.error('请选择开始日期'); return }
  if (!formData.end_date) { message.error('请选择结束日期'); return }

  loading.value = true
  loadingProgress.value = 0
  loadingStatus.value = '🔍 正在搜索景点...'

  const progressInterval = setInterval(() => {
    if (loadingProgress.value < 90) {
      loadingProgress.value += 10
      if (loadingProgress.value <= 30) loadingStatus.value = '🔍 正在搜索景点...'
      else if (loadingProgress.value <= 50) loadingStatus.value = '🌤️ 正在查询天气...'
      else if (loadingProgress.value <= 70) loadingStatus.value = '🏨 正在推荐酒店...'
      else loadingStatus.value = '📋 正在生成行程计划...'
    }
  }, 500)

  try {
    const response = await generateTripPlan({ ...formData })
    clearInterval(progressInterval)
    loadingProgress.value = 100
    loadingStatus.value = '✅ 完成！'
    setTimeout(() => {
      router.push({ name: 'Result', state: { tripPlan: response } })
    }, 400)
  } catch (error) {
    clearInterval(progressInterval)
    message.error('生成计划失败，请检查 API 配置后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.home-container {
  max-width: 680px; margin: 0 auto; padding: 40px 20px;
}
.page-header { text-align: center; margin-bottom: 32px; }
.page-title { font-size: 28px; font-weight: 700; color: #1a1a2e; margin: 0; }
.page-subtitle { color: #909399; font-size: 14px; margin-top: 8px; }

.form-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04), 0 4px 20px rgba(0,0,0,0.06);
}

.progress-section { margin-top: 16px; text-align: center; }
.progress-status { margin-top: 8px; color: #606266; font-size: 13px; }
</style>
