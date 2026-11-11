<template>
  <el-container>
    <el-header class="homeHeader">
      <div class="header-left">
        <el-button link @click="collapse = !collapse" style="color: #ffffff; font-size: 20px;">
          <i :class="collapse ? 'fa fa-indent' : 'fa fa-outdent'"></i>
        </el-button>
        <span class="title">人事管理系统</span>
      </div>
      <div style="display: flex; align-items: center;">
        <el-dropdown @command="commandHandler">
          <span class="el-dropdown-link">
            {{ user?.name }}
            <el-tag v-if="isAdmin" type="danger" effect="dark" size="small" style="margin-left: 8px;">管理员</el-tag>
            <el-tag v-else type="info" effect="dark" size="small" style="margin-left: 8px;">普通HR</el-tag>
            <el-avatar :src="avatarUrl" :size="40" style="margin-left: 8px;" />
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="userinfo">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided>注销登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>
      <el-aside :width="collapse ? '64px' : '200px'" class="home-aside">
        <el-menu
            :router="true"
            :default-active="route.path"
            :collapse="collapse"
            :collapse-transition="false"
            unique-opened
            background-color="#ffffff"
            text-color="#5b6472"
            active-text-color="#8fb8ff"
        >
          <template v-for="(item, index) in filteredRoutes" :key="index">
            <!-- 有子菜单 -->
            <el-sub-menu :index="String(index)" v-if="item.children && item.children.length">
              <template #title>
                <i v-if="item.iconCls" :class="item.iconCls" style="margin-right: 5px;"></i>
                <span>{{ item.name }}</span>
              </template>
              <el-menu-item v-for="(child, childIndex) in item.children" :key="childIndex" :index="child.path">
                <i v-if="child.iconCls" :class="child.iconCls"></i>
                <span>{{ child.name }}</span>
              </el-menu-item>
            </el-sub-menu>
            <!-- 无子菜单 -->
            <el-menu-item v-else :index="item.path">
              <i v-if="item.iconCls" :class="item.iconCls"></i>
              <span>{{ item.name }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore, isAdminUser, resolveUserface } from '@/store/userInfo'
import { initMenu } from '@/utils/menus'
import { postRequest, getRequest } from '@/utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const collapse = ref(false)

const user = computed(() => userStore.user)
const avatarUrl = computed(() => resolveUserface(user.value?.userface, user.value?.avatarVersion))
const isAdmin = computed(() => isAdminUser(userStore.user))

const filteredRoutes = computed(() => {
  const menus = userStore.menus.filter(item => !item.hidden)

  if (isAdmin.value) {
    return menus
  }

  return menus
    .map(item => {
      if (item.name !== '系统管理' || !Array.isArray(item.children)) {
        return item
      }

      return {
        ...item,
        children: []
      }
    })
    .filter(item => item.name !== '系统管理' || item.children.length > 0)
})

onMounted(async () => {
  if (userStore.menus.length === 0) {
    await initMenu(router)
  }

  if (route.path === '/home') {
    router.replace('/home/dashboard')
  }
})

const commandHandler = async (cmd) => {
  if (cmd === 'logout') {
    try {
      await ElMessageBox.confirm('此操作将注销登录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await postRequest('/logout.do')
      userStore.logout()
      router.replace('/login')
    } catch (error) {
      if (error !== 'cancel') {
        console.error('注销失败:', error)
      }
    }
  } else if (cmd === 'userinfo') {
    router.push('/home/hrinfo')
  }
}
</script>

<style scoped>
.homeHeader {
  background-color: #60A5FA;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;
  box-sizing: border-box;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-left .title {
  font-size: 22px;
  color: #ffffff;
  margin-left: 10px;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  color: #ffffff;
  cursor: pointer;
}

.homeHeader :deep(.el-button.is-link) {
  color: #ffffff;
}

.home-aside {
  background-color: #ffffff;
  border-right: 1px solid #EDF0F2;
  transition: width 0.3s;
  overflow: hidden;
}

.home-aside .el-menu {
  border-right: none;
  padding: 10px;
  background-color: #ffffff;
}
.home-aside .el-menu-item,
.home-aside .el-sub-menu .el-sub-menu__title {
  border-radius: 14px;
  margin-bottom: 5px;
  color: #5b6472;
  font-weight: 600;
}
.home-aside .el-menu-item.is-active,
.home-aside .el-sub-menu.is-active > .el-sub-menu__title {
  background-color: #60A5FA;
  color: #ffffff;
}
.home-aside .el-menu-item:hover:not(.is-active),
.home-aside .el-sub-menu .el-sub-menu__title:hover {
  background-color: rgba(143, 184, 255, 0.12);
}
.el-main {
  background-color: #f7fbff;
  min-height: calc(100vh - 60px);
}
</style>