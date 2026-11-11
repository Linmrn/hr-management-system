<template>
  <div class="sta-score">
    <div class="search-bar">
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        size="default"
      />
      <el-button type="primary" @click="loadData" :loading="loading">查询</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>

    <el-table :data="scoreData" border stripe v-loading="loading">
      <el-table-column prop="id" label="员工ID" width="100" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="rewardScore" label="奖励积分" width="120" />
      <el-table-column prop="punishScore" label="处罚积分" width="120" />
      <el-table-column prop="totalScore" label="总积分" sortable>
        <template #default="{ row }">
          <el-tag :type="Number(row.totalScore) >= 0 ? 'success' : 'danger'">
            {{ row.totalScore }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <span class="total-info">共 {{ total }} 条</span>
      <el-pagination
        background
        layout="prev, pager, next, jumper"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRequest } from '@/utils/api'

const dateRange = ref([])
const scoreData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const requesting = ref(false)

const formatDate = (date) => {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const resetDateRange = () => {
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 30)
  dateRange.value = [formatDate(start), formatDate(end)]
}

const loadData = async () => {
  if (requesting.value) return
  if (!Array.isArray(dateRange.value) || dateRange.value.length !== 2 || !dateRange.value[0] || !dateRange.value[1]) {
    ElMessage.warning('请选择日期范围')
    return
  }

  requesting.value = true
  loading.value = true
  try {
    const res = await getRequest('/statistics/score.do', {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      page: currentPage.value,
      size: pageSize.value
    })

    const ok = res?.code === 200 || res?.success === true
    if (ok) {
      scoreData.value = res?.data?.records || []
      total.value = Number(res?.data?.total || 0)
      if (scoreData.value.length === 0) {
        ElMessage.info('当前日期范围内无积分记录，请调整查询条件')
      }
    } else {
      scoreData.value = []
      total.value = 0
      ElMessage.error(`接口错误：${res?.message || res?.msg || '未知错误'}`)
    }
  } catch (error) {
    console.error('请求异常', error)
    scoreData.value = []
    total.value = 0
    ElMessage.error('网络或服务异常，请检查后端')
  } finally {
    loading.value = false
    requesting.value = false
  }
}

const resetQuery = () => {
  currentPage.value = 1
  resetDateRange()
}

const handlePageChange = (page) => {
  currentPage.value = page
}

watch([dateRange, currentPage, pageSize], () => {
  if (dateRange.value && dateRange.value.length === 2) {
    loadData()
  }
})

onMounted(() => {
  resetDateRange()
})
</script>

<style scoped>
.sta-score {
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  background: #fff;
  padding: 16px 20px;
  border-radius: 6px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.pagination-container {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 18px;
  padding: 12px 0;
  border-top: 1px solid #e8ecf1;
}

.total-info {
  margin-right: 16px;
  color: #5a6a7e;
  font-size: 14px;
}
</style>
