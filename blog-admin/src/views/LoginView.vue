<template>
  <div class="login-page">
    <!-- Background layers -->
    <div class="bg-grid"></div>
    <div class="bg-glow bg-glow-1"></div>
    <div class="bg-glow bg-glow-2"></div>
    <canvas ref="particleCanvas" class="bg-particles"></canvas>

    <!-- Scanline -->
    <div class="scanline"></div>

    <!-- Login Card -->
    <div class="login-card">
      <div class="card-border-glow"></div>
      <div class="card-inner">
        <div class="logo">
          <span class="logo-bracket">&lt;</span>
          <span class="logo-core">B2026</span>
          <span class="logo-bracket">/&gt;</span>
        </div>
        <p class="subtitle">
          <span class="subtitle-line"></span>
          admin console
          <span class="subtitle-line"></span>
        </p>

        <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="doLogin" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="form.username" placeholder="用户名" size="large"
              :prefix-icon="UserIcon"
              class="login-input"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password" type="password" placeholder="密码" size="large"
              show-password :prefix-icon="LockIcon"
              @keyup.enter="doLogin"
              class="login-input"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="doLogin" :loading="loading" size="large" class="login-btn">
              <span v-if="!loading" class="btn-text">SYS.AUTH()</span>
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <span class="footer-dot"></span>
          Blog2026 Admin v1.0
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, h } from 'vue'
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

