<template>
  <div>
    <div style="display:flex;gap:12px;align-items:center;margin-bottom:16px;flex-wrap:wrap;">
      <el-select v-model="eid" clearable filterable placeholder="选择员工" style="width:220px">
        <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <el-table :data="records" border stripe v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="eid" label="员工ID" width="80" />
      <el-table-column label="员工姓名" width="120">
        <template #default="scope">{{ getEmployeeName(scope.row.eid) }}</template>
      </el-table-column>
      <el-table-column prop="ecdate" label="日期" width="180" />
      <el-table-column prop="ectype" label="类型" width="90" />
      <el-table-column prop="ecreason" label="原因" min-width="220" />
      <el-table-column prop="ecpoint" label="分值" width="80" />
      <el-table-column prop="remark" label="备注" min-width="180" />
    </el-table>

    <div style="display:flex;justify-content:flex-end;margin-top:16px;">
      <el-pagination
        background
        layout="total, prev, pager, next, sizes"
        :total="total"
        :page-size="pageSize"
        :current-page="page"
        :page-sizes="[5,10,20,50]"
        @current-change="handleCurrentChange"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script>
export default {
  name: 'EcMana',
  data() {
    return {
      loading: false,
      employees: [],
      records: [],
      eid: '',
      page: 1,
      pageSize: 10,
      total: 0
    }
  },
  mounted() {
    this.loadEmployees();
    this.loadData();
  },
  methods: {
    extractData(resp) { return resp?.data?.data || resp?.data || resp || {} },
    loadEmployees() {
      this.$getRequest('/employee/basic/list.do?page=1&size=10000').then(resp => {
        const data = this.extractData(resp)
        this.employees = data.records || data.list || []
      })
    },
    getEmployeeName(id) {
      const emp = this.employees.find(item => String(item.id) === String(id))
      return emp ? emp.name : `员工#${id || '-'}`
    },
    loadData() {
      this.loading = true
      let url = `/employee/ec/page.do?page=${this.page}&size=${this.pageSize}`
      if (this.eid) url += `&eid=${this.eid}`
      this.$getRequest(url).then(resp => {
        const data = this.extractData(resp)
        this.records = data.records || []
        this.total = data.count || data.total || this.records.length
      }).finally(() => { this.loading = false })
    },
    handleCurrentChange(page) { this.page = page; this.loadData() },
    handleSizeChange(size) { this.pageSize = size; this.page = 1; this.loadData() },
    resetSearch() { this.eid = ''; this.page = 1; this.loadData() }
  }
}
</script>
