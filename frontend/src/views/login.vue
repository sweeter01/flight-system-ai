<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { loginRequest } from '../api/auth.js'
import { getUserMenuAndPerms } from '../api/menu.js'
import { setMenus, setPermissions } from '../store/permission.js'
import bgImg from '../assets/img.png'

const router = useRouter()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginFormRef = ref(null)

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await loginRequest(loginForm)

      if (res.code === 200) {
        localStorage.setItem('accessToken', res.data.accessToken)
        localStorage.setItem('expireIn', res.data.expireIn)

        try {
          const menuRes = await getUserMenuAndPerms()
          if (menuRes.code === 200 && menuRes.data) {
            setMenus(menuRes.data.menuList || [])
            setPermissions(menuRes.data.authorities || [])
          }
        } catch (menuError) {
          console.warn('获取菜单权限失败，但不影响登录:', menuError)
        }

        const savedToken = localStorage.getItem('accessToken')
        if (savedToken) {
          ElMessage.success('登录成功')
          router.push('/')
        }
      } else {
        ElMessage.error(res.message || '登录失败')
      }
    } catch (error) {
      const msg = error.response?.data?.message || error.message || '登录失败，请检查网络连接'
      ElMessage.error(msg)
    } finally {
      loading.value = false
    }
  })
}
</script>

<template>
  <div class="login-page" :style="{ backgroundImage: `url(${bgImg})` }">
    <!-- 遮罩层，让背景图变暗以突出卡片 -->
    <div class="bg-overlay"></div>

    <!-- 装饰粒子 -->
    <div class="particles">
      <span class="particle" style="top:10%;left:15%;animation-delay:0s"></span>
      <span class="particle" style="top:25%;left:70%;animation-delay:1s"></span>
      <span class="particle" style="top:60%;left:20%;animation-delay:2s"></span>
      <span class="particle" style="top:80%;left:80%;animation-delay:0.5s"></span>
      <span class="particle" style="top:40%;left:50%;animation-delay:1.5s"></span>
      <span class="particle" style="top:15%;left:85%;animation-delay:3s"></span>
      <span class="particle" style="top:70%;left:45%;animation-delay:2.5s"></span>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="card-border-top"></div>
      <div class="card-border-bottom"></div>
      <div class="card-corner card-corner-tl"></div>
      <div class="card-corner card-corner-tr"></div>
      <div class="card-corner card-corner-bl"></div>
      <div class="card-corner card-corner-br"></div>

      <div class="card-content">
        <h1 class="card-title">吉利航空后台</h1>
        <p class="card-subtitle">航班运营 · 票务管理 · 数据驾驶舱</p>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          class="login-form"
          label-width="60px"
          size="large"
        >
          <el-form-item label="账号：">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入账号"
              prefix-icon="User"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item label="密码：">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              登录系统
            </el-button>
          </el-form-item>
        </el-form>

        <div class="card-footer">
          <span class="footer-link">忘记密码</span>
          <span class="footer-link">验证码登录</span>
        </div>
      </div>
    </div>

    <!-- 底部图标栏 -->
    <div class="bottom-icons">
      <span class="bottom-icon">🏠</span>
      <span class="bottom-icon"></span>
      <span class="bottom-icon">✈</span>
      <span class="bottom-icon">🔗</span>
      <span class="bottom-icon">📋</span>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding-right: 12%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

/* 遮罩层 - 压暗背景图 */
.bg-overlay {
  position: absolute;
  inset: 0;
  background: rgba(5, 10, 30, 0.45);
  z-index: 0;
}

/* 装饰粒子 */
.particles {
  position: absolute;
  inset: 0;
  z-index: 1;
  pointer-events: none;
}

.particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: #00d4ff;
  border-radius: 50%;
  box-shadow: 0 0 6px #00d4ff, 0 0 12px rgba(0, 212, 255, 0.5);
  animation: particlePulse 3s ease-in-out infinite;
}

@keyframes particlePulse {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.5); }
}

/* 登录卡片 */
.login-card {
  position: relative;
  z-index: 2;
  width: 400px;
  padding: 50px 40px 40px;
  background: rgba(10, 20, 50, 0.65);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 180, 255, 0.25);
  border-radius: 4px;
  clip-path: polygon(
    20px 0%, calc(100% - 20px) 0%,
    100% 20px, 100% calc(100% - 20px),
    calc(100% - 20px) 100%, 20px 100%,
    0% calc(100% - 20px), 0% 20px
  );
}

