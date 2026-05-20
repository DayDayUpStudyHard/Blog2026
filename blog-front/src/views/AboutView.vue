<template>
  <div class="about-page">
    <h2 class="page-title">
      <span class="title-icon">&gt;</span> 关于 Blog2026
    </h2>
    <div class="content">
      <div class="about-card">
        <div class="card-dot"></div>
        <p>这里是我的个人博客，记录技术笔记、读书心得和生活感悟。</p>
      </div>
      <div class="about-card">
        <div class="card-dot purple"></div>
        <p>技术栈：Spring Boot + Vue 3 + MySQL</p>
      </div>
      <div class="about-card">
        <div class="card-dot"></div>
        <p>如果你有任何想说的，欢迎在文章下方留言。</p>
      </div>
    </div>
    <div class="tech-stack">
      <h3><span class="title-icon">#</span> 技术栈</h3>
      <div class="stack-grid">
        <span class="stack-item">Spring Boot</span>
        <span class="stack-item">MyBatis-Plus</span>
        <span class="stack-item">MySQL</span>
        <span class="stack-item">Vue 3</span>
        <span class="stack-item">Naive UI</span>
        <span class="stack-item">Vite</span>
        <span class="stack-item">Docker</span>
        <span class="stack-item">Markdown</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.about-page { padding: 32px 0; }
.page-title { font-family: 'JetBrains Mono', monospace; font-size: 22px; color: #e8eaed; margin-bottom: 32px; font-weight: 600; }
.title-icon { color: #00d4aa; margin-right: 8px; }

.about-card {
  background: rgba(19,26,43,0.8); border: 1px solid rgba(255,255,255,0.06);
  padding: 18px 22px; border-radius: 10px; margin-bottom: 14px; position: relative;
  transition: border-color 0.3s;
}
.about-card:hover { border-color: rgba(0,212,170,0.2); }
.about-card p { font-size: 15px; color: #b0b8c4; line-height: 1.7; }
.card-dot {
  position: absolute; left: 10px; top: 50%; transform: translateY(-50%);
  width: 8px; height: 8px; border-radius: 50%; background: #00d4aa;
  box-shadow: 0 0 8px rgba(0,212,170,0.5);
}
.card-dot.purple { background: #7c3aed; box-shadow: 0 0 8px rgba(124,58,237,0.5); }

.tech-stack { margin-top: 36px; }
.tech-stack h3 { font-family: 'JetBrains Mono', monospace; font-size: 16px; color: #e8eaed; margin-bottom: 16px; font-weight: 500; }
.stack-grid { display: flex; gap: 10px; flex-wrap: wrap; }
.stack-item {
  padding: 6px 14px; border-radius: 6px; font-size: 13px; font-family: 'JetBrains Mono', monospace;
  background: rgba(0,212,170,0.06); border: 1px solid rgba(0,212,170,0.15); color: #00d4aa;
  transition: all 0.3s;
}
.stack-item:hover { background: rgba(0,212,170,0.12); border-color: rgba(0,212,170,0.35); box-shadow: 0 0 12px rgba(0,212,170,0.1); }
</style>
