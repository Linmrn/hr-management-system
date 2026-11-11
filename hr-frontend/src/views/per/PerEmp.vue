<template>
  <div class="detail-page">
    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar">
        <div>
          <p class="eyebrow">员工高级资料</p>
          <h2>员工详情查看</h2>
          <p class="summary">在员工基本资料列表中直接查看详细信息，并汇总奖惩、培训、考评、调薪、调动等记录。</p>
        </div>
        <div class="toolbar-actions">
          <el-button @click="goBack">返回员工资料</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="search-card">
      <el-row :gutter="16" align="middle">
        <el-col :span="8">
          <el-input v-model="keyword" clearable placeholder="输入员工姓名搜索" @keyup.enter="loadEmployees" />
        </el-col>
        <el-col :span="8">
          <el-select v-model="positionId" clearable filterable placeholder="按职位筛选" style="width: 100%">
            <el-option v-for="pos in positionOptions" :key="pos.id" :label="pos.name" :value="pos.id" />
          </el-select>
        </el-col>
        <el-col :span="8" class="actions">
          <el-button type="primary" @click="loadEmployees">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never">
      <el-table :data="employees" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="workID" label="工号" width="110" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="department.name" label="部门" min-width="140" />
        <el-table-column prop="position.name" label="职位" min-width="140" />
        <el-table-column prop="jobLevel.name" label="职称" min-width="140" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="showDetail(scope.row)">查看</el-button>
          </template>
        </el-table-column>
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

    <el-dialog v-model="detailVisible" title="员工高级资料" width="50%" top="8vh">
      <template v-if="currentEmployee">
        <el-skeleton v-if="detailLoading" :rows="6" animated />
        <template v-else>
          <el-card class="summary-card" shadow="never">
            <el-row :gutter="12">
              <el-col :span="6" v-for="card in summaryCards" :key="card.label">
                <div class="metric-card compact-metric-card">
                  <span class="metric-label">{{ card.label }}</span>
                  <strong class="metric-value">{{ card.value }}</strong>
                </div>
              </el-col>
            </el-row>
          </el-card>

          <el-descriptions :column="3" border size="small" class="base-desc compact-desc">
            <el-descriptions-item label="姓名">{{ currentEmployee.name }}</el-descriptions-item>
            <el-descriptions-item label="工号">{{ currentEmployee.workID }}</el-descriptions-item>
            <el-descriptions-item label="部门">{{ currentEmployee.department?.name || '-' }}</el-descriptions-item>
            <el-descriptions-item label="职位">{{ currentEmployee.position?.name || '-' }}</el-descriptions-item>
            <el-descriptions-item label="职称">{{ currentEmployee.jobLevel?.name || '-' }}</el-descriptions-item>
            <el-descriptions-item label="学历">{{ currentEmployee.tiptopDegree || '-' }}</el-descriptions-item>
          </el-descriptions>

          <el-card shadow="never" class="salary-card compact-section-card">
            <template #header>
              <div class="section-header compact-section-header">
                <span>工资信息</span>
              </div>
            </template>
            <template v-if="salaryInfo">
              <el-descriptions :column="4" border size="small" class="compact-desc">
                <el-descriptions-item label="账套名称">{{ salaryInfo.name || '-' }}</el-descriptions-item>
                <el-descriptions-item label="应发工资">{{ salaryInfo.allSalary ?? '-' }}</el-descriptions-item>
                <el-descriptions-item label="基本工资">{{ salaryInfo.basicSalary ?? '-' }}</el-descriptions-item>
                <el-descriptions-item label="奖金">{{ salaryInfo.bonus ?? '-' }}</el-descriptions-item>
              </el-descriptions>
            </template>
            <div v-else class="empty-text">暂无数据</div>
          </el-card>

          <el-row :gutter="12" class="section-grid">
            <el-col :span="12" v-for="section in sections" :key="section.key">
              <el-card shadow="never" class="section-card compact-section-card">
                <template #header>
                  <div class="section-header compact-section-header">
                    <span>{{ section.label }}</span>
                    <el-tag size="small" effect="plain">{{ section.items.length }}</el-tag>
                  </div>
                </template>
                <el-empty v-if="!section.items.length" description="暂无数据" />
                <el-table v-else :data="section.items" size="small" border style="width: 100%">
                  <el-table-column v-for="col in section.columns" :key="col.prop" :prop="col.prop" :label="col.label" :width="col.width" :min-width="col.minWidth" />
                </el-table>
              </el-card>
            </el-col>
          </el-row>
        </template>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getRequest } from '@/utils/api'

