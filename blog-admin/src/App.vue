<template>
  <div class="admin-app">
    <div class="bg-blobs">
      <div class="bg-blob blob-1"></div>
      <div class="bg-blob blob-2"></div>
      <div class="bg-blob blob-3"></div>
    </div>
    <router-view />
  </div>
</template>

<style>
.admin-app { min-height: 100vh; position: relative; }

.bg-blobs { position: fixed; inset: 0; z-index: 0; pointer-events: none; overflow: hidden; }

.bg-blob {
  position: absolute; border-radius: 50%; filter: blur(100px); opacity: 0.35;
  will-change: transform;
}

.blob-1 {
  width: 500px; height: 500px;
  background: rgba(64,158,255,0.14);
  top: -10%; right: -5%;
  animation: blobFloat1 14s ease-in-out infinite;
}

.blob-2 {
  width: 420px; height: 420px;
  background: rgba(139,92,246,0.10);
  bottom: -8%; left: -3%;
  animation: blobFloat2 16s ease-in-out infinite;
  animation-delay: -2s;
}

.blob-3 {
  width: 360px; height: 360px;
  background: rgba(16,185,129,0.09);
  top: 40%; left: 50%;
  animation: blobFloat3 18s ease-in-out infinite;
  animation-delay: -5s;
}

@keyframes blobFloat1 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -25px) scale(1.06); }
  66% { transform: translate(-15px, 20px) scale(0.94); }
}

@keyframes blobFloat2 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(-25px, 20px) scale(1.04); }
  66% { transform: translate(20px, -15px) scale(0.96); }
}

@keyframes blobFloat3 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(18px, -28px) scale(1.05); }
}
</style>
