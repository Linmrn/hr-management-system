<template>
  <div class="sys-page">
    <el-card class="hero-card" shadow="never">
      <div class="hero-content">
        <div>
          <p class="eyebrow">系统管理</p>
          <h2>操作日志管理</h2>
          <p class="summary">记录管理员对系统数据的操作信息，支持分页查看与关键字检索。</p>
        </div>
        <el-tag type="warning" effect="plain">日志中心</el-tag>
      </div>
    </el-card>

    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="keyword" placeholder="按操作内容检索" clearable style="width: 320px" @keyup.enter="loadLogs" />
          <el-button type="primary" @click="loadLogs">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table :data="logs" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="hrid" label="操作员ID" width="100" />
        <el-table-column prop="operate" label="操作内容" min-width="360" show-overflow-tooltip />
        <el-table-column prop="adddate" label="时间" width="180" />
      </el-table>
      <div class="pagination-wrap">
        <el-pagination
          background
          :current-page="page"
          :page-size="size"
          :page-sizes="[10, 20, 30, 50]"
          layout="sizes, prev, pager, next, jumper, ->, total"
          :total="total"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </div>
      <div class="empty-state" v-if="!loading && !logs.length">暂无数据</div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'SysLog',
  data() {
    return {
      loading: false,
      keyword: '',
      logs: [],
      page: 1,
      size: 10,
      total: 0
    }
  },
  mounted() {
    this.loadLogs()
  },
  methods: {
    loadLogs() {
      this.loading = true
      let url = `/system/log/list.do?page=${this.page}&size=${this.size}`
      if (this.keyword) url += `&keyword=${encodeURIComponent(this.keyword)}`
      this.$getRequest(url).then(resp => {
        const data = resp?.data?.data || resp?.data || resp || {}
        this.logs = data.records || data.list || []
        this.total = data.total || data.count || 0
      }).finally(() => { this.loading = false })
    },
    handleCurrentChange(page) {
      this.page = page
      this.loadLogs()
    },
    handleSizeChange(size) {
      this.size = size
      this.page = 1
      this.loadLogs()
    },
    resetSearch() {
      this.keyword = ''
      this.page = 1
      this.loadLogs()
    }
  }
}
</script>

<style scoped>
.sys-page{min-height:100%;padding:24px;background:linear-gradient(180deg,#f8fbff 0%,#fff 100%)}
.hero-card,.toolbar-card,.table-card{margin-bottom:16px;border-radius:16px}.hero-content,.toolbar{display:flex;justify-content:space-between;gap:16px;align-items:flex-start}.toolbar{align-items:center}.eyebrow{margin:0 0 8px;font-size:12px;color:var(--el-color-primary);letter-spacing:.12em;text-transform:uppercase}h2{margin:0;font-size:24px;color:#1f2a37}.summary{margin:10px 0 0;color:#5b6472;line-height:1.7}.empty-state{padding:24px 0;text-align:center;color:#909399}
</style>