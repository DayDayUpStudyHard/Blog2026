<template>
  <div class="about-page">
    <div class="page-head">
      <h2 class="page-title">
        <span class="title-icon">&gt;</span> 关于 Blog2026
      </h2>
      <div class="page-line"></div>
    </div>

    <div class="about-grid">
      <div class="about-card">
        <div class="card-icon">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>
        </div>
        <div class="card-text">
          <h4>关于本站</h4>
          <p>这里是我的个人博客，记录技术笔记、读书心得和生活感悟。</p>
        </div>
      </div>

      <div class="about-card">
        <div class="card-icon purple">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/></svg>
        </div>
        <div class="card-text">
          <h4>技术栈</h4>
          <p>Spring Boot + Vue 3 + MySQL + Naive UI</p>
        </div>
      </div>

      <div class="about-card">
        <div class="card-icon">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
        </div>
        <div class="card-text">
          <h4>联系我</h4>
          <p>如果你有任何想说的，欢迎在文章下方留言。</p>
        </div>
      </div>
    </div>

    <div class="tech-section">
      <div class="section-head">
        <h3><span class="title-icon">#</span> 技术栈</h3>
        <div class="head-line"></div>
      </div>
      <div class="stack-grid">
        <span v-for="tech in techs" :key="tech" class="stack-item">
          <span class="stack-dot"></span>
          {{ tech }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
const techs = ['Spring Boot', 'MyBatis-Plus', 'MySQL', 'Redis', 'Sa-Token', 'Vue 3', 'Naive UI', 'Vite', 'Markdown', 'Docker']
</script>

<style scoped>
.about-page { padding: 32px 0; }
.page-head { margin-bottom: 32px; }
.page-title { font-family: 'JetBrains Mono', monospace; font-size: 22px; color: #e8eaed; margin-bottom: 12px; font-weight: 600; }
.title-icon { color: #00d4aa; margin-right: 8px; }
.page-line { height: 1px; background: linear-gradient(90deg, rgba(0,212,170,0.25), transparent); }

.about-grid { display: flex; flex-direction: column; gap: 12px; margin-bottom: 40px; }

.about-card {
  display: flex; gap: 18px; align-items: flex-start;
  background: rgba(26,39,56,0.5); border: 1px solid rgba(255,255,255,0.04);
  padding: 20px 22px; border-radius: 14px;
  backdrop-filter: blur(8px);
  transition: all 0.35s;
}
.about-card:hover {
  border-color: rgba(0,212,170,0.2);
  background: rgba(26,39,56,0.7);
  transform: translateX(4px);
  box-shadow: 0 0 30px rgba(0,212,170,0.06);
}

.card-icon {
  width: 48px; height: 48px; border-radius: 12px; flex-shrink: 0;
  background: linear-gradient(135deg, rgba(0,212,170,0.15), rgba(0,212,170,0.05));
  border: 1px solid rgba(0,212,170,0.15);
  display: flex; align-items: center; justify-content: center; color: #00d4aa;
}
.card-icon.purple {
  background: linear-gradient(135deg, rgba(124,58,237,0.15), rgba(124,58,237,0.05));
  border-color: rgba(124,58,237,0.15); color: #7c3aed;
}

.card-text h4 {
  font-family: 'JetBrains Mono', monospace; font-size: 15px; color: #e8eaed;
  font-weight: 500; margin: 0 0 6px;
}
.card-text p { font-size: 14px; color: #8b949e; line-height: 1.65; margin: 0; }

/* Tech stack */
.tech-section { }
.section-head { display: flex; align-items: center; gap: 12px; margin-bottom: 18px; }
.section-head h3 { font-family: 'JetBrains Mono', monospace; font-size: 16px; color: #e8eaed; font-weight: 500; margin: 0; white-space: nowrap; }
.head-line { flex: 1; height: 1px; background: rgba(255,255,255,0.06); }

.stack-grid { display: flex; gap: 10px; flex-wrap: wrap; }
.stack-item {
  display: flex; align-items: center; gap: 7px;
  padding: 7px 16px; border-radius: 8px;
  font-family: 'JetBrains Mono', monospace; font-size: 13px;
  background: rgba(26,39,56,0.5); border: 1px solid rgba(0,212,170,0.12);
  color: #00d4aa; transition: all 0.3s;
}
.stack-item:hover {
  background: rgba(0,212,170,0.1); border-color: rgba(0,212,170,0.3);
  box-shadow: 0 0 16px rgba(0,212,170,0.1); transform: translateY(-2px);
}
.stack-dot {
  width: 5px; height: 5px; border-radius: 50%; background: #00d4aa;
  box-shadow: 0 0 4px rgba(0,212,170,0.4);
}
</style>