.card-border-top {
  position: absolute;
  top: 0;
  left: 20px;
  right: 20px;
  height: 2px;
  background: linear-gradient(90deg, transparent, #00d4ff, transparent);
  box-shadow: 0 0 10px #00d4ff;
}

.card-border-bottom {
  position: absolute;
  bottom: 0;
  left: 20px;
  right: 20px;
  height: 2px;
  background: linear-gradient(90deg, transparent, #00d4ff, transparent);
  box-shadow: 0 0 10px #00d4ff;
}

.card-corner {
  position: absolute;
  width: 16px;
  height: 16px;
  border-color: #00d4ff;
  border-style: solid;
  border-width: 0;
}

.card-corner-tl {
  top: -1px;
  left: -1px;
  border-top-width: 2px;
  border-left-width: 2px;
}

.card-corner-tr {
  top: -1px;
  right: -1px;
  border-top-width: 2px;
  border-right-width: 2px;
}

.card-corner-bl {
  bottom: -1px;
  left: -1px;
  border-bottom-width: 2px;
  border-left-width: 2px;
}

.card-corner-br {
  bottom: -1px;
  right: -1px;
  border-bottom-width: 2px;
  border-right-width: 2px;
}

.card-content {
  position: relative;
  z-index: 1;
}

.card-title {
  text-align: center;
  font-size: 26px;
  font-weight: 700;
  letter-spacing: 4px;
  color: #ffffff;
  margin: 0 0 10px;
  text-shadow: 0 0 20px rgba(0, 180, 255, 0.5);
}

.card-subtitle {
  text-align: center;
  font-size: 13px;
  letter-spacing: 3px;
  color: rgba(0, 212, 255, 0.6);
  margin: 0 0 36px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.login-form :deep(.el-form-item__label) {
  color: #ffffff;
  font-size: 16px;
}

.login-form :deep(.el-input__wrapper) {
  background: #e0e0e0 !important;
  border: 1px solid rgba(0, 180, 255, 0.2);
  border-radius: 4px;
  box-shadow: none;
  padding: 4px 16px;
  height: 46px;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.login-form :deep(.el-input__wrapper:hover),
.login-form :deep(.el-input__wrapper.is-focus) {
  background: #e0e0e0 !important;
  border-color: #00d4ff;
  box-shadow: 0 0 12px rgba(0, 212, 255, 0.2);
}

.login-form :deep(.el-input__inner) {
  color: #000000;
  font-size: 15px;
  letter-spacing: 1px;
}

.login-form :deep(.el-input__inner::placeholder) {
  color: rgba(0, 0, 0, 0.4);
}

.login-form :deep(.el-input__prefix .el-icon) {
  color: rgba(0, 212, 255, 0.7);
  font-size: 18px;
}

.login-form :deep(.el-input__suffix .el-icon) {
  color: #333333;
  font-size: 18px;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 17px;
  font-weight: 600;
  letter-spacing: 8px;
  background: linear-gradient(135deg, #0066cc, #0099ff);
  border: none;
  border-radius: 4px;
  color: #ffffff;
  margin-top: 8px;
  transition: all 0.3s;
}

.login-btn:hover {
  background: linear-gradient(135deg, #0077ee, #00aaff);
  box-shadow: 0 0 24px rgba(0, 150, 255, 0.5);
  transform: translateY(-1px);
}

.login-btn:active {
  transform: translateY(0);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 16px;
  padding: 0 4px;
}

.footer-link {
  font-size: 13px;
  color: rgba(0, 212, 255, 0.6);
  cursor: pointer;
  transition: color 0.3s;
}

.footer-link:hover {
  color: #00d4ff;
}

.bottom-icons {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 24px;
  z-index: 2;
}

.bottom-icon {
  font-size: 22px;
  opacity: 0.5;
  cursor: pointer;
  transition: all 0.3s;
  filter: drop-shadow(0 0 4px rgba(0, 212, 255, 0.3));
}

.bottom-icon:hover {
  opacity: 1;
  filter: drop-shadow(0 0 8px rgba(0, 212, 255, 0.6));
  transform: translateY(-2px);
}
</style>