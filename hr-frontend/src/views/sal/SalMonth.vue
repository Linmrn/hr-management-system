<template>
  <div class="salary-page">
    <el-card class="hero-card" shadow="never">
      <div class="hero-content">
        <div>
          <p class="eyebrow">工资管理</p>
          <h2>工资表管理</h2>
          <p class="summary">对当月员工工资进行增删改查，并支持结算状态控制与导出。</p>
        </div>
        <div class="hero-actions">
          <el-button type="primary" @click="openAddDialog">新增工资记录</el-button>
          <el-button type="success" plain @click="exportExcel">导出 Excel</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="searchYearMonth" placeholder="请输入年月，如 2026-07" clearable style="width: 220px" />
          <el-input v-model="keyword" placeholder="员工姓名或工号" clearable style="width: 220px" />
          <el-button type="primary" @click="loadRecords">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </div>
        <div class="toolbar-right">
          <el-button type="success" plain @click="loadRecords">刷新</el-button>
        </div>
      </div>
    </el-card>

    <el-table :data="records" border stripe v-loading="loading" class="table-card">
      <el-table-column prop="eid" label="员工ID" width="80" />
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
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteRecord(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrap">
      <el-pagination background :current-page="page" :page-size="size" :page-sizes="[10, 20, 30, 50]" layout="sizes, prev, pager, next, jumper, ->, total" :total="total" @current-change="handleCurrentChange" @size-change="handleSizeChange" />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="720px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="员工ID">
              <el-input v-model="form.eid" type="number" :disabled="isEdit && form.status === 1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年月">
              <el-input v-model="form.yearMonth" placeholder="如 2026-07" :disabled="isEdit && form.status === 1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="基本工资"><el-input-number v-model="form.basicSalary" :min="0" controls-position="right" style="width: 100%" :disabled="form.status === 1" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="奖金"><el-input-number v-model="form.bonus" :min="0" controls-position="right" style="width: 100%" :disabled="form.status === 1" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="餐补"><el-input-number v-model="form.lunchSalary" :min="0" controls-position="right" style="width: 100%" :disabled="form.status === 1" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交通补"><el-input-number v-model="form.trafficSalary" :min="0" controls-position="right" style="width: 100%" :disabled="form.status === 1" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="养老保险"><el-input-number v-model="form.pension" :min="0" controls-position="right" style="width: 100%" :disabled="form.status === 1" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="医疗保险"><el-input-number v-model="form.medical" :min="0" controls-position="right" style="width: 100%" :disabled="form.status === 1" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公积金"><el-input-number v-model="form.accumulationFund" :min="0" controls-position="right" style="width: 100%" :disabled="form.status === 1" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="个税"><el-input-number v-model="form.tax" :min="0" controls-position="right" style="width: 100%" :disabled="form.status === 1" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status" :disabled="isEdit && form.status === 1">
                <el-radio :value="0">未结算</el-radio>
                <el-radio :value="1">已结算</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRecord">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="bulkVisible" title="批量修改工资记录" width="720px" destroy-on-close>
      <el-form :model="bulkForm" label-width="100px">
        <el-alert :title="`已选中 ${selectedRows.length} 条记录，修改项会批量应用。`" type="info" :closable="false" style="margin-bottom: 16px" />
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="年月"><el-input v-model="bulkForm.yearMonth" placeholder="批量修改年月" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="基本工资"><el-input-number v-model="bulkForm.basicSalary" :min="0" controls-position="right" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="奖金"><el-input-number v-model="bulkForm.bonus" :min="0" controls-position="right" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="餐补"><el-input-number v-model="bulkForm.lunchSalary" :min="0" controls-position="right" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="交通补"><el-input-number v-model="bulkForm.trafficSalary" :min="0" controls-position="right" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="养老保险"><el-input-number v-model="bulkForm.pension" :min="0" controls-position="right" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="医疗保险"><el-input-number v-model="bulkForm.medical" :min="0" controls-position="right" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="公积金"><el-input-number v-model="bulkForm.accumulationFund" :min="0" controls-position="right" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="个税"><el-input-number v-model="bulkForm.tax" :min="0" controls-position="right" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-radio-group v-model="bulkForm.status"><el-radio :value="0">未结算</el-radio><el-radio :value="1">已结算</el-radio></el-radio-group></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="bulkVisible = false">取消</el-button>
        <el-button type="primary" @click="saveBulkEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'SalMonth',
  data() {
    return {
      loading: false,
      records: [],
      total: 0,
      page: 1,
      size: 10,
      searchYearMonth: '',
      keyword: '',
      dialogVisible: false,
      isEdit: false,
      dialogTitle: '添加工资记录',
      form: this.createForm()
    }
  },
  mounted() { this.loadRecords() },
  methods: {
    createForm() {
      return { id: null, eid: null, yearMonth: '', basicSalary: 0, bonus: 0, lunchSalary: 0, trafficSalary: 0, pension: 0, medical: 0, accumulationFund: 0, tax: 0, status: 0 }
    },
    extractPageData(resp) { return resp?.data?.data || resp?.data || resp },
    loadRecords() {
      this.loading = true
      let url = `/salary/table/list.do?page=${this.page}&size=${this.size}`
      if (this.searchYearMonth) url += `&yearMonth=${encodeURIComponent(this.searchYearMonth)}`
      if (this.keyword) url += `&keyword=${encodeURIComponent(this.keyword)}`
      this.$getRequest(url).then(resp => {
        const data = this.extractPageData(resp) || {}
        this.records = data.records || data.list || []
        this.total = data.total || data.count || 0
      }).finally(() => { this.loading = false })
    },
    resetSearch() {
      this.searchYearMonth = ''
      this.keyword = ''
      this.page = 1
      this.loadRecords()
    },
    handleCurrentChange(page) { this.page = page; this.loadRecords() },
    handleSizeChange(size) { this.size = size; this.page = 1; this.loadRecords() },
    openAddDialog() {
      this.isEdit = false
      this.dialogTitle = '添加工资记录'
      this.form = this.createForm()
      this.dialogVisible = true
    },
    openEditDialog(row) {
      if (row.status === 1) return this.$message.warning('已结算的记录不能编辑')
      this.isEdit = true
      this.dialogTitle = '编辑工资记录'
      this.form = { ...this.createForm(), ...row }
      this.dialogVisible = true
    },
    saveRecord() {
      if (!this.form.eid || !this.form.yearMonth) return this.$message.warning('请填写员工ID和年月')
      const payload = {
        ...this.form,
        eid: Number(this.form.eid),
        basicSalary: Number(this.form.basicSalary),
        bonus: Number(this.form.bonus),
        lunchSalary: Number(this.form.lunchSalary),
        trafficSalary: Number(this.form.trafficSalary),
        pension: Number(this.form.pension),
        medical: Number(this.form.medical),
        accumulationFund: Number(this.form.accumulationFund),
        tax: Number(this.form.tax),
        status: Number(this.form.status)
      }
      const request = this.isEdit ? this.$putRequest('/salary/table/update.do', payload) : this.$postRequest('/salary/table/add.do', payload)
      request.then(resp => {
        if (resp) {
          this.$message.success(this.isEdit ? '更新成功' : '添加成功')
          this.dialogVisible = false
          this.loadRecords()
        }
      })
    },
    deleteRecord(row) {
      if (row.status === 1) return this.$message.warning('已结算的记录不能删除')
      this.$confirm('此操作将永久删除该工资记录, 是否继续?', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
        .then(() => this.$deleteRequest('/salary/table/deleteRecord.do?id=' + row.id))
        .then(resp => { if (resp) { this.$message.success('删除成功'); this.loadRecords() } })
        .catch(() => this.$message.info('已取消删除'))
    },
    exportExcel() {
      let url = '/salary/table/export.do'
      if (this.searchYearMonth) url += '?yearMonth=' + encodeURIComponent(this.searchYearMonth)
      if (this.keyword) url += (this.searchYearMonth ? '&' : '?') + 'keyword=' + encodeURIComponent(this.keyword)
      window.open(url, '_blank')
    }
  }
}
</script>

<style scoped>
.salary-page { min-height: 100%; padding: 24px; background: linear-gradient(180deg, #f8fbff 0%, #ffffff 100%); }
.hero-card, .toolbar-card { margin-bottom: 16px; border-radius: 16px; }
.hero-content, .toolbar, .toolbar-left { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.hero-content { align-items: flex-start; }
.hero-actions { display: flex; gap: 12px; }
.eyebrow { margin: 0 0 8px; font-size: 12px; color: var(--el-color-primary); letter-spacing: .12em; text-transform: uppercase; }
h2 { margin: 0; font-size: 24px; color: #1f2a37; }
.summary { margin: 10px 0 0; color: #5b6472; line-height: 1.7; }
.table-card { border-radius: 16px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
.toolbar-right { display: flex; gap: 12px; }
</style>