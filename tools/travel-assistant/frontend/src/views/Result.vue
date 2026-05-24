<template>
  <div class="result-layout" v-if="tripPlan">
    <!-- Sidebar -->
    <aside class="result-sidebar">
      <a-menu v-model:selectedKeys="[activeSection]" mode="inline" @click="scrollToSection">
        <a-menu-item key="overview">📋 行程概览</a-menu-item>
        <a-menu-item key="budget" v-if="tripPlan.budget">💰 预算明细</a-menu-item>
        <a-menu-item key="days">📅 每日行程</a-menu-item>
        <a-menu-item key="weather">🌤️ 天气预报</a-menu-item>
        <a-menu-item key="suggestions">💡 旅行建议</a-menu-item>
      </a-menu>
      <div class="sidebar-actions">
        <a-button type="primary" block @click="goBack" style="margin-bottom: 8px">🔄 重新规划</a-button>
        <a-button block @click="toggleEditMode" :type="editMode ? 'dashed' : 'default'">
          {{ editMode ? '💾 保存修改' : '✏️ 编辑行程' }}
        </a-button>
        <a-dropdown style="margin-top: 8px; width: 100%">
          <a-button block>📥 导出</a-button>
          <template #overlay>
            <a-menu>
              <a-menu-item key="image" @click="exportAsImage">导出为图片</a-menu-item>
              <a-menu-item key="pdf" @click="exportAsPDF">导出为 PDF</a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
    </aside>

    <!-- Main content -->
    <main class="result-main" id="trip-plan-content">
      <div id="overview" class="section">
        <h2 class="section-title">📋 行程概览</h2>
        <a-card>
          <a-descriptions bordered :column="2">
            <a-descriptions-item label="目的地">{{ tripPlan.city }}</a-descriptions-item>
            <a-descriptions-item label="日期">{{ tripPlan.start_date }} ~ {{ tripPlan.end_date }}</a-descriptions-item>
            <a-descriptions-item label="天数">{{ tripPlan.days.length }} 天</a-descriptions-item>
            <a-descriptions-item label="景点数">{{ totalAttractions }} 个</a-descriptions-item>
          </a-descriptions>
        </a-card>
      </div>

      <div id="budget" class="section" v-if="tripPlan.budget">
        <h2 class="section-title">💰 预算明细</h2>
        <a-card>
          <a-row :gutter="16">
            <a-col :span="6">
              <a-statistic title="景点门票" :value="tripPlan.budget.total_attractions" suffix="元" />
            </a-col>
            <a-col :span="6">
              <a-statistic title="酒店住宿" :value="tripPlan.budget.total_hotels" suffix="元" />
            </a-col>
            <a-col :span="6">
              <a-statistic title="餐饮费用" :value="tripPlan.budget.total_meals" suffix="元" />
            </a-col>
            <a-col :span="6">
              <a-statistic title="交通费用" :value="tripPlan.budget.total_transportation" suffix="元" />
            </a-col>
          </a-row>
          <a-divider />
          <a-row>
            <a-col :span="24" style="text-align: center">
              <a-statistic
                title="预估总费用"
                :value="tripPlan.budget.total"
                suffix="元"
                :value-style="{ color: '#cf1322', fontSize: '32px', fontWeight: 'bold' }"
              />
            </a-col>
          </a-row>
        </a-card>
      </div>

      <div id="days" class="section">
        <h2 class="section-title">📅 每日行程</h2>
        <a-card v-for="(day, di) in tripPlan.days" :key="di" style="margin-bottom: 16px" :title="`第 ${di + 1} 天 — ${day.date}`">
          <p class="day-desc">{{ day.description }}</p>

          <a-divider>🏨 住宿</a-divider>
          <p v-if="day.hotel" class="hotel-info">
            <strong>{{ day.hotel.name }}</strong> — {{ day.hotel.address }} | {{ day.hotel.price_range }} | 评分 {{ day.hotel.rating }}
          </p>
          <p v-else>暂无酒店信息</p>

          <a-divider>🏛️ 景点</a-divider>
          <div v-for="(att, ai) in day.attractions" :key="ai" class="attraction-row">
            <div class="attraction-info">
              <span class="attraction-index">{{ ai + 1 }}</span>
              <div>
                <strong>{{ att.name }}</strong>
                <p class="attraction-detail">{{ att.description }} | 建议游览 {{ att.visit_duration }} 分钟 | 门票 ¥{{ att.ticket_price }}</p>
                <p class="attraction-addr">{{ att.address }}</p>
              </div>
            </div>
            <div v-if="editMode" class="attraction-actions">
              <a-button size="small" @click="moveAttraction(di, ai, 'up')" :disabled="ai === 0">上移</a-button>
              <a-button size="small" @click="moveAttraction(di, ai, 'down')" :disabled="ai === day.attractions.length - 1">下移</a-button>
              <a-button size="small" danger @click="deleteAttraction(di, ai)">删除</a-button>
            </div>
          </div>

          <a-divider>🍽️ 餐饮</a-divider>
          <div v-for="(meal, mi) in day.meals" :key="mi" class="meal-row">
            <a-tag>{{ mealTypeLabel(meal.type) }}</a-tag>
            <span>{{ meal.name }}</span>
            <span class="meal-cost">¥{{ meal.estimated_cost }}</span>
          </div>
        </a-card>
      </div>

      <div id="weather" class="section">
        <h2 class="section-title">🌤️ 天气预报</h2>
        <a-row :gutter="16">
          <a-col :span="8" v-for="(w, wi) in tripPlan.weather_info" :key="wi">
            <a-card size="small">
              <p class="weather-date">{{ w.date }}</p>
              <p>{{ w.day_weather }} / {{ w.night_weather }}</p>
              <p class="weather-temp">🌡️ {{ w.day_temp }}°C ~ {{ w.night_temp }}°C</p>
              <p class="weather-wind">{{ w.wind_direction }} {{ w.wind_power }}</p>
            </a-card>
          </a-col>
        </a-row>
      </div>

      <div id="suggestions" class="section">
        <h2 class="section-title">💡 旅行建议</h2>
        <a-card>
          <p class="suggestions-text">{{ tripPlan.overall_suggestions }}</p>
        </a-card>
      </div>
    </main>
  </div>

  <a-empty v-else description="未找到旅行计划数据" style="margin-top: 100px" />
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import type { TripPlan } from '../types'

