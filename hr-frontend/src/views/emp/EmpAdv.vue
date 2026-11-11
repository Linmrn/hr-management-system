<template>
  <div class="emp-adv">
    <el-card class="adv-shell" shadow="never">
      <el-tabs v-model="activeTab" type="border-card">
      <!-- ==================== 奖惩资料 ==================== -->
      <el-tab-pane label="奖惩资料" name="ec">
        <div class="tab-header">
          <el-button type="primary" size="small" @click="openEcDialog()">添加奖惩</el-button>
        </div>
        <el-table :data="ecList" border stripe v-loading="ecLoading">
          <el-table-column prop="ecdate" label="日期" width="140" />
          <el-table-column prop="ecreason" label="原因" />
          <el-table-column prop="ecpoint" label="分数" width="100">
            <template #default="{ row }">
              <el-tag :type="row.ecpoint >= 0 ? 'success' : 'danger'">
                {{ row.ecpoint }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="ectype" label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="row.ectype === 0 ? 'success' : 'danger'">
                {{ row.ectype === 0 ? '奖励' : '惩罚' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" />
          <el-table-column label="操作" width="120" align="center">
            <template #default="{ row }">
              <el-button type="danger" size="small" @click="deleteEc(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- ==================== 培训资料 ==================== -->
      <el-tab-pane label="培训资料" name="train">
        <div class="tab-header">
          <el-button type="primary" size="small" @click="$message.info('开发中')">添加培训</el-button>
        </div>
        <el-empty description="培训资料功能开发中" />
      </el-tab-pane>

      <!-- ==================== 考评资料 ==================== -->
      <el-tab-pane label="考评资料" name="appraise">
        <div class="tab-header">
          <el-button type="primary" size="small" @click="$message.info('开发中')">添加考评</el-button>
        </div>
        <el-empty description="考评资料功能开发中" />
      </el-tab-pane>

      <!-- ==================== 调薪资料 ==================== -->
      <el-tab-pane label="调薪资料" name="adjust">
        <div class="tab-header">
          <el-button type="primary" size="small" @click="$message.info('开发中')">添加调薪</el-button>
        </div>
        <el-empty description="调薪资料功能开发中" />
      </el-tab-pane>

      <!-- ==================== 调动资料 ==================== -->
      <el-tab-pane label="调动资料" name="remove">
        <div class="tab-header">
          <el-button type="primary" size="small" @click="$message.info('开发中')">添加调动</el-button>
        </div>
        <el-empty description="调动资料功能开发中" />
      </el-tab-pane>

      <!-- ==================== 工资信息 ==================== -->
      <el-tab-pane label="工资信息" name="salary">
        <div class="salary-info" v-if="salaryData">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="账套名称">{{ salaryData.name || '-' }}</el-descriptions-item>
            <el-descriptions-item label="基本工资">{{ salaryData.basicSalary || 0 }}</el-descriptions-item>
            <el-descriptions-item label="奖金">{{ salaryData.bonus || 0 }}</el-descriptions-item>
            <el-descriptions-item label="午餐补助">{{ salaryData.lunchSalary || 0 }}</el-descriptions-item>
            <el-descriptions-item label="交通补助">{{ salaryData.trafficSalary || 0 }}</el-descriptions-item>
            <el-descriptions-item label="应发工资">{{ salaryData.allSalary || 0 }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <el-empty v-else description="该员工暂无工资账套" />
      </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- ==================== 添加奖惩弹窗 ==================== -->
    <el-dialog v-model="ecDialogVisible" title="添加奖惩记录" width="500px">
      <el-form :model="ecForm" label-width="80px" size="default">
        <el-form-item label="日期">
          <el-date-picker v-model="ecForm.ecdate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="原因">
          <el-input v-model="ecForm.ecreason" placeholder="请输入奖惩原因" />
        </el-form-item>
        <el-form-item label="分数">
          <el-input-number v-model="ecForm.ecpoint" :min="-100" :max="100" />
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="ecForm.ectype">
            <el-radio :value="0">奖励</el-radio>
            <el-radio :value="1">惩罚</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="ecForm.remark" placeholder="备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="ecDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEc" :loading="ecSubmitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getRequest, postRequest, deleteRequest } from '@/utils/api'

export default {
  name: 'EmpAdv',
  data() {
    return {
      activeTab: 'ec',
      employeeId: null,

      // 奖惩
      ecList: [],
      ecLoading: false,
      ecDialogVisible: false,
      ecSubmitting: false,
      ecForm: {
        eid: null,
        ecdate: '',
        ecreason: '',
        ecpoint: 0,
        ectype: 0,
        remark: ''
      }
    }
  },
  mounted() {
    this.employeeId = this.$route.query.id ? parseInt(this.$route.query.id) : null
    if (this.employeeId) {
      this.loadEcList()
      this.loadSalaryInfo()
    }
  },
  methods: {
    // ==================== 奖惩 ====================
    async loadEcList() {
      if (!this.employeeId) return
      this.ecLoading = true
      try {
        const res = await getRequest('/employee/basic/reward/list.do', { eid: this.employeeId })
        this.ecList = res?.data || []
        console.log('奖惩列表:', this.ecList)  // 添加这行查看控制台输出
      } catch (error) {
        console.error('加载奖惩列表失败', error)
        this.$message.error('加载失败')
      } finally {
        this.ecLoading = false
      }
    },

    openEcDialog() {
      this.ecForm = {
        eid: this.employeeId,
        ecdate: '',
        ecreason: '',
        ecpoint: 0,
        ectype: 0,
        remark: ''
      }
      this.ecDialogVisible = true
    },

    async submitEc() {
      if (!this.ecForm.ecdate) {
        this.$message.warning('请选择日期')
        return
      }
      if (!this.ecForm.ecreason) {
        this.$message.warning('请输入原因')
        return
      }
      this.ecSubmitting = true
      try {
        await postRequest('/employee/basic/reward/add.do', this.ecForm)
        this.$message.success('添加成功')
        this.ecDialogVisible = false
        this.loadEcList()
      } catch (error) {
        console.error('添加失败', error)
        this.$message.error('添加失败')
      } finally {
        this.ecSubmitting = false
      }
    },

    async deleteEc(id) {
      if (!confirm('确认删除该记录吗？')) return
      try {
        await deleteRequest('/employee/basic/reward/delete.do', { id })
        this.$message.success('删除成功')
        this.loadEcList()
      } catch (error) {
        console.error('删除失败', error)
        this.$message.error('删除失败')
      }
    },

    // ==================== 工资信息 ====================
    async loadSalaryInfo() {
      if (!this.employeeId) return
      try {
        const res = await getRequest('/employee/salary/info.do', { eid: this.employeeId })
        this.salaryData = res?.data || null
      } catch (error) {
        console.error('加载工资信息失败', error)
      }
    }
  }
}
</script>

<style scoped>
.emp-adv {
  padding: 20px;
}
.adv-shell {
  border-radius: 16px;
}
.tab-header {
  margin-bottom: 12px;
  display: flex;
  justify-content: flex-end;
}
.salary-info {
  padding: 6px 0 0;
}
:deep(.el-tabs--border-card > .el-tabs__content) {
  padding: 12px;
}
:deep(.el-descriptions__body .el-descriptions__table) {
  font-size: 13px;
}
</style>