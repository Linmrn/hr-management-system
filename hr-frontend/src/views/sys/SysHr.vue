<template>
  <div class="sys-page">
    <el-card class="hero-card" shadow="never">
      <div class="hero-content">
        <div>
          <p class="eyebrow">系统管理</p>
          <h2>操作员管理</h2>
          <p class="summary">管理系统登录账号、启用状态与角色权限，支持新增、编辑、删除、角色分配。</p>
        </div>
        <el-button type="primary" @click="showAddDialog">新增操作员</el-button>
      </div>
    </el-card>

    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar">
        <el-input v-model="keywords" placeholder="通过用户名搜索用户" clearable style="width: 360px" @keyup.enter="doSearch" />
        <div>
          <el-button type="primary" @click="doSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table :data="hrs" border stripe v-loading="loading" style="width:100%">
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="phone" label="手机号码" width="140" />
        <el-table-column prop="telephone" label="电话" width="140" />
        <el-table-column prop="address" label="地址" min-width="180" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.enabled" type="success">启用</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="角色" min-width="220">
          <template #default="scope">
            <el-tag v-for="role in scope.row.roles || []" :key="role.id" type="success" style="margin-right: 4px">{{ role.nameZh }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="editHr(scope.row)">编辑</el-button>
            <el-button link type="primary" @click="showRoleDialog(scope.row)">角色</el-button>
            <el-button link type="danger" @click="deleteHr(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager-wrap">
        <el-pagination
          background
          layout="total, prev, pager, next, sizes"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          :page-sizes="[5, 10, 20, 50]"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="editVisible" title="编辑操作员" width="520px">
      <el-form :model="editForm" label-width="90px">
        <el-form-item label="姓名"><el-input v-model="editForm.name" /></el-form-item>
        <el-form-item label="用户名"><el-input v-model="editForm.username" /></el-form-item>
        <el-form-item label="手机号码"><el-input v-model="editForm.phone" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="editForm.telephone" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="editForm.address" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="editForm.remark" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="editForm.enabled" active-text="启用" inactive-text="禁用" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleVisible" title="分配角色" width="420px">
      <el-select v-model="selectedRoles" multiple placeholder="请选择角色" style="width:100%">
        <el-option v-for="r in allroles" :key="r.id" :label="r.nameZh" :value="r.id" />
      </el-select>
      <template #footer>
        <el-button @click="roleVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRoles">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="addVisible" title="新增操作员" width="520px">
      <el-form :model="addForm" label-width="90px">
        <el-form-item label="姓名"><el-input v-model="addForm.name" /></el-form-item>
        <el-form-item label="用户名"><el-input v-model="addForm.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="addForm.password" type="password" /></el-form-item>
        <el-form-item label="手机号码"><el-input v-model="addForm.phone" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="addForm.telephone" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="addForm.address" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAdd">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'SysHr',
  data() {
    return {
      loading: false,
      hrs: [],
      total: 0,
      pageSize: 10,
      currentPage: 1,
      keywords: '',
      allroles: [],
      selectedRoles: [],
      roleTarget: null,
      roleVisible: false,
      editVisible: false,
      addVisible: false,
      addForm: { name: '', username: '', password: '123456', phone: '', telephone: '', address: '', enabled: true },
      editForm: { id: null, name: '', username: '', phone: '', telephone: '', address: '', remark: '', enabled: true }
    }
  },
  mounted() {
    this.loadRoles()
    this.loadHrs()
  },
  methods: {
    extractData(resp) { return resp?.data?.data || resp?.data || resp || {} },
    loadRoles() {
      this.$getRequest('/system/hr/roles.do').then(resp => {
        const data = this.extractData(resp)
        this.allroles = Array.isArray(data) ? data : (data?.records || data || [])
      })
    },
    loadHrs() {
      this.loading = true
      this.$getRequest(`/system/hr/list.do?keywords=${encodeURIComponent(this.keywords)}&page=${this.currentPage}&size=${this.pageSize}`).then(resp => {
        const data = this.extractData(resp)
        const list = Array.isArray(data) ? data : (data.records || data.list || [])
        this.hrs = list
        this.total = data.total || data.count || list.length
      }).finally(() => { this.loading = false })
    },
    handleCurrentChange(page) { this.currentPage = page; this.loadHrs() },
    handleSizeChange(size) { this.pageSize = size; this.currentPage = 1; this.loadHrs() },
    doSearch() { this.currentPage = 1; this.loadHrs() },
    resetSearch() { this.keywords = ''; this.doSearch() },
    showAddDialog() { this.addForm = { name: '', username: '', password: '123456', phone: '', telephone: '', address: '', enabled: true }; this.addVisible = true },
    saveAdd() {
      this.$postRequest('/system/hr/add.do', this.addForm).then(resp => {
        if (resp) { this.addVisible = false; this.loadHrs() }
      })
    },
    editHr(hr) { this.editForm = { ...hr }; this.editVisible = true },
    saveEdit() {
      this.$putRequest('/system/hr/update.do', this.editForm).then(resp => {
        if (resp) { this.editVisible = false; this.loadHrs() }
      })
    },
    deleteHr(hr) {
      this.$confirm(`此操作将永久删除【${hr.name || hr.username}】, 是否继续?`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
        .then(() => this.$deleteRequest('/system/hr/delete.do?id=' + hr.id))
        .then(resp => { if (resp) this.loadHrs() })
    },
    showRoleDialog(hr) {
      this.roleTarget = hr
      this.selectedRoles = (hr.roles || []).map(r => r.id)
      this.roleVisible = true
    },
    saveRoles() {
      if (!this.roleTarget) return
      let url = '/system/hr/role.do?hrid=' + this.roleTarget.id
      this.selectedRoles.forEach(id => { url += '&rids=' + id })
      this.$putRequest(url).then(resp => {
        if (resp) { this.roleVisible = false; this.loadHrs() }
      })
    }
  }
}
</script>

<style scoped>
.sys-page{min-height:100%;padding:24px;background:linear-gradient(180deg,#f8fbff 0%,#fff 100%)}
.hero-card,.toolbar-card,.table-card{margin-bottom:16px;border-radius:16px}.hero-content,.toolbar{display:flex;justify-content:space-between;gap:16px;align-items:center}.eyebrow{margin:0 0 8px;font-size:12px;color:var(--el-color-primary);letter-spacing:.12em;text-transform:uppercase}h2{margin:0;font-size:24px;color:#1f2a37}.summary{margin:10px 0 0;color:#5b6472;line-height:1.7}.pager-wrap{display:flex;justify-content:flex-end;margin-top:16px}
</style>
