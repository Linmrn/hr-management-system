<template>
  <div class="auth-page">
    <div class="auth-shell" :class="{ 'is-register': isRegister }">
      <aside class="auth-hero">
        <div class="hero-badge">HR System</div>
        <h1>
          <span>{{ isRegister ? 'Welcome to Join Us' : 'Welcome Back' }}</span>
          <span>{{ isRegister ? '欢迎加入我们' : '欢迎回来' }}</span>
        </h1>
        <p>
          <span>{{ isRegister ? 'Create an account to start using the HR management system.' : 'Sign in to enter the HR management console quickly.' }}</span>
          <span>{{ isRegister ? '创建账号后即可开始使用人事管理系统' : '使用你的账号登录系统，快速进入人事管理后台。' }}</span>
        </p>
      </aside>

      <section class="auth-panel">
        <div class="switch-row">
          <button class="switch-btn" :class="{ active: !isRegister }" type="button" @click="toggleMode(false)">
            登录
          </button>
          <button class="switch-btn" :class="{ active: isRegister }" type="button" @click="toggleMode(true)">
            注册
          </button>
        </div>

        <transition name="fade-slide" mode="out-in">
          <el-form
            v-if="!isRegister"
            :key="'login'"
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="auth-form"
            @submit.prevent
          >
            <h2>登录账号</h2>
            <p class="subtitle">请输入用户名和密码登录系统</p>

            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                size="large"
                placeholder="用户名"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                size="large"
                type="password"
                placeholder="密码"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <div class="form-row">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            </div>

            <el-alert v-if="errorMsg" :title="errorMsg" type="error" :closable="false" show-icon class="alert" />

            <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="submitLogin">
              立即登录
            </el-button>

            <p class="form-footer">
              还没有账号？
              <el-link type="primary" underline="never" @click="toggleMode(true)">去注册</el-link>
            </p>
          </el-form>

          <el-form
            v-else
            :key="'register'"
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            class="auth-form"
            @submit.prevent
          >
            <h2>创建账号</h2>
            <p class="subtitle">填写信息提交申请，管理员审核并分配角色后才能登录</p>

            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                size="large"
                placeholder="用户名"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                size="large"
                type="password"
                placeholder="密码"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                size="large"
                type="password"
                placeholder="确认密码"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <el-alert v-if="errorMsg" :title="errorMsg" type="error" :closable="false" show-icon class="alert" />

            <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="submitRegister">
              立即注册
            </el-button>

            <p class="form-footer">
              已有账号？
              <el-link type="primary" underline="never" @click="toggleMode(false)">去登录</el-link>
            </p>
          </el-form>
        </transition>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'
import { postKeyValueRequest } from '@/utils/api'
import { initMenu } from '@/utils/menus'
import { useUserStore } from '@/store/userInfo'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref()
const registerFormRef = ref()
const loading = ref(false)
const errorMsg = ref('')
const rememberMe = ref(true)
const isRegister = ref(route.path === '/register' || route.query.mode === 'register')

const loginForm = reactive({
  username: 'admin',
  password: '123456'
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3 到 20 位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 20, message: '密码长度为 3 到 20 位', trigger: 'blur' }
  ]
}

const validateConfirmPassword = (_, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
    return
  }
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
    return
  }
  callback()
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3 到 20 位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 20, message: '密码长度为 3 到 20 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const redirectPath = computed(() => {
  const target = route.query.redirect
  return typeof target === 'string' && target ? target : '/home'
})

const syncAuthRoute = async (registerMode) => {
  const targetPath = registerMode ? '/register' : '/login'
  const query = { ...route.query }
  if (registerMode) query.mode = 'register'
  else delete query.mode
  if (route.path !== targetPath || route.query.mode !== query.mode) {
    await router.replace({ path: targetPath, query })
  }
}

const handleSuccess = async (user) => {
  userStore.setUser(user)
  await initMenu(router)
  await router.replace(redirectPath.value === '/' ? '/home' : redirectPath.value)
}

const submitLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    errorMsg.value = ''
    try {
      const resp = await postKeyValueRequest('/doLogin.do', loginForm)
      if (resp && resp.success) {
        const user = resp.data || resp.obj
        if (rememberMe.value) sessionStorage.setItem('rememberUsername', loginForm.username)
        await handleSuccess(user)
      } else {
        errorMsg.value = resp?.message || '登录失败，请检查用户名和密码'
      }
    } catch (err) {
      errorMsg.value = '登录失败，请稍后重试'
      console.error('登录失败:', err)
    } finally {
      loading.value = false
    }
  })
}

const toggleMode = async (registerMode) => {
  isRegister.value = registerMode
  await syncAuthRoute(registerMode)
}

