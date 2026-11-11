<template>
  <div class="salary-page">
    <el-card class="hero-card" shadow="never">
      <div class="hero-content">
        <div>
          <p class="eyebrow">工资管理</p>
          <h2>工资表查询</h2>
          <p class="summary">按员工和月份查询历史工资记录，查看某员工在某月的工资情况。</p>
        </div>
        <div class="hero-actions">
          <el-button type="success" plain @click="exportExcel">导出 Excel</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="employeeId" filterable clearable placeholder="员工姓名/工号" style="width: 240px" @change="handleEmployeeChange">
            <el-option v-for="item in employeeOptions" :key="item.id" :label="`${item.name}（${item.workID || item.id}）`" :value="item.id" />
          </el-select>
          <el-date-picker v-model="yearMonth" type="month" value-format="YYYY-MM" placeholder="选择月份" style="width: 180px" />
          <el-button type="primary" @click="loadRecords">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </div>
        <div class="toolbar-right">
          <el-tag effect="plain" type="info">{{ yearMonth || '请选择月份' }}</el-tag>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table :data="records" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="eid" label="员工ID" width="80" />
        <el-table-column label="员工姓名" width="120">
          <template #default="scope">{{ getEmployeeName(scope.row.eid) }}</template>
        </el-table-column>
        <el-table-column prop="yearMonth" label="年月" width="100" />
        <el-table-column prop="basicSalary" label="基本工资" width="90" />
        <el-table-column prop="bonus" label="奖金" width="70" />
        <el-table-column prop="lunchSalary" label="餐补" width="70" />
        <el-table-column prop="trafficSalary" label="交通补" width="70" />
        <el-table-column prop="allSalary" label="应发合计" width="90" />
        <el-table-column prop="pension" label="养老保险" width="90" />
        <el-table-column prop="medical" label="医疗保险" width="90" />
        <el-table-column prop="accumulationFund" label="公积金" width="80" />
        <el-table-column prop="tax" label="个税" width="70" />
        <el-table-column prop="actualSalary" label="实发工资" width="90" />
        <el-table-column label="状态" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" type="success">已结算</el-tag>
            <el-tag v-else type="warning">未结算</el-tag>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无数据" />
        </template>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'SalSearch',
  data() {
    return {
      loading: false,
      employeeOptions: [],
      employeeId: '',
      employeeName: '',
      yearMonth: '',
      records: []
    }
  },
  mounted() {
    this.loadEmployees()
  },
  methods: {
    extractPageData(resp) { return resp?.data?.data || resp?.data || resp },
    async loadEmployees() {
      const resp = await this.$getRequest('/employee/basic/list.do?page=1&size=10000')
      const data = this.extractPageData(resp)
      this.employeeOptions = data?.records || []
    },
    getEmployeeName(eid) {
      const emp = this.employeeOptions.find(item => String(item.id) === String(eid))
      return emp ? emp.name : `员工#${eid || '-'}`
    },
    handleEmployeeChange(eid) {
      const emp = this.employeeOptions.find(item => String(item.id) === String(eid))
      this.employeeName = emp ? emp.name : ''
    },
    loadRecords() {
      if (!this.employeeId || !this.yearMonth) {
        this.$message.warning('请选择员工和月份')
        return
      }
      this.loading = true
      const url = `/salary/table/list.do?page=1&size=20&yearMonth=${encodeURIComponent(this.yearMonth)}&keyword=${encodeURIComponent(this.employeeId)}`
      this.$getRequest(url).then(resp => {
        const data = this.extractPageData(resp) || {}
        const list = data.records || data.list || []
        this.records = list.filter(item => String(item.eid) === String(this.employeeId))
      }).finally(() => { this.loading = false })
    },
    resetSearch() {
      this.employeeId = ''
      this.employeeName = ''
      this.yearMonth = ''
      this.records = []
    },
    exportExcel() {
      if (!this.employeeId || !this.yearMonth) {
        this.$message.warning('请选择员工和月份')
        return
      }
      const url = `/salary/table/export.do?yearMonth=${encodeURIComponent(this.yearMonth)}&keyword=${encodeURIComponent(this.employeeId)}`
      window.open(url, '_blank')
    }
  }
}
</script>

<style scoped>
.salary-page { min-height: 100%; padding: 24px; background: linear-gradient(180deg, #f8fbff 0%, #ffffff 100%); }
.hero-card, .toolbar-card, .table-card { margin-bottom: 16px; border-radius: 16px; }
.hero-content, .toolbar, .toolbar-left { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.hero-content { align-items: flex-start; }
.hero-actions { display: flex; gap: 12px; }
.eyebrow { margin: 0 0 8px; font-size: 12px; color: var(--el-color-primary); letter-spacing: .12em; text-transform: uppercase; }
h2 { margin: 0; font-size: 24px; color: #1f2a37; }
.summary { margin: 10px 0 0; color: #5b6472; line-height: 1.7; }
.empty-state { padding: 24px 0; text-align: center; color: #909399; }
</style>