// Particles
const particleCanvas = ref(null)
let animId = null
function initParticles() {
  const canvas = particleCanvas.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  const dpr = window.devicePixelRatio || 1
  const { innerWidth: w, innerHeight: h } = window

  canvas.width = w * dpr
  canvas.height = h * dpr
  canvas.style.width = w + 'px'
  canvas.style.height = h + 'px'
  ctx.scale(dpr, dpr)

  const particles = Array.from({ length: 50 }, () => ({
    x: Math.random() * w, y: Math.random() * h,
    vx: (Math.random() - 0.5) * 0.3,
    vy: (Math.random() - 0.5) * 0.3,
    r: Math.random() * 1.5 + 0.3,
    alpha: Math.random() * 0.4 + 0.08,
  }))

  function draw() {
    ctx.clearRect(0, 0, w, h)
    for (let i = 0; i < particles.length; i++) {
      const p = particles[i]
      p.x += p.vx; p.y += p.vy
      if (p.x < 0) p.x = w; if (p.x > w) p.x = 0
      if (p.y < 0) p.y = h; if (p.y > h) p.y = 0

      ctx.beginPath()
      ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(0,212,170,${p.alpha})`
      ctx.fill()

      for (let j = i + 1; j < particles.length; j++) {
        const dx = p.x - particles[j].x
        const dy = p.y - particles[j].y
        const dist = Math.sqrt(dx * dx + dy * dy)
        if (dist < 100) {
          ctx.beginPath()
          ctx.moveTo(p.x, p.y); ctx.lineTo(particles[j].x, particles[j].y)
          ctx.strokeStyle = `rgba(0,212,170,${0.03 * (1 - dist / 100)})`
          ctx.stroke()
        }
      }
    }
    animId = requestAnimationFrame(draw)
  }
  draw()
}

onMounted(initParticles)
onUnmounted(() => { if (animId) cancelAnimationFrame(animId) })

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
/* ═══ Page ═══ */
.login-page {
  height: 100vh; display: flex; justify-content: center; align-items: center;
  position: relative; overflow: hidden; background: #0d1520;
}

/* ═══ Background ═══ */
.bg-grid {
  position: absolute; inset: 0;
  background-image:
    linear-gradient(rgba(255,255,255,0.028) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.028) 1px, transparent 1px);
  background-size: 60px 60px;
  mask-image: radial-gradient(ellipse at center, black 30%, transparent 70%);
}
.bg-glow {
  position: absolute; border-radius: 50%; filter: blur(140px); opacity: 0.07;
  animation: glowDrift 8s ease-in-out infinite;
}
.bg-glow-1 { width: 700px; height: 700px; background: #00d4aa; top: -250px; right: -150px; }
.bg-glow-2 { width: 500px; height: 500px; background: #7c3aed; bottom: -200px; left: -100px; animation-delay: -4s; }
.bg-particles { position: absolute; inset: 0; z-index: 1; }

/* Scanline */
.scanline {
  position: absolute; inset: 0; z-index: 2; pointer-events: none;
  background: repeating-linear-gradient(0deg, transparent, transparent 3px, rgba(0,0,0,0.03) 3px, rgba(0,0,0,0.03) 4px);
}

/* ═══ Card ═══ */
.login-card {
  width: 420px; position: relative; z-index: 10;
  border-radius: 20px;
  animation: floatUp 0.6s ease;
}
.card-border-glow {
  position: absolute; inset: -1px; border-radius: 20px; padding: 1px;
  background: linear-gradient(135deg, rgba(0,212,170,0.3), transparent 40%, transparent 60%, rgba(124,58,237,0.3));
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  mask-composite: exclude;
  -webkit-mask-composite: xor;
  animation: borderRotate 6s linear infinite;
}
.card-inner {
  background: rgba(22,34,48,0.9);
  border-radius: 20px;
  padding: 52px 44px 36px;
  backdrop-filter: blur(24px);
  position: relative;
  overflow: hidden;
}
.card-inner::before {
  content: '';
  position: absolute; top: 0; left: 0; right: 0; height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0,212,170,0.5), rgba(124,58,237,0.3), transparent);
}

/* Logo */
.logo {
  text-align: center; font-family: 'JetBrains Mono', monospace; font-size: 26px; font-weight: 700;
  color: #e8eaed; margin-bottom: 10px; letter-spacing: 2px;
}
.logo-bracket { color: #00d4aa; font-size: 23px; }
.logo-core {
  background: linear-gradient(135deg, #e8eaed 20%, #00d4aa 100%);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
}

.subtitle {
  text-align: center; color: #6e7687; font-family: 'JetBrains Mono', monospace;
  font-size: 11px; margin-bottom: 40px; letter-spacing: 3px;
  display: flex; align-items: center; justify-content: center; gap: 12px;
  text-transform: uppercase;
}
.subtitle-line { width: 30px; height: 1px; background: rgba(255,255,255,0.10); }

/* Form */
.login-form { position: relative; z-index: 1; }
.login-form :deep(.el-form-item) { margin-bottom: 20px; }
.login-form :deep(.el-input__wrapper) {
  background: rgba(255,255,255,0.05) !important;
  border: 1px solid rgba(255,255,255,0.10);
  border-radius: 10px !important;
  padding: 8px 14px;
  transition: all 0.3s;
}
.login-form :deep(.el-input__wrapper:hover) {
  border-color: rgba(0,212,170,0.3);
  background: rgba(255,255,255,0.08) !important;
}
.login-form :deep(.el-input.is-focus .el-input__wrapper) {
  border-color: rgba(0,212,170,0.5);
  box-shadow: 0 0 0 3px rgba(0,212,170,0.08), 0 0 16px rgba(0,212,170,0.05) !important;
  background: rgba(255,255,255,0.10) !important;
}

/* Button */
.login-btn {
  width: 100%; height: 48px !important; border-radius: 10px !important;
  font-family: 'JetBrains Mono', monospace !important; font-size: 14px !important;
  font-weight: 600 !important; letter-spacing: 2px;
  background: linear-gradient(135deg, rgba(0,212,170,0.2), rgba(0,212,170,0.1)) !important;
  border: 1px solid rgba(0,212,170,0.35) !important;
  color: #00d4aa !important;
  position: relative; overflow: hidden; margin-top: 8px;
}
.login-btn::after {
  content: '';
  position: absolute; inset: 0;
  background: linear-gradient(135deg, transparent 30%, rgba(0,212,170,0.15) 50%, transparent 70%);
  background-size: 200% 100%;
  animation: btnShine 2.5s ease-in-out infinite;
}
.login-btn:hover {
  box-shadow: 0 0 30px rgba(0,212,170,0.25), 0 0 60px rgba(0,212,170,0.08) !important;
  transform: translateY(-2px);
}
.login-btn:active { transform: translateY(0); }
.btn-text { position: relative; z-index: 1; }

/* Footer */
.login-footer {
  text-align: center; margin-top: 32px;
  font-family: 'JetBrains Mono', monospace; font-size: 10px; color: #4a5060;
  display: flex; align-items: center; justify-content: center; gap: 6px;
  letter-spacing: 1px;
}
.footer-dot {
  width: 5px; height: 5px; border-radius: 50%; background: #00d4aa;
  box-shadow: 0 0 6px rgba(0,212,170,0.5);
}

/* ═══ Animations ═══ */
@keyframes floatUp {
  0% { opacity: 0; transform: translateY(20px) scale(0.96); }
  100% { opacity: 1; transform: translateY(0) scale(1); }
}
@keyframes glowDrift {
  0%, 100% { transform: translate(0, 0); }
  33% { transform: translate(30px, -20px); }
  66% { transform: translate(-20px, 15px); }
}
@keyframes borderRotate {
  0% { filter: hue-rotate(0deg); }
  100% { filter: hue-rotate(360deg); }
}
@keyframes btnShine {
  0%, 100% { background-position: -200% 0; }
  50% { background-position: 200% 0; }
}
</style>
