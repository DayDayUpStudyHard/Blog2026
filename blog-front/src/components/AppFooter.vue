<template>
  <footer class="footer">
    <div class="footer-line"></div>
    <p><span class="accent">~/blog2026</span> <span class="muted">· 记录思考与生活 ·</span></p>
    <p class="copyright">© 2024 Blog2026. Powered by Spring Boot & Vue.</p>
  </footer>
</template>

<style scoped>
.footer { text-align: center; padding: 40px 20px; position: relative; z-index: 1; }
.footer-line {
  height: 1px; margin-bottom: 20px;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.06), transparent);
}
.footer p { margin-bottom: 6px; font-size: 13px; }
.accent { color: #00d4aa; font-family: 'JetBrains Mono', monospace; }
.muted { color: #555d6b; }
.copyright { color: #3a4050; font-size: 12px; }
</style>