const router = useRouter()
const tripPlan = ref<TripPlan | null>(null)
const editMode = ref(false)
const activeSection = ref('overview')
const originalPlan = ref<TripPlan | null>(null)

onMounted(() => {
  const state = history.state as any
  if (state?.tripPlan) {
    tripPlan.value = state.tripPlan
  }
})

const totalAttractions = computed(() => {
  if (!tripPlan.value) return 0
  return tripPlan.value.days.reduce((s, d) => s + d.attractions.length, 0)
})

function goBack() {
  router.push({ name: 'Home' })
}

function scrollToSection({ key }: { key: string }) {
  activeSection.value = key
  document.getElementById(key)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function mealTypeLabel(type: string) {
  const map: Record<string, string> = { breakfast: '早餐', lunch: '午餐', dinner: '晚餐', snack: '小吃' }
  return map[type] || type
}

function toggleEditMode() {
  if (editMode.value) {
    editMode.value = false
    message.success('行程已保存')
  } else {
    editMode.value = true
    originalPlan.value = JSON.parse(JSON.stringify(tripPlan.value))
    message.info('进入编辑模式，可调整景点顺序')
  }
}

function moveAttraction(dayIdx: number, attIdx: number, direction: 'up' | 'down') {
  if (!tripPlan.value) return
  const arr = tripPlan.value.days[dayIdx].attractions
  const newIdx = direction === 'up' ? attIdx - 1 : attIdx + 1
  if (newIdx >= 0 && newIdx < arr.length) {
    ;[arr[attIdx], arr[newIdx]] = [arr[newIdx], arr[attIdx]]
  }
}

function deleteAttraction(dayIdx: number, attIdx: number) {
  if (!tripPlan.value) return
  tripPlan.value.days[dayIdx].attractions.splice(attIdx, 1)
  message.success('已删除景点')
}

async function exportAsImage() {
  message.info('导出功能需要 html2canvas 库支持，请安装依赖后使用')
}

async function exportAsPDF() {
  message.info('导出功能需要 jspdf 库支持，请安装依赖后使用')
}
</script>

<style scoped>
.result-layout { display: flex; min-height: calc(100vh - 0px); }

.result-sidebar {
  width: 220px; padding: 20px 12px; position: fixed; top: 0; left: 0; bottom: 0;
  background: #fff; border-right: 1px solid #f0f0f0;
  display: flex; flex-direction: column;
}
.sidebar-actions { padding: 12px 4px; margin-top: auto; border-top: 1px solid #f0f0f0; }

.result-main {
  margin-left: 220px; flex: 1; padding: 24px 32px; max-width: 900px;
}

.section { margin-bottom: 32px; scroll-margin-top: 20px; }
.section-title { font-size: 18px; font-weight: 600; margin-bottom: 12px; color: #1a1a2e; }

.day-desc { color: #606266; margin-bottom: 4px; }
.hotel-info { font-size: 13px; color: #606266; }

.attraction-row {
  display: flex; align-items: flex-start; gap: 12px;
  padding: 10px 0; border-bottom: 1px solid #f5f5f5;
}
.attraction-info { flex: 1; display: flex; gap: 10px; }
.attraction-index {
  width: 24px; height: 24px; border-radius: 50%;
  background: #409EFF; color: #fff; display: flex; align-items: center;
  justify-content: center; font-size: 12px; font-weight: 600; flex-shrink: 0; margin-top: 2px;
}
.attraction-detail { font-size: 12px; color: #909399; margin: 2px 0; }
.attraction-addr { font-size: 12px; color: #c0c4cc; }
.attraction-actions { display: flex; gap: 4px; flex-shrink: 0; }

.meal-row {
  display: flex; align-items: center; gap: 8px; padding: 6px 0;
}
.meal-cost { margin-left: auto; color: #f59e0b; font-weight: 500; }

.weather-date { font-weight: 600; }
.weather-temp { font-size: 20px; font-weight: 700; color: #f59e0b; }
.weather-wind { color: #909399; font-size: 12px; }

.suggestions-text { line-height: 1.8; color: #4a5568; }
</style>
