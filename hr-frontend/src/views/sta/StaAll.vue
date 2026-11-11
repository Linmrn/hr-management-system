<template>
  <div class="sta-container">
    <!-- 顶部统计卡片（简化） -->
    <div class="stat-cards">
      <div class="stat-item">
        <span class="stat-label">员工总数</span>
        <span class="stat-value">{{ totalEmployees }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">已转正</span>
        <span class="stat-value" style="color:#67C23A;">{{ conversionData.converted || 0 }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">未转正</span>
        <span class="stat-value" style="color:#E6A23C;">{{ conversionData.unconverted || 0 }}</span>
      </div>
    </div>

    <!-- 图表 -->
    <div class="chart-wrapper">
      <div class="chart-box">
        <div class="chart-title">出生月份分布</div>
        <div id="birthdayChart" class="chart-area"></div>
      </div>
      <div class="chart-box">
        <div class="chart-title">转正状态</div>
        <div id="conversionChart" class="chart-area"></div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getRequest } from '@/utils/api'

export default {
  data() {
    return {
      birthdayData: [],
      conversionData: {}
    }
  },
  computed: {
    totalEmployees() {
      const c = this.conversionData
      return (c.converted || 0) + (c.unconverted || 0)
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const [birthRes, convRes] = await Promise.all([
          getRequest('/statistics/birthday.do'),
          getRequest('/statistics/conversion.do')
        ])
        this.birthdayData = birthRes?.data || []
        this.conversionData = convRes?.data || {}
        this.$nextTick(() => {
          this.renderCharts()
        })
      } catch (error) {
        console.error('加载统计数据失败', error)
        this.$message?.error('加载统计数据失败')
      }
    },
    renderCharts() {
      const birthChart = echarts.init(document.getElementById('birthdayChart'))
      const months = this.birthdayData.map(item => item.month + '月')
      const counts = this.birthdayData.map(item => item.count)

      birthChart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 40, right: 20, top: 20, bottom: 30 },
        xAxis: { type: 'category', data: months.length ? months : ['暂无数据'] },
        yAxis: { type: 'value' },
        series: [{
          type: 'bar',
          data: counts.length ? counts : [0],
          barWidth: '45%',
          itemStyle: { color: '#409EFF', borderRadius: [4, 4, 0, 0] }
        }]
      })

      const convChart = echarts.init(document.getElementById('conversionChart'))
      convChart.setOption({
        tooltip: { trigger: 'item' },
        legend: { orient: 'vertical', left: 'left', top: 'center' },
        series: [{
          type: 'pie',
          radius: ['45%', '70%'],
          center: ['55%', '50%'],
          data: [
            { value: this.conversionData.converted || 0, name: '已转正' },
            { value: this.conversionData.unconverted || 0, name: '未转正' }
          ],
          label: { formatter: '{d}%' },
          itemStyle: {
            borderRadius: 4,
            borderColor: '#fff',
            borderWidth: 2
          },
          color: ['#67C23A', '#E6A23C']
        }]
      })

      // 防内存泄漏
      const handler = () => {
        birthChart.resize()
        convChart.resize()
      }
      window.removeEventListener('resize', handler)
      window.addEventListener('resize', handler)
    }
  }
}
</script>

<style scoped>
.sta-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: calc(100vh - 80px);
}

/* 统计卡片 — 极简风格 */
.stat-cards {
  display: flex;
  gap: 16px;
  background: #fff;
  padding: 16px 24px;
  border-radius: 6px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.stat-item {
  display: flex;
  align-items: baseline;
  gap: 10px;
  padding-right: 24px;
  border-right: 1px solid #e8ecf1;
}
.stat-item:last-child {
  border-right: none;
}
.stat-label {
  font-size: 14px;
  color: #909399;
}
.stat-value {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
}

/* 图表区域 */
.chart-wrapper {
  display: flex;
  gap: 20px;
}
.chart-box {
  flex: 1;
  background: #fff;
  border-radius: 6px;
  padding: 16px 16px 8px 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.chart-title {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 10px;
}
.chart-area {
  width: 100%;
  height: 300px;
}
</style>