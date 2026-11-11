<template>
  <div>
    <div style="margin-bottom:10px;">
      <el-input v-model="keyword" placeholder="输入员工姓名或工号" size="small" style="width:200px;margin-right:10px;" @keyup.enter="searchEmps" />
      <el-button type="primary" size="small" @click="searchEmps">搜索</el-button>
      <el-button size="small" @click="resetSearch">重置</el-button>
      <el-button type="success" size="small" @click="initEmps" style="margin-left:10px;">刷新</el-button>
    </div>

    <el-table :data="emps" border stripe size="small" v-loading="loading">
      <el-table-column prop="name" label="姓名" fixed width="120" align="left" />
      <el-table-column prop="workID" label="工号" width="120" align="left" />
      <el-table-column prop="email" label="电子邮件" width="200" align="left" />
      <el-table-column prop="phone" label="电话号码" width="120" align="left" />
      <el-table-column prop="department.name" label="所属部门" width="120" align="left" />
      <el-table-column label="工资账套" align="center">
        <template #default="scope">
          <el-tooltip placement="right" v-if="scope.row.salary">
            <template #content>
              <table>
                <tbody>
                  <tr><td>基本工资</td><td>{{ scope.row.salary.basicSalary }}</td></tr>
                  <tr><td>交通补助</td><td>{{ scope.row.salary.trafficSalary }}</td></tr>
                  <tr><td>午餐补助</td><td>{{ scope.row.salary.lunchSalary }}</td></tr>
                  <tr><td>奖金</td><td>{{ scope.row.salary.bonus }}</td></tr>
                  <tr><td>养老金比率</td><td>{{ scope.row.salary.pensionPer }}</td></tr>
                  <tr><td>养老金基数</td><td>{{ scope.row.salary.pensionBase }}</td></tr>
                  <tr><td>医疗保险比率</td><td>{{ scope.row.salary.medicalPer }}</td></tr>
                  <tr><td>医疗保险基数</td><td>{{ scope.row.salary.medicalBase }}</td></tr>
                  <tr><td>公积金比率</td><td>{{ scope.row.salary.accumulationFundPer }}</td></tr>
                  <tr><td>公积金基数</td><td>{{ scope.row.salary.accumulationFundBase }}</td></tr>
                  <tr><td>启用时间</td><td>{{ scope.row.salary.createDate }}</td></tr>
                </tbody>
              </table>
            </template>
            <el-tag>{{ scope.row.salary.name }}</el-tag>
          </el-tooltip>
          <el-tag v-else type="info">暂未设置</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template #default="scope">
          <el-popover
              placement="left"
              title="修改工资账套"
              @show="showPop(scope.row.salary)"
              @hide="hidePop(scope.row)"
              width="200"
              trigger="click"
          >
            <div>
              <el-select v-model="currentSalary" placeholder="请选择账套" size="small">
                <el-option
                    v-for="item in salaries"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                />
              </el-select>
            </div>
            <template #reference>
              <el-button type="primary" size="small">修改账套</el-button>
            </template>
          </el-popover>
        </template>
      </el-table-column>
    </el-table>

    <div style="display: flex; justify-content: flex-end; margin-top: 10px;">
      <el-pagination
          background
          @size-change="sizeChange"
          @current-change="currentChange"
          layout="sizes, prev, pager, next, jumper, ->, total"
          :total="total"
          v-model:page-size="currentSize"
          v-model:current-page="currentPage"
          :page-sizes="[5, 10, 20, 50]"
      />
    </div>
  </div>
</template>

<script>
export default {
  name: "SalSobCfg",
  data() {
    return {
      keyword: '',
      loading: false,
      emps: [],
      total: 0,
      currentPage: 1,
      currentSize: 10,
      currentSalary: null,
      salaries: []
    }
  },
  mounted() {
    this.initEmps();
    this.initSalaries();
  },
  methods: {
    sizeChange(size) {
      this.currentSize = size;
      this.currentPage = 1;
      this.initEmps();
    },
    currentChange(page) {
      this.currentPage = page;
      this.initEmps();
    },
    searchEmps() {
      this.currentPage = 1;
      this.initEmps();
    },
    resetSearch() {
      this.keyword = '';
      this.currentPage = 1;
      this.initEmps();
    },
    hidePop(data) {
      if (this.currentSalary) {
        this.$putRequest('/salary/sobcfg/update.do?eid=' + data.id + '&sid=' + this.currentSalary)
            .then(resp => {
              if (resp) {
                this.$message.success('账套修改成功');
                this.initEmps();
              }
            })
            .catch(() => this.$message.error('修改失败'));
      }
    },
    showPop(data) {
      this.currentSalary = data ? data.id : null;
    },
    initSalaries() {
      this.$getRequest("/salary/sobcfg/salaries.do")
          .then(resp => {
            const data = resp?.data || resp || [];
            this.salaries = Array.isArray(data) ? data : [];
          })
          .catch(() => this.$message.error('加载账套列表失败'));
    },
    initEmps() {
      this.loading = true;
      const keyword = this.keyword.trim();
      const params = {
        page: keyword ? 1 : this.currentPage,
        size: keyword ? 10000 : this.currentSize
      };
      if (keyword) params.keyword = keyword;

      this.$getRequest('/salary/sobcfg/list.do', params)
          .then(resp => {
            const data = resp?.data?.data || resp?.data || resp || {};
            const records = data.records || data.list || [];
            const normalizedKeyword = keyword.toLocaleLowerCase();
            if (normalizedKeyword) {
              const matchedRecords = records.filter(emp => [emp.name, emp.workID]
                .some(value => String(value ?? '').toLocaleLowerCase().includes(normalizedKeyword)));
              this.total = matchedRecords.length;
              const start = (this.currentPage - 1) * this.currentSize;
              this.emps = matchedRecords.slice(start, start + this.currentSize);
              return;
            }

            this.emps = records;
            this.total = Number(data.total ?? data.count ?? 0);
          })
          .catch(() => {
            this.emps = [];
            this.total = 0;
            this.$message.error('加载员工数据失败');
          })
          .finally(() => {
            this.loading = false;
          });
    }
  }
}
</script>

<style scoped>
</style>