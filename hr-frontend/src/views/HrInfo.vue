<template>
  <div class="hr-center-page">
    <el-card class="hero-card" shadow="never">
      <div class="hero-content">
        <div>
          <p class="eyebrow">个人中心</p>
          <h2>HR 个人信息</h2>
          <p class="summary">查看和维护当前登录账号的基础资料、头像与登录密码。</p>
        </div>
        <div class="hero-actions">
          <el-tag type="primary" effect="plain">个人中心</el-tag>
          <el-button @click="reloadProfile">刷新</el-button>
          <el-button type="danger" plain @click="handleLogout">注销登录</el-button>
        </div>
      </div>
    </el-card>

    <el-row :gutter="16">
      <el-col :span="8">
        <el-card shadow="never" class="profile-card" v-loading="loading">
          <div class="avatar-wrap">
            <el-upload
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="uploadAvatar"
              :data="{ id: hr?.id }"
            >
              <img v-if="avatarUrl" title="点击修改用户图像" :src="avatarUrl" class="avatar" alt="avatar" @error="handleAvatarError" />
              <div v-else class="avatar-placeholder">点击上传头像</div>
            </el-upload>
          </div>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户名">{{ hr?.username || '-' }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ hr?.name || '-' }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ hr?.telephone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="手机">{{ hr?.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="地址">{{ hr?.address || '-' }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ hr?.enabled ? '启用' : '禁用' }}</el-descriptions-item>
            <el-descriptions-item label="角色">
              <el-tag v-for="(r, index) in hr?.roles || []" :key="index" size="small" style="margin-right: 6px">{{ r.nameZh }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>
          <div class="profile-actions">
            <el-button type="primary" @click="showUpdateHrInfoView">修改信息</el-button>
            <el-button type="danger" @click="showUpdatePasswdView">修改密码</el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card shadow="never" class="profile-card">
          <template #header>
            <div class="section-title">快捷操作</div>
          </template>
          <el-row :gutter="16">
            <el-col :span="8">
              <el-card shadow="never" class="mini-card" @click="showUpdateHrInfoView">
                <div class="mini-title">修改个人资料</div>
                <div class="mini-desc">更新昵称、电话、手机和地址</div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="never" class="mini-card" @click="showUpdatePasswdView">
                <div class="mini-title">修改登录密码</div>
                <div class="mini-desc">定期更新密码保障账号安全</div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="never" class="mini-card" @click="reloadProfile">
                <div class="mini-title">刷新信息</div>
                <div class="mini-desc">同步最新账号资料和头像</div>
              </el-card>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" title="修改用户信息" width="30%">
      <el-form ref="infoFormRef" :model="hr2" :rules="infoRules" label-width="100px">
        <el-form-item label="用户昵称" prop="name"><el-input v-model="hr2.name" /></el-form-item>
        <el-form-item label="电话号码" prop="telephone"><el-input v-model="hr2.telephone" /></el-form-item>
        <el-form-item label="手机号码" prop="phone"><el-input v-model="hr2.phone" /></el-form-item>
        <el-form-item label="用户地址" prop="address"><el-input v-model="hr2.address" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="updateHrInfo">确定</el-button></template>
    </el-dialog>

    <el-dialog v-model="passwdDialogVisible" title="修改密码" width="30%">
      <el-form ref="ruleFormRef" :model="ruleForm" status-icon :rules="rules" label-width="100px">
        <el-form-item label="请输入旧密码" prop="oldpass"><el-input v-model="ruleForm.oldpass" type="password" autocomplete="off" /></el-form-item>
        <el-form-item label="请输入新密码" prop="pass"><el-input v-model="ruleForm.pass" type="password" autocomplete="off" /></el-form-item>
        <el-form-item label="新确认密码" prop="checkPass"><el-input v-model="ruleForm.checkPass" type="password" autocomplete="off" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetForm">重置</el-button>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore, resolveUserface } from '@/store/userInfo'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRequest, putRequest, postRequest } from '@/utils/api'

const router = useRouter()
const userStore = useUserStore()
const hr = ref(null)
const hr2 = ref({ name: '', telephone: '', phone: '', address: '' })
const loading = ref(false)
const dialogVisible = ref(false)
const passwdDialogVisible = ref(false)
const ruleFormRef = ref(null)
const infoFormRef = ref(null)
const ruleForm = reactive({ oldpass: '', pass: '', checkPass: '' })
const avatarUrl = computed(() => resolveUserface(hr.value?.userface, hr.value?.avatarVersion))

const infoRules = {
  name: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号码', trigger: 'blur' }]
}
const validatePass = (rule, value, callback) => { if (!value) return callback(new Error('请输入密码')); if (ruleForm.checkPass !== '') ruleFormRef.value?.validateField('checkPass'); callback() }
const validatePass2 = (rule, value, callback) => { if (!value) return callback(new Error('请再次输入密码')); if (value !== ruleForm.pass) return callback(new Error('两次输入密码不一致!')); callback() }
const rules = { oldpass: [{ validator: validatePass, trigger: 'blur' }], pass: [{ validator: validatePass, trigger: 'blur' }], checkPass: [{ validator: validatePass2, trigger: 'blur' }] }

const initHr = async () => {
  loading.value = true
  try {
    const resp = await getRequest('/hr/info.do')
    const user = resp?.data?.data || resp?.data || resp
    const savedUser = userStore.user || {}
    const profile = { ...savedUser, ...user, avatarVersion: Date.now() }
    hr.value = profile
    hr2.value = { name: profile.name || '', telephone: profile.telephone || '', phone: profile.phone || '', address: profile.address || '' }
    userStore.setUser(profile)
  } finally {
    loading.value = false
  }
}
const reloadProfile = () => initHr()
const showUpdateHrInfoView = () => { dialogVisible.value = true }
const updateHrInfo = async () => {
  const valid = await infoFormRef.value?.validate?.().catch(() => false)
  if (valid === false) return
  const payload = { ...hr.value, ...hr2.value }
  const resp = await putRequest('/hr/info/update.do', payload)
  if (resp) {
    ElMessage.success('更新成功')
    dialogVisible.value = false
    await initHr()
  }
}
const showUpdatePasswdView = () => { passwdDialogVisible.value = true }
const resetForm = () => ruleFormRef.value?.resetFields()
const submitForm = () => { ruleFormRef.value?.validate(async (valid) => { if (!valid) return; const payload = { ...ruleForm, hrid: hr.value.id }; const resp = await putRequest('/hr/pass.do', payload); if (resp) { ElMessage.success('密码修改成功，请重新登录'); await handleLogout(false) } }) }
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) ElMessage.error('请上传图片文件')
  if (!isLt2M) ElMessage.error('头像图片大小不能超过 2MB')
  return isImage && isLt2M
}
const uploadAvatar = async ({ file }) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('id', hr.value.id)
  const resp = await postRequest('/hr/userface.do', formData)
  if (resp) {
    const responseData = resp?.data?.data || resp?.data || resp || {}
    const userface = responseData.userface || responseData.url || responseData.data?.userface || hr.value.userface
    const profile = { ...hr.value, userface, avatarVersion: Date.now() }
    hr.value = profile
    userStore.setUser(profile)
    ElMessage.success('头像更新成功')
    await initHr()
  }
}
const handleAvatarError = (event) => {
  const source = hr.value?.userface
  if (source && !String(source).startsWith('data:')) {
    event.target.src = URL.createObjectURL(new Blob())
  }
  ElMessage.error('头像加载失败，请刷新后重试')
}
const handleLogout = async (showConfirm = true) => {
  try {
    if (showConfirm) {
      await ElMessageBox.confirm('确定要注销当前登录吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    }
  } catch {
    return
  }
  userStore.logout()
  router.replace('/login')
}

onMounted(initHr)
</script>

<style scoped>
.hr-center-page { min-height: 100%; padding: 24px; background: linear-gradient(180deg, #f8fbff 0%, #ffffff 100%); }
.hero-card, .profile-card { border-radius: 16px; }
.hero-card { margin-bottom: 16px; }
.hero-content { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; }
.hero-actions { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.eyebrow { margin: 0 0 8px; font-size: 12px; color: var(--el-color-primary); letter-spacing: .12em; text-transform: uppercase; }
h2 { margin: 0; font-size: 24px; color: #1f2a37; }
.summary { margin: 10px 0 0; color: #5b6472; line-height: 1.7; }
.avatar-wrap { display: flex; justify-content: center; margin-bottom: 16px; }
.avatar { width: 100px; height: 100px; border-radius: 50%; object-fit: cover; }
.avatar-placeholder { width: 100px; height: 100px; border-radius: 50%; display: flex; align-items: center; justify-content: center; background: #f3f7fb; color: #6b7280; }
.profile-actions { display: flex; gap: 12px; justify-content: center; margin-top: 16px; flex-wrap: wrap; }
.section-title { font-weight: 600; }
.mini-card { cursor: pointer; border-radius: 12px; background: linear-gradient(135deg, #f7fbff, #ffffff); }
.mini-title { font-weight: 600; margin-bottom: 8px; }
.mini-desc { color: #6b7280; line-height: 1.6; }
</style>