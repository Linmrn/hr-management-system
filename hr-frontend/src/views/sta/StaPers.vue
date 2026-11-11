<template>
  <!-- 筛选条件 -->
  <div class="filter-bar">
    <el-form :inline="true" size="default" class="filter-form">
      <el-form-item label="性别">
        <el-select
            v-model="filters.gender"
            placeholder="全部"
            clearable
            style="width: 140px"
        >
          <el-option label="男" value="男" />
          <el-option label="女" value="女" />
        </el-select>
      </el-form-item>

      <el-form-item label="部门">
        <el-select
            v-model="filters.deptId"
            placeholder="全部"
            clearable
            style="width: 220px"
        >
          <el-option
              v-for="item in deptList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="职称">
        <el-select
            v-model="filters.jobLevelId"
            placeholder="全部"
            clearable
            style="width: 200px"
        >
          <el-option
              v-for="item in jobLevelList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="loadData" :loading="loading">查询</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </el-form-item>
    </el-form>
  </div>

    <!-- 饼图区域  -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="chart-title">性别分布</div>
          <div id="genderChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="chart-title">部门分布（Top 5）</div>
          <div id="deptChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="chart-title">职称分布（Top 5）</div>
          <div id="jobChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  <div class="sta-pers">

    <!-- 统计信息 -->
    <div class="stats-info">共 <strong>{{ total }}</strong> 条记录</div>

    <!-- 数据表格 -->
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="员工ID" width="90" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="80" />
      <el-table-column prop="deptName" label="部门" />
      <el-table-column prop="jobLevelName" label="职称" />
      <el-table-column prop="posName" label="职位" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="workID" label="工号" width="120" />
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadData"
          @size-change="loadData"
      />
    </div>
  </div>
</template>

<script>
import { getRequest } from '@/utils/api'
import * as echarts from 'echarts'

export default {
  data() {
    return {
      filters: { gender: '', deptId: '', jobLevelId: '' },
      deptList: [],
      jobLevelList: [],
      tableData: [],
      total: 0,
      page: 1,
      size: 10,
      loading: false
    }
  },
  mounted() {
    this.loadOptions()
    this.loadData()
  },
  methods: {
    async loadOptions() {
      try {
        const [deptRes, jobRes] = await Promise.all([
          getRequest('/employee/basic/deps.do'),
          getRequest('/employee/basic/joblevels.do')
        ])
        this.deptList = deptRes?.data || []
        this.jobLevelList = jobRes?.data || []
      } catch (error) {
        console.error('加载选项失败', error)
      }
    },
    async loadData() {
      this.loading = true
      try {
        const params = { ...this.filters, page: this.page, size: this.size }
        const res = await getRequest('/statistics/personnel.do', params)
        const data = res?.data || {}
        this.tableData = data.data || []
        this.total = data.total || 0
        this.$nextTick(() => {
          this.renderCharts()
        })
      } catch (error) {
        console.error('加载人事信息失败', error)
        this.$message.error('加载失败')
      } finally {
        this.loading = false
      }
    },
    renderCharts() {
      // 性别分布
      const genderChart = echarts.init(document.getElementById('genderChart'))
      const genderMap = {}
      this.tableData.forEach(item => {
        genderMap[item.gender] = (genderMap[item.gender] || 0) + 1
      })
      const genderData = Object.keys(genderMap).map(key => ({
        name: key || '未知',
        value: genderMap[key]
      }))
      genderChart.setOption({
        tooltip: { trigger: 'item' },
        legend: {
          orient: 'vertical',
          right: '2%',
          top: 'center',
          itemWidth: 12,
          itemHeight: 12
        },
        series: [{
          type: 'pie',
          radius: ['34%', '58%'],
          center: ['36%', '48%'],
          data: genderData,
          label: {
            formatter: '{d}%',
            fontSize: 12,
            position: 'outside'
          },
          labelLine: { length: 8, length2: 10 },
          itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
          color: ['#409EFF', '#F56C6C']
        }]
      })

      // 部门分布（Top 5）
      const deptChart = echarts.init(document.getElementById('deptChart'))
      const deptMap = {}
      this.tableData.forEach(item => {
        const name = item.deptName || '未分配'
        deptMap[name] = (deptMap[name] || 0) + 1
      })
      const deptData = Object.keys(deptMap)
          .map(key => ({ name: key, value: deptMap[key] }))
          .sort((a, b) => b.value - a.value)
          .slice(0, 5)
      deptChart.setOption({
        tooltip: { trigger: 'item' },
        legend: {
          orient: 'vertical',
          right: '2%',
          top: 'center',
          itemWidth: 12,
          itemHeight: 12
        },
        series: [{
          type: 'pie',
          radius: ['34%', '58%'],
          center: ['36%', '48%'],
          data: deptData,
          label: {
            formatter: '{d}%',
            fontSize: 12,
            position: 'outside'
          },
          labelLine: { length: 8, length2: 10 },
          itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 }
        }]
      })

      // 职称分布（Top 5）
      const jobChart = echarts.init(document.getElementById('jobChart'))
      const jobMap = {}
      this.tableData.forEach(item => {
        const name = item.jobLevelName || '未分配'
        jobMap[name] = (jobMap[name] || 0) + 1
      })
      const jobData = Object.keys(jobMap)
          .map(key => ({ name: key, value: jobMap[key] }))
          .sort((a, b) => b.value - a.value)
          .slice(0, 5)
      jobChart.setOption({
        tooltip: { trigger: 'item' },
        legend: {
          orient: 'vertical',
          right: '0%',
          top: 'center',
          itemWidth: 12,
          itemHeight: 12
        },
        series: [{
          type: 'pie',
          radius: ['34%', '58%'],
          center: ['36%', '48%'],
          data: jobData,
          label: {
            formatter: '{d}%',
            fontSize: 12,
            position: 'outside'
          },
          labelLine: { length: 8, length2: 10 },
          itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 }
        }]
      })

      // 窗口自适应
      const resizeHandler = () => {
        genderChart.resize()
        deptChart.resize()
        jobChart.resize()
      }
      window.removeEventListener('resize', resizeHandler)
      window.addEventListener('resize', resizeHandler)
    },
    resetFilters() {
      this.filters = { gender: '', deptId: '', jobLevelId: '' }
      this.page = 1
      this.loadData()
    }
  }
}
</script>

<style scoped>
.sta-pers { padding: 20px; }
.filter-bar {
  background: #fff;
  padding: 16px 20px;
  border-radius: 6px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  margin-bottom: 16px;
}
.filter-bar :deep(.el-form-item) { margin-bottom: 0; }

.chart-row { margin-bottom: 16px; }
.chart-title {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 8px;
  text-align: center;
}
.chart-container { width: 100%; height: 220px; }

.stats-info { font-size: 14px; color: #606266; margin-bottom: 12px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>