const submitRegister = async () => {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    errorMsg.value = ''
    try {
      const payload = {
        username: registerForm.username,
        password: registerForm.password
      }
      const resp = await postKeyValueRequest('/doRegister.do', payload)
      if (resp && resp.success) {
        ElMessage.success('申请已提交，请联系管理员审核并分配角色')
        registerForm.password = ''
        registerForm.confirmPassword = ''
        isRegister.value = false
      } else {
        errorMsg.value = resp?.message || '注册失败，请稍后重试'
      }
    } catch (err) {
      errorMsg.value = '注册失败，请稍后重试'
      console.error('注册失败:', err)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
  box-sizing: border-box;
  overflow: hidden;
  background:
    radial-gradient(circle at top left, rgba(96, 165, 250, 0.22), transparent 34%),
    radial-gradient(circle at bottom right, rgba(59, 130, 246, 0.16), transparent 28%),
    linear-gradient(135deg, #eaf2ff 0%, #f7fbff 46%, #eef4ff 100%);
}

.auth-page::before {
  content: '';
  position: fixed;
  inset: 0;
  pointer-events: none;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.22) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.22) 1px, transparent 1px);
  background-size: 72px 72px;
  mask-image: linear-gradient(to bottom, rgba(0, 0, 0, 0.55), transparent 85%);
}

.auth-shell {
  width: min(1200px, 100%);
  min-height: min(760px, calc(100vh - 48px));
  display: grid;
  grid-template-columns: 1.05fr 0.95fr;
  overflow: hidden;
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 30px 90px rgba(43, 79, 137, 0.2);
  backdrop-filter: blur(18px);
  border: 1px solid rgba(255, 255, 255, 0.45);
}

.auth-hero {
  position: relative;
  padding: 56px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 28px;
  color: #fff;
  background:
    linear-gradient(160deg, rgba(37, 99, 235, 0.98) 0%, rgba(59, 130, 246, 0.95) 42%, rgba(96, 165, 250, 0.88) 100%);
  overflow: hidden;
}

.auth-panel {
  padding: 56px 64px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: rgba(255, 255, 255, 0.92);
}


.hero-glow {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.hero-glow-a {
  width: 320px;
  height: 320px;
  right: -80px;
  top: -80px;
  background: rgba(255, 255, 255, 0.16);
  filter: blur(2px);
}

.hero-glow-b {
  width: 220px;
  height: 220px;
  right: 60px;
  bottom: -70px;
  background: rgba(255, 255, 255, 0.1);
  filter: blur(2px);
}

.hero-badge {
  position: relative;
  z-index: 1;
  width: fit-content;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.22);
  font-size: 13px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.auth-hero h1 {
  position: relative;
  z-index: 1;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: clamp(30px, 3.6vw, 52px);
  line-height: 1.08;
  letter-spacing: 0.01em;
}

.auth-hero h1 span:first-child {
  font-weight: 700;
}

.auth-hero h1 span:last-child {
  font-size: 0.72em;
  font-weight: 500;
  letter-spacing: 0.04em;
  opacity: 0.95;
}

.auth-hero p {
  position: relative;
  z-index: 1;
  margin: 0;
  max-width: 560px;
  display: grid;
  gap: 4px;
  font-size: clamp(14px, 1.35vw, 17px);
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.92);
}

.auth-hero p span {
  display: block;
}


.switch-row {
  display: flex;
  justify-content: center;
  align-self: center;
  padding: 6px;
  gap: 6px;
  width: fit-content;
  border-radius: 999px;
  background: #f5f8ff;
  border: 1px solid rgba(148, 163, 184, 0.18);
  margin: 0 0 34px;
}

.switch-btn {
  min-width: 96px;
  height: 40px;
  border: 0;
  border-radius: 999px;
  background: transparent;
  color: #64748b;
  cursor: pointer;
  transition: all 0.25s ease;
}

.switch-btn.active {
  background: #fff;
  color: #2563eb;
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.12);
}

.auth-form {
  width: 100%;
  max-width: 430px;
}

.auth-form h2 {
  margin: 0;
  font-size: 30px;
  color: #0f172a;
}

.subtitle {
  margin: 10px 0 26px;
  color: #64748b;
}

.form-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.alert {
  margin-bottom: 18px;
}

.submit-btn {
  width: 100%;
  height: 48px;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.06em;
  box-shadow: 0 14px 28px rgba(37, 99, 235, 0.18);
}

.form-footer {
  margin: 18px 0 0;
  color: #64748b;
  text-align: center;
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.25s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

@media (max-width: 960px) {
  .auth-shell {
    grid-template-columns: 1fr;
    width: min(1200px, calc(100vw - 24px));
    min-height: auto;
  }

  .auth-hero {
    padding: 40px 28px;
  }

  .auth-panel {
    padding: 36px 24px 40px;
  }
}

@media (max-width: 640px) {
  .auth-page {
    padding: 12px;
  }

  .auth-shell {
    border-radius: 24px;
  }

  .auth-hero,
  .auth-panel {
    padding-left: 18px;
    padding-right: 18px;
  }

  .auth-hero h1 {
    gap: 6px;
    font-size: clamp(28px, 6vw, 38px);
  }

  .auth-hero h1 span:last-child {
    font-size: 0.78em;
  }

  .auth-form h2 {
    font-size: 26px;
  }

  .switch-row {
    width: 100%;
  }

  .switch-btn {
    flex: 1;
  }
}
</style>
