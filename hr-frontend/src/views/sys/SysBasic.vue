<template>
  <div>
    <div class="sys-basic-header">
      <div>
        <h2>基础信息设置</h2>
        <p>管理部门、职位、职称与奖惩规则。</p>
      </div>
      <div class="sys-basic-actions">
        <el-button @click="goDeptHome">部门首页</el-button>
        <el-button @click="goPosHome">职位首页</el-button>
      </div>
    </div>
    <el-tabs v-model="activeName" type="card">
      <el-tab-pane label="部门管理" name="depmana"><DepMana /></el-tab-pane>
      <el-tab-pane label="职位管理" name="posmana"><PosMana /></el-tab-pane>
      <el-tab-pane label="职称管理" name="joblevelmana"><JobLevelMana /></el-tab-pane>
      <el-tab-pane label="奖惩规则" name="ecmana"><EcMana /></el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import DepMana from '@/components/sys/basic/DepMana.vue'
import PosMana from '@/components/sys/basic/PosMana.vue'
import JobLevelMana from '@/components/sys/basic/JobLevelMana.vue'
import EcMana from '@/components/sys/basic/EcMana.vue'

export default {
  name: 'SysBasic',
  data() {
    return { activeName: 'depmana' }
  },
  methods: {
    goDeptHome() {
      window.location.href = '/system/basic/department/home.do'
    },
    goPosHome() {
      window.location.href = '/system/basic/pos/home.do'
    }
  },
  components: {
    DepMana,
    PosMana,
    JobLevelMana,
    EcMana
  }
}
</script>

<style scoped>
.sys-basic-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  gap: 16px;
}

.sys-basic-header h2 {
  margin: 0 0 6px;
}

.sys-basic-header p {
  margin: 0;
  color: #666;
}

.sys-basic-actions {
  display: flex;
  gap: 10px;
}
</style>
