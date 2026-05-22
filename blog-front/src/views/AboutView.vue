<template>
  <div class="about-page">
    <div class="page-head">
      <h2 class="page-title">关于 Blog2026</h2>
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
        <div class="card-icon green">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/></svg>
        </div>
        <div class="card-text">
          <h4>技术栈</h4>
          <p>Spring Boot + Vue 3 + MySQL + Naive UI</p>
        </div>
      </div>

      <div class="about-card">
        <div class="card-icon violet">
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
        <h3>技术栈</h3>
        <div class="head-line"></div>
      </div>
      <div class="stack-grid">
        <span v-for="tech in techs" :key="tech" class="stack-item">
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
.about-page { padding: 28px 0; }
.page-head { margin-bottom: 28px; }
.page-title { font-size: 22px; color: #303133; margin-bottom: 10px; font-weight: 600; }
.page-line { height: 1px; background: #e8ecf0; }

.about-grid { display: flex; flex-direction: column; gap: 10px; margin-bottom: 36px; }

.about-card {
  display: flex; gap: 18px; align-items: flex-start;
  background: #ffffff; border: 1px solid #e8ecf0;
  padding: 20px 22px; border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.about-card:hover {
  border-color: #409EFF;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  transform: translateY(-3px);
}

.card-icon {
  width: 48px; height: 48px; border-radius: 12px; flex-shrink: 0;
  background: linear-gradient(135deg, #ecf5ff, #d9ecff);
  display: flex; align-items: center; justify-content: center; color: #409EFF;
}
.card-icon.green { background: linear-gradient(135deg, #ecfdf5, #d1fae5); color: #67c23a; }
.card-icon.violet { background: linear-gradient(135deg, #f5f3ff, #ede9fe); color: #8b5cf6; }

.card-text h4 {
  font-size: 15px; color: #303133;
  font-weight: 600; margin: 0 0 6px;
}
.card-text p { font-size: 14px; color: #909399; line-height: 1.65; margin: 0; }

.tech-section { }
.section-head { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.section-head h3 { font-size: 16px; color: #303133; font-weight: 600; margin: 0; white-space: nowrap; }
.head-line { flex: 1; height: 1px; background: #e8ecf0; }

.stack-grid { display: flex; gap: 8px; flex-wrap: wrap; }
.stack-item {
  padding: 7px 14px; border-radius: 6px;
  font-size: 13px;
  background: #ffffff; border: 1px solid #e8ecf0;
  color: #606266; transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}
.stack-item:hover {
  border-color: #409EFF; color: #409EFF; background: #ecf5ff;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
</style>
