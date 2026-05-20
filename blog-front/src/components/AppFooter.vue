<template>
  <footer class="footer">
    <div class="footer-inner">
      <div class="footer-top">
        <div class="footer-brand">
          <span class="prompt">~/blog2026</span>
          <span class="cursor">_</span>
        </div>
        <div class="footer-links">
          <a href="/" class="footer-link"><span class="link-num">01</span> 首页</a>
          <a href="/categories" class="footer-link"><span class="link-num">02</span> 分类</a>
          <a href="/search" class="footer-link"><span class="link-num">03</span> 搜索</a>
          <a href="/about" class="footer-link"><span class="link-num">04</span> 关于</a>
        </div>
      </div>
      <div class="footer-divider"></div>
      <div class="footer-bottom">
        <span class="copyright">&copy; 2024 Blog2026</span>
        <span class="separator">·</span>
        <span class="powered">Powered by Spring Boot & Vue</span>
        <span class="separator">·</span>
        <span class="status">
          <span class="status-dot"></span>
          ONLINE
        </span>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer { position: relative; z-index: 1; padding: 48px 0 32px; }
.footer-inner { max-width: 860px; margin: 0 auto; padding: 0 20px; }

.footer-top { display: flex; justify-content: space-between; align-items: center; }

.footer-brand {
  font-family: 'JetBrains Mono', monospace; font-size: 15px; color: #00d4aa;
  display: flex; align-items: center; gap: 4px;
}
.prompt { }
.cursor {
  color: #00d4aa; font-weight: 400;
  animation: blink 1s step-end infinite;
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

.footer-links { display: flex; gap: 20px; }
.footer-link {
  font-family: 'JetBrains Mono', monospace; font-size: 12px; color: #7d8790;
  text-decoration: none; transition: color 0.2s;
}
.footer-link:hover { color: #00d4aa; }
.link-num { font-size: 9px; color: #5a6070; margin-right: 2px; transition: color 0.2s; }
.footer-link:hover .link-num { color: #00d4aa; }

.footer-divider {
  height: 1px; margin: 20px 0;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.06), transparent);
}

.footer-bottom {
  display: flex; align-items: center; justify-content: center; gap: 8px;
  font-family: 'JetBrains Mono', monospace; font-size: 11px; color: #5a6070;
}
.separator { color: #2a3040; }
.powered { }
.status { display: flex; align-items: center; gap: 5px; }
.status-dot {
  width: 5px; height: 5px; border-radius: 50%; background: #00d4aa;
  box-shadow: 0 0 6px rgba(0,212,170,0.5);
  animation: statusPulse 2s ease-in-out infinite;
}
@keyframes statusPulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}
</style>