const router = useRouter()
const loading = ref(false)
const detailLoading = ref(false)
const employees = ref([])
const positionOptions = ref([])
const keyword = ref('')
const positionId = ref('')
const detailVisible = ref(false)
const currentEmployee = ref(null)
const page = ref(1)
const size = ref(10)
const total = ref(0)

const emptyAdvanced = () => ({ ec: [], train: [], appraise: [], adjustSalary: [], remove: [], salary: [] })
const extractPageData = (resp) => resp?.data?.data || resp?.data || resp
const extractList = (resp) => extractPageData(resp)?.records || extractPageData(resp) || []

const summaryCards = computed(() => {
  const detail = currentEmployee.value?.advanced || emptyAdvanced()
  return [
    { label: '奖惩条数', value: detail.ec.length },
    { label: '培训条数', value: detail.train.length },
    { label: '考评条数', value: detail.appraise.length },
    { label: '调动条数', value: detail.remove.length }
  ]
})

const salaryInfo = computed(() => {
  const detail = currentEmployee.value?.advanced || {}
  return detail.salary?.[0] || currentEmployee.value?.salary || null
})

const latestSalaryRecord = computed(() => {
  const detail = currentEmployee.value?.advanced || emptyAdvanced()
  return detail.adjustSalary?.[0] || null
})

const sections = computed(() => {
  const detail = currentEmployee.value?.advanced || emptyAdvanced()
  return [
    { key: 'ec', label: '奖惩记录', items: detail.ec, columns: [{ prop: 'ecdate', label: '日期', width: 120 }, { prop: 'ecreason', label: '原因', minWidth: 160 }, { prop: 'ecpoint', label: '分值', width: 80 }, { prop: 'ectype', label: '类别', width: 80 }] },
    { key: 'train', label: '培训记录', items: detail.train, columns: [{ prop: 'traindate', label: '日期', width: 120 }, { prop: 'traincontent', label: '培训内容', minWidth: 180 }] },
    { key: 'appraise', label: '考评记录', items: detail.appraise, columns: [{ prop: 'appdate', label: '日期', width: 120 }, { prop: 'appreason', label: '事由', minWidth: 160 }, { prop: 'appresult', label: '结果', minWidth: 180 }] },
    { key: 'adjustSalary', label: '调薪记录', items: detail.adjustSalary, columns: [{ prop: 'saldate', label: '日期', width: 120 }, { prop: 'salreason', label: '原因', minWidth: 160 }, { prop: 'oldsalary', label: '原工资', width: 90 }, { prop: 'newsalary', label: '新工资', width: 90 }] },
    { key: 'remove', label: '调动记录', items: detail.remove, columns: [{ prop: 'removedate', label: '日期', width: 120 }, { prop: 'departmentid', label: '部门ID', width: 90 }, { prop: 'posid', label: '岗位ID', width: 90 }, { prop: 'reason', label: '原因', minWidth: 160 }] }
  ]
})

const loadEmployees = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (keyword.value) params.name = keyword.value
    if (positionId.value) params.posId = positionId.value
    const resp = await getRequest('/employee/basic/list.do', params)
    const payload = extractPageData(resp) || {}
    employees.value = payload.records || []
    total.value = Number(payload.total ?? payload.count ?? 0)
  } finally {
    loading.value = false
  }
}

const loadPositions = async () => {
  const resp = await getRequest('/employee/basic/positions.do')
  positionOptions.value = extractList(resp)
}

const resetFilters = () => {
  keyword.value = ''
  positionId.value = ''
  page.value = 1
  loadEmployees()
}

