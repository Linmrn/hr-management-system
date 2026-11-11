<template>
  <div class="sta-record-page">
    <div class="header-card">
      <div>
        <h2>人事记录统计</h2>
        <p>对员工积分的统一管理，可根据部门、月份等查询其积分信息。</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" :loading="loading" @click="loadData">查询</el-button>
        <el-button :disabled="loading" @click="resetFilters">重置</el-button>
      </div>
    </div>

    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="filters" class="filter-form" @submit.prevent>
        <el-form-item label="员工姓名">
          <el-input
            v-model.trim="filters.keyword"
            placeholder="输入员工姓名关键字"
            clearable
            style="width: 240px"
            @keyup.enter="loadData"
          />
        </el-form-item>
      </el-form>
    </el-card>

    <div class="stat-grid">
      <el-card shadow="never" class="stat-card">
        <div class="stat-label">记录条数</div>
        <div class="stat-value">{{ summary.totalCount }}</div>
      </el-card>
      <el-card shadow="never" class="stat-card success">
        <div class="stat-label">累计加分</div>
        <div class="stat-value">+{{ summary.positiveScore }}</div>
      </el-card>
      <el-card shadow="never" class="stat-card danger">
        <div class="stat-label">累计扣分</div>
        <div class="stat-value">{{ summary.negativeScore }}</div>
      </el-card>
      <el-card shadow="never" class="stat-card primary">
        <div class="stat-label">净积分</div>
        <div class="stat-value">{{ summary.netScore }}</div>
      </el-card>
    </div>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="table-header">
          <span>所有员工积分记录</span>
          <span class="table-tip">支持分页查看和按姓名关键字筛选</span>
        </div>
      </template>

      <el-table :data="pagedRecords" border stripe v-loading="loading" empty-text="暂无积分记录">
        <el-table-column prop="id" label="编号" width="90" />
        <el-table-column prop="empId" label="员工ID" width="100" />
        <el-table-column prop="employeeName" label="员工姓名" min-width="140" />
        <el-table-column prop="departmentName" label="部门" min-width="120" show-overflow-tooltip />
        <el-table-column prop="positionName" label="职位" min-width="120" show-overflow-tooltip />
        <el-table-column prop="rewardScore" label="奖励积分" width="120" sortable />
        <el-table-column prop="punishScore" label="处罚积分" width="120" sortable />
        <el-table-column prop="totalScore" label="总积分" width="120" sortable>
          <template #default="{ row }">
            <el-tag :type="Number(row.totalScore) >= 0 ? 'success' : 'danger'">
              {{ Number(row.totalScore) >= 0 ? '+' : '' }}{{ row.totalScore }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="原因" min-width="240" show-overflow-tooltip />
        <el-table-column prop="recordDate" label="日期" width="140" />
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
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getRequest } from '@/utils/api'

const loading = ref(false)
const records = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const employeeMap = ref({})

const filters = reactive({
  keyword: ''
})

const summary = computed(() => {
  const totalCount = total.value || records.value.length
  const positiveScore = records.value.reduce((sum, item) => sum + (Number(item.rewardScore) || 0), 0)
  const negativeScore = records.value.reduce((sum, item) => sum + (Number(item.punishScore) || 0), 0)
  const netScore = records.value.reduce((sum, item) => sum + (Number(item.totalScore) || 0), 0)
  return { totalCount, positiveScore, negativeScore, netScore }
})

const filteredRecords = computed(() => {
  const keyword = filters.keyword.trim().toLowerCase()
  if (!keyword) return records.value
  return records.value.filter((item) => {
    return [item.employeeName, item.reason, item.departmentName, item.positionName]
      .filter(Boolean)
      .some((value) => String(value).toLowerCase().includes(keyword))
  })
})

const pagedRecords = computed(() => filteredRecords.value)

const resolveEmployeeName = (item) => {
  if (item.employeeName && item.employeeName !== '未知员工') return item.employeeName
  const eid = item.empId ?? item.employeeId ?? item.eid
  return employeeMap.value[eid]?.name || item.name || '未知员工'
}

