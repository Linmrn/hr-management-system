<template>
  <div class="personnel-page">
    <el-card class="hero-card" shadow="never">
      <div class="hero-content">
        <div>
          <p class="eyebrow">人事管理</p>
          <h2>员工考评管理</h2>
          <p class="summary">统一维护员工考评记录，支持按员工筛选、分页查看、添加、编辑和删除，方便后续联动奖惩、调薪与培训。</p>
        </div>
        <div class="hero-actions">
          <el-button type="primary" @click="openAddDialog">新增考评记录</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="searchEid" placeholder="按员工姓名筛选" clearable filterable style="width: 220px">
            <el-option v-for="item in employeeOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
          <el-button type="primary" @click="loadRecords">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </div>
        <el-button type="success" plain @click="loadRecords">刷新</el-button>
      </div>
    </el-card>

    <el-table :data="records" border stripe v-loading="loading" class="table-card">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="员工姓名" width="120"><template #default="scope">{{ getEmployeeName(scope.row.eid) }}</template></el-table-column>
      <el-table-column prop="eid" label="员工ID" width="90" />
      <el-table-column prop="appdate" label="日期" width="130" :formatter="formatDate" />
      <el-table-column prop="appcontent" label="考评内容" min-width="240" show-overflow-tooltip />
      <el-table-column prop="appresult" label="结果" min-width="160" show-overflow-tooltip />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="scope">
          <el-button link type="primary" class="table-action-btn" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button link type="danger" class="table-action-btn delete-btn" @click="deleteRecord(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrap"><el-pagination background :current-page="page" :page-size="size" :total="total" layout="total, prev, pager, next, jumper" @current-change="handlePageChange" /></div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close>
      <el-form :model="form" label-width="90px">
        <el-form-item label="员工姓名">
          <el-select v-model="form.eid" placeholder="请选择员工" filterable style="width: 100%" @change="handleEmployeeChange">
            <el-option v-for="item in employeeOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="基本资料">
          <el-descriptions :column="1" border size="small" style="width: 100%">
            <el-descriptions-item label="工号">{{ selectedEmployee?.workID || '-' }}</el-descriptions-item>
            <el-descriptions-item label="部门">{{ selectedEmployee?.department?.name || '-' }}</el-descriptions-item>
            <el-descriptions-item label="职位">{{ selectedEmployee?.position?.name || '-' }}</el-descriptions-item>
            <el-descriptions-item label="职称">{{ selectedEmployee?.jobLevel?.name || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-form-item>
        <el-form-item label="日期"><el-date-picker v-model="form.appdate" type="date" style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.appcontent" placeholder="请输入考评内容" /></el-form-item>
        <el-form-item label="结果"><el-input v-model="form.appresult" type="textarea" :rows="3" placeholder="请输入考评结果" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="saveRecord">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'PerAppraise',
  data() { return { loading:false, records:[], total:0, page:1, size:10, searchEid:'', employeeOptions:[], employeeMap:{}, selectedEmployee:null, dialogVisible:false, dialogTitle:'新增考评记录', editId:null, form:this.createForm() } },
  mounted() { this.loadEmployees().then(() => this.loadRecords()) },
  methods: {
    createForm(){ return { eid:null, appdate:'', appcontent:'', appresult:'' } },
    extractPageData(resp){ return resp?.data?.data || resp?.data || resp },
    async loadEmployees(){ const resp = await this.$getRequest('/employee/basic/list.do?page=1&size=10000'); const pageData = this.extractPageData(resp); this.employeeOptions = pageData?.records || []; this.employeeMap = Object.fromEntries(this.employeeOptions.map(item => [item.id, item])); },
    getEmployeeName(eid){ return this.employeeMap[eid]?.name || `员工#${eid || '-'}` },
    handleEmployeeChange(eid){ this.selectedEmployee = this.employeeMap[eid] || null },
    loadRecords(){ this.loading=true; let url=`/employee/appraise/page.do?page=${this.page}&size=${this.size}`; if(this.searchEid) url+=`&eid=${encodeURIComponent(this.searchEid)}`; this.$getRequest(url).then(resp=>{ const pageData=this.extractPageData(resp); this.records=pageData?.records||[]; this.total=pageData?.count||pageData?.total||0; }).finally(()=>{ this.loading=false; }); },
    handlePageChange(page){ this.page=page; this.loadRecords(); },
    resetSearch(){ this.searchEid=''; this.page=1; this.loadRecords(); },
    openAddDialog(){ this.dialogTitle='新增考评记录'; this.editId=null; this.form=this.createForm(); this.selectedEmployee=null; this.form.appdate=this.formatDateTime(new Date()); this.dialogVisible=true; },
    openEditDialog(row){ this.dialogTitle='编辑考评记录'; this.editId=row.id; this.form={ eid:row.eid, appdate:this.formatDateTime(row.appdate), appcontent:row.appcontent||'', appresult:row.appresult||'' }; this.selectedEmployee=this.employeeMap[row.eid] || null; this.dialogVisible=true; },
    saveRecord(){ if(!this.form.eid) return this.$message.warning('请选择员工'); if(!this.form.appcontent) return this.$message.warning('请输入考评内容'); const payload={...this.form, eid:Number(this.form.eid), appdate:this.form.appdate || this.formatDateTime(new Date())}; const request=this.editId ? this.$putRequest('/employee/appraise/update.do',{ id:this.editId, ...payload }) : this.$postRequest('/employee/appraise/add.do', payload); request.then(resp=>{ if(resp){ this.$message.success(this.editId?'更新成功':'添加成功'); this.dialogVisible=false; this.page=1; this.loadRecords(); } }); },
    deleteRecord(row){ this.$confirm('此操作将永久删除该记录，是否继续？','提示',{confirmButtonText:'确定',cancelButtonText:'取消',type:'warning'}).then(()=>this.$deleteRequest(`/employee/appraise/delete.do?id=${row.id}`)).then(resp=>{ if(resp){ this.$message.success('删除成功'); this.loadRecords(); } }).catch(()=>this.$message.info('已取消删除')); },
    formatDate(row,col,cellValue){ return this.formatDateTime(cellValue,false); },
    formatDateTime(value, includeTime=true){ if(!value) return ''; const date=new Date(value); if(Number.isNaN(date.getTime())) return typeof value==='string' ? value : ''; const y=date.getFullYear(), m=String(date.getMonth()+1).padStart(2,'0'), d=String(date.getDate()).padStart(2,'0'), hh=String(date.getHours()).padStart(2,'0'), mm=String(date.getMinutes()).padStart(2,'0'), ss=String(date.getSeconds()).padStart(2,'0'); return includeTime ? `${y}-${m}-${d} ${hh}:${mm}:${ss}` : `${y}-${m}-${d}`; }
  }
}
</script>

<style scoped>
.personnel-page { min-height: 100%; padding: 24px; background: linear-gradient(180deg, #f8fbff 0%, #ffffff 100%); }
.hero-card, .toolbar-card { margin-bottom: 16px; border-radius: 16px; }
.hero-content, .toolbar, .toolbar-left { display:flex; align-items:center; justify-content:space-between; gap:12px; }
.hero-content { align-items:flex-start; }
.hero-actions { display:flex; align-items:center; gap:12px; }
.eyebrow { margin:0 0 8px; font-size:12px; color:var(--el-color-primary); letter-spacing:.12em; text-transform:uppercase; }
h2 { margin:0; font-size:24px; color:#1f2a37; }
.summary { margin:10px 0 0; max-width:720px; color:#5b6472; line-height:1.7; }
.table-card { border-radius:16px; }
.pagination-wrap { display:flex; justify-content:flex-end; margin-top:16px; }
.table-action-btn { font-weight:600; }
.delete-btn { margin-left:10px; }
</style>