const loadEmployeeDetail = async (id) => {
  const resp = await getRequest('/employee/record/emp.do', { id })
  return extractPageData(resp)
}

const loadSalaryInfo = async (eid) => {
  try {
    const resp = await getRequest('/employee/adjustsalary/page.do', { page: 1, size: 1, eid })
    const pageData = extractPageData(resp) || {}
    return pageData.records?.[0] || null
  } catch (e) {
    return null
  }
}

const loadRecordList = async (url) => extractList(await getRequest(url))

const buildDetails = async (employee) => {
  const [basic, ec, train, appraise, adjustSalary, remove, salaryInfo] = await Promise.all([
    loadEmployeeDetail(employee.id),
    loadRecordList('/employee/ec/list.do'),
    loadRecordList('/employee/train/list.do'),
    loadRecordList('/employee/appraise/list.do'),
    loadRecordList('/employee/adjustsalary/list.do'),
    loadRecordList('/employee/remove/list.do'),
    loadSalaryInfo(employee.id)
  ])

  const filterByEid = (list) => (list || []).filter(item => String(item.eid) === String(employee.id))
  return {
    ...basic,
    advanced: {
      ec: filterByEid(ec),
      train: filterByEid(train),
      appraise: filterByEid(appraise),
      adjustSalary: filterByEid(adjustSalary),
      remove: filterByEid(remove),
      salary: salaryInfo ? [salaryInfo] : []
    }
  }
}

const handleCurrentChange = (val) => {
  page.value = val
  loadEmployees()
}

const handleSizeChange = (val) => {
  size.value = val
  page.value = 1
  loadEmployees()
}

const goBack = () => router.push('/home/emp/basic')

const showDetail = async (row) => {
  detailVisible.value = true
  detailLoading.value = true
  currentEmployee.value = row
  try {
    currentEmployee.value = await buildDetails(row)
  } catch (e) {
    currentEmployee.value = { ...row, advanced: emptyAdvanced() }
  } finally {
    detailLoading.value = false
  }
}

onMounted(() => {
  loadEmployees()
  loadPositions()
})
</script>

<style scoped>
.detail-page { padding: 24px; background: linear-gradient(180deg, #f8fbff 0%, #ffffff 100%); min-height: 100%; }
.toolbar-card, .search-card { margin-bottom: 16px; border-radius: 16px; }
.toolbar { display: flex; justify-content: space-between; gap: 16px; align-items: center; }
.toolbar-actions { display: flex; align-items: center; gap: 12px; }
.eyebrow { margin: 0 0 8px; font-size: 12px; color: var(--el-color-primary); letter-spacing: .12em; text-transform: uppercase; }
h2 { margin: 0; font-size: 24px; color: #1f2a37; }
.summary { margin: 10px 0 0; color: #5b6472; line-height: 1.7; }
.actions { display: flex; justify-content: flex-end; gap: 8px; }
.summary-card { margin-bottom: 8px; border-radius: 10px; background: linear-gradient(135deg, #f5faff, #ffffff); }
.metric-card { padding: 12px; border: 1px solid #e5eef8; border-radius: 10px; background: #fff; }
.compact-metric-card { padding: 8px 10px; }
.metric-label { display: block; color: #6b7280; font-size: 11px; margin-bottom: 4px; }
.metric-value { font-size: 18px; color: #1f2a37; }
.base-desc { margin-top: 8px; }
.compact-desc :deep(.el-descriptions__label),
.compact-desc :deep(.el-descriptions__content) { padding-top: 6px; padding-bottom: 6px; }
.section-grid { margin-top: 8px; }
.section-card { margin-bottom: 8px; }
.compact-section-card :deep(.el-card__header),
.compact-section-card :deep(.el-card__body) { padding: 8px 10px; }
.section-header { display: flex; align-items: center; justify-content: space-between; font-weight: 600; }
.compact-section-header { font-size: 12px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
.empty-text { padding: 18px 0; text-align: center; color: #909399; font-size: 14px; }
:deep(.el-empty) { padding: 12px 0; }
:deep(.el-empty__image) { display: none; }
:deep(.el-empty__description) { margin-top: 0; }
</style>