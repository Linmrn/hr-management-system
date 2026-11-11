<template>
  <div class="dashboard">
    <div class="carousel-container">
      <el-carousel :interval="4000" height="400px" indicator-position="outside">
        <el-carousel-item v-for="item in carouselItems" :key="item.id">
          <div class="carousel-item-wrapper">
            <img :src="item.imageUrl" :alt="item.title" class="carousel-image" />
            <div class="carousel-caption">
              <h4>{{ item.title }}</h4>
              <p>{{ item.desc }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 快速管理通道 -->
    <h3 class="section-title">快速管理通道</h3>
    <div class="quick-actions">
      <el-row :gutter="20" justify="start">
        <el-col :span="4" v-for="item in quickActions" :key="item.path">
          <div class="action-card" @click="$router.push(item.path)">
            <i :class="item.icon" class="action-icon"></i>
            <span class="action-label">{{ item.label }}</span>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore, isAdminUser } from '@/store/userInfo'

const userStore = useUserStore()
const isAdmin = computed(() => isAdminUser(userStore.user))

const carouselItems = computed(() => [
  {
    id: 1,
    title: '员工资料',
    desc: '全面管理员工基本资料与高级资料，支持奖惩、培训、考评、调薪、调动等详细记录',
    imageUrl: 'https://images.unsplash.com/photo-1600880292203-757bb62b4baf?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80'
  },
  {
    id: 2,
    title: '人事管理',
    desc: '覆盖员工奖惩、培训、考评、调薪、调动全流程，支持模糊查询与批量操作',
    imageUrl: 'https://images.unsplash.com/photo-1552664730-d307ca884978?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80'
  },
  {
    id: 3,
    title: '工资管理',
    desc: '账套管理、员工薪酬设置、工资表生成、月末处理及历史工资查询一站完成',
    imageUrl: 'https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80'
  },
  {
    id: 4,
    title: '统计管理',
    desc: '综合信息统计、积分统计、人事信息统计与记录统计，多维度辅助决策分析',
    imageUrl: 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80'
  },
  ...(isAdmin.value ? [{
    id: 5,
    title: '系统管理',
    desc: '基础信息设置、操作员与权限管理、日志审计、数据备份恢复及数据库初始化',
    imageUrl: 'https://images.unsplash.com/photo-1558494949-ef010cbdcc31?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80'
  }] : [])
])

const quickActions = computed(() => [
  { path: '/home/emp/basic', icon: 'fa fa-address-card', label: '员工资料' },
  { path: '/home/per/emp', icon: 'fa fa-users', label: '人事管理' },
  { path: '/home/sal/sob', icon: 'fa fa-money', label: '工资管理' },
  { path: '/home/sta/all', icon: 'fa fa-bar-chart', label: '统计管理' },
  ...(isAdmin.value ? [{ path: '/home/sys/basic', icon: 'fa fa-cogs', label: '系统管理' }] : [])
])
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.carousel-container {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 5px 20px rgba(0,0,0,0.1);
  margin-bottom: 30px;
}

.carousel-item-wrapper {
  position: relative;
  width: 100%;
  height: 400px;
}

.carousel-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.carousel-caption {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0,0,0,0.6);
  color: #fff;
  padding: 15px 30px;
  border-radius: 10px;
  text-align: center;
}

.carousel-caption h4 {
  margin: 0 0 5px;
  font-size: 18px;
}

.carousel-caption p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #1A1D1F;
  margin-bottom: 20px;
}

.quick-actions {
  margin-top: 10px;
}

.action-card {
  background: #fff;
  padding: 30px 20px;
  border-radius: 24px;
  border: 1px solid #EDF0F2;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-card:hover {
  transform: translateY(-5px);
  border-color: #60A5FA;
  box-shadow: 0 10px 20px rgba(0,0,0,0.05);
}

.action-icon {
  font-size: 32px;
  display: block;
  margin-bottom: 15px;
  color: #000; /* 参考模板的黑色图标 */
}

.action-label {
  font-weight: 700;
  font-size: 14px;
  color: #1A1D1F;
}
</style>