const resolveEmployeeMeta = (item) => {
  const eid = Number(item.empId ?? item.employeeId ?? item.eid)
  const employee = employeeMap.value[eid] || {}
  return {
    departmentName: item.departmentName || employee.department?.name || employee.departmentName || '',
    positionName: item.positionName || employee.position?.name || employee.positionName || '',
    employeeName: resolveEmployeeName(item)
  }
}

const normalizeRecords = (data) => {
  if (!Array.isArray(data)) return []
  return data.map((item, index) => {
    const meta = resolveEmployeeMeta(item)
    const ecType = Number(item.ecType ?? item.ectype ?? 0)
    const point = Number(item.ecPoint ?? item.ecpoint ?? item.score ?? item.point ?? item.value ?? item.totalScore ?? 0)
    return {
      id: item.id ?? index + 1,
      empId: item.empId ?? item.employeeId ?? item.eid ?? '--',
      employeeName: meta.employeeName,
      departmentName: meta.departmentName,
      positionName: meta.positionName,
      rewardScore: ecType === 0 ? point : 0,
      punishScore: ecType === 1 ? point : 0,
      totalScore: ecType === 0 ? point : -point,
      reason: item.reason ?? item.remark ?? item.content ?? item.ecreason ?? item.ecReason ?? '',
      recordDate: item.recordDate ?? item.date ?? item.createDate ?? item.time ?? item.ecdate ?? item.ecDate ?? ''
    }
  })
}

const loadEmployees = async () => {
  const resp = await getRequest('/employee/basic/list.do', { page: 1, size: 10000 })
  const payload = resp?.data?.data || resp?.data || resp || {}
  const list = payload.records || payload.list || payload.rows || payload.data || []
  employeeMap.value = Object.fromEntries((Array.isArray(list) ? list : []).map((item) => [item.id, item]))
}

const loadData = async () => {
  loading.value = true
  try {
    const resp = await getRequest('/employee/ec/page.do', {
      page: page.value,
      size: size.value
    })
    const payload = resp?.data?.data || resp?.data || resp || {}
    const list = payload.records || payload.list || payload.rows || []
    records.value = normalizeRecords(list)
    total.value = Number(payload.total ?? payload.count ?? records.value.length)
  } catch (error) {
    console.error('加载员工积分统计失败', error)
    ElMessage.error('加载员工积分统计失败')
    records.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.keyword = ''
  page.value = 1
  size.value = 10
}

const handleCurrentChange = (val) => {
  page.value = val
}

const handleSizeChange = (val) => {
  size.value = val
  page.value = 1
}

watch([page, size, () => filters.keyword], () => {
  loadData()
})

onMounted(async () => {
  try {
    await loadEmployees()
  } catch (e) {
    console.error('加载员工映射失败', e)
  }
  await loadData()
})
</script>

<style scoped>
.sta-record-page {
  padding: 20px;
  background: linear-gradient(180deg, #f5f8ff 0%, #f7f9fc 100%);
  min-height: calc(100vh - 80px);
}

.header-card,
.filter-card,
.table-card {
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
  margin-bottom: 18px;
}

.header-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 22px 24px;
  background: #fff;
}

.header-card h2 {
  margin: 0 0 8px;
  font-size: 24px;
  color: #0f172a;
}

.header-card p {
  margin: 0;
  color: #64748b;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.filter-card :deep(.el-card__body),
.table-card :deep(.el-card__body) {
  padding: 18px 20px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 18px;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}

.stat-card {
  border-radius: 16px;
}

.stat-label {
  color: #64748b;
  font-size: 14px;
  margin-bottom: 10px;
}

.stat-value {
  color: #0f172a;
  font-size: 28px;
  font-weight: 700;
}

.stat-card.success .stat-value { color: #67c23a; }
.stat-card.danger .stat-value { color: #f56c6c; }
.stat-card.primary .stat-value { color: #409eff; }

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #0f172a;
  font-weight: 600;
}

.table-tip {
  color: #94a3b8;
  font-size: 13px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

@media (max-width: 960px) {
  .stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .header-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}

@media (max-width: 640px) {
  .sta-record-page {
    padding: 12px;
  }

  .stat-grid {
    grid-template-columns: 1fr;
  }
}
</style>
