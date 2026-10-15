D:\FromGItHub\Flight-demo\bussiness-server\flight-booking\frontend\src\views\intro.vue
<template>
  <div class="intro-panel">
    <div class="intro-icon">✈</div>
    <h1 class="intro-title">吉利航空智能后台系统</h1>
    <p class="intro-subtitle">AI-Powered Flight Booking System</p>
    <div class="intro-divider"></div>
    <div class="intro-desc">
      <p>欢迎使用吉利航空智能后台系统，本系统集成了航班查询、在线预订、智能退改签、数据可视化管理等核心功能，并搭载 AI 智能助手为您提供一站式服务。</p>
      <p>请从左侧菜单选择功能模块开始使用。</p>
    </div>
    <div class="intro-features">
      <div class="feature-card">
        <span class="feature-icon">🔍</span>
        <span class="feature-text">航班查询</span>
      </div>
      <div class="feature-card">
        <span class="feature-icon">📋</span>
        <span class="feature-text">在线预订</span>
      </div>
      <div class="feature-card">
        <span class="feature-icon">🤖</span>
        <span class="feature-text">AI 助手</span>
      </div>
      <div class="feature-card">
        <span class="feature-icon">📊</span>
        <span class="feature-text">数据管理</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Intro',
};
</script>

<style scoped>
.intro-panel {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(15px); }
  to { opacity: 1; transform: translateY(0); }
}

.intro-icon {
  font-size: 56px;
  margin-bottom: 16px;
  filter: drop-shadow(0 0 12px rgba(0, 212, 255, 0.8));
  animation: fly 3s ease-in-out infinite;
}

@keyframes fly {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  25% { transform: translateY(-8px) rotate(-3deg); }
  75% { transform: translateY(5px) rotate(3deg); }
}

.intro-title {
  font-size: 32px;
  font-weight: 700;
  letter-spacing: 6px;
  margin: 0 0 8px;
  background: linear-gradient(180deg, #ffffff 0%, #60d5ff 40%, #0090ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.intro-subtitle {
  font-size: 13px;
  letter-spacing: 6px;
  text-transform: uppercase;
  color: rgba(0, 212, 255, 0.45);
  margin: 0 0 24px;
}

.intro-divider {
  width: 200px;
  height: 1px;
  background: linear-gradient(90deg, transparent, #00d4ff, transparent);
  box-shadow: 0 0 8px rgba(0, 212, 255, 0.5);
  margin-bottom: 28px;
}

.intro-desc {
  max-width: 520px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  line-height: 2;
  margin-bottom: 40px;
}

.intro-features {
  display: flex;
  gap: 24px;
}

.feature-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px 28px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 10px;
  transition: all 0.3s;
}

.feature-card:hover {
  background: rgba(0, 212, 255, 0.08);
  border-color: rgba(0, 212, 255, 0.4);
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 212, 255, 0.1);
}

.feature-icon {
  font-size: 28px;
}

.feature-text {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.75);
  letter-spacing: 1px;
}
</style>
