<template>
  <div>
    <!-- 顶部按钮 -->
    <div style="display: flex; justify-content: space-between; margin-bottom: 10px">
      <el-button type="primary" @click="showAddSalaryView">添加工资账套</el-button>
      <el-button type="success" @click="initSalaries">刷新</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="salaries" border stripe
              style="width: 100%"
              :header-cell-style="{ textAlign: 'center' }"
              :cell-style="{ textAlign: 'center' }"
    >
      <el-table-column width="120" prop="name" label="账套名称"></el-table-column>
      <el-table-column width="100" prop="basicSalary" label="基本工资"></el-table-column>
      <el-table-column width="100" prop="trafficSalary" label="交通补助"></el-table-column>
      <el-table-column width="100" prop="lunchSalary" label="午餐补助"></el-table-column>
      <el-table-column width="100" prop="bonus" label="奖金"></el-table-column>
      <el-table-column width="120" prop="createDate" label="启用时间" :formatter="formatDate"></el-table-column>
      <el-table-column label="养老金" align="center">
        <el-table-column width="70" prop="pensionPer" label="比率" align="center"></el-table-column>
        <el-table-column width="70" prop="pensionBase" label="基数"align="center"></el-table-column>
      </el-table-column>
      <el-table-column label="医疗保险" align="center">
        <el-table-column width="70" prop="medicalPer" label="比率" align="center"></el-table-column>
        <el-table-column width="70" prop="medicalBase" label="基数"align="center"></el-table-column>
      </el-table-column>
      <el-table-column label="公积金" align="center" width="160">
        <el-table-column width="70" prop="accumulationFundPer" label="比率" align="center"></el-table-column>
        <el-table-column width="70" prop="accumulationFundBase" label="基数" align="center"></el-table-column>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="showEditSalaryView(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteSalary(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- ========== 添加/编辑对话框 ========== -->
    <el-dialog
        :title="dialogTitle"
        v-model="dialogVisible"
        width="50%"
        @close="closeDialog"
    >
      <div style="display: flex; justify-content: space-around; align-items: center">
        <el-steps direction="vertical" :active="activeItemIndex">
          <el-step :title="itemName" v-for="(itemName, index) in salaryItemName" :key="index"></el-step>
        </el-steps>
        <!-- 动态输入框 -->
        <div style="width: 220px; text-align: center;">
          <!-- 数字输入 -->
          <template v-if="activeItemIndex < 10">
            <el-input-number
                v-model="salary[fieldKey]"
                :placeholder="'请输入' + salaryItemName[activeItemIndex]"
                controls-position="right"
                style="width: 200px"
                :min="0"
            />
          </template>
          <!-- 最后一步：账套名称 -->
          <template v-else>
            <el-input
                v-model="salary.name"
                placeholder="请输入账套名称"
                style="width: 200px"
            />
          </template>
        </div>
      </div>
      <template #footer><span class="dialog-footer">
        <el-button @click="preStep">{{ activeItemIndex === 10 ? '取消' : '上一步' }}</el-button>
        <el-button type="primary" @click="nextStep">{{ activeItemIndex === 10 ? '完成' : '下一步' }}</el-button>
      </span></template>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "SalSob",
  data() {
    return {
      salaries: [],
      dialogVisible: false,
      dialogTitle: '添加工资账套',
      activeItemIndex: 0,
      editId: null,
      salaryItemName: [
        '基本工资',
        '交通补助',
        '午餐补助',
        '奖金',
        '养老金比率',
        '养老金基数',
        '医疗保险比率',
        '医疗保险基数',
        '公积金比率',
        '公积金基数',
        '账套名称'
      ],
      // 表单数据，字段名与后端一致
      salary: {
        basicSalary: 0,
        trafficSalary: 0,
        lunchSalary: 0,
        bonus: 0,
        pensionPer: 0,
        pensionBase: 0,
        medicalPer: 0,
        medicalBase: 0,
        accumulationFundPer: 0,
        accumulationFundBase: 0,
        name: ''
      }
    };
  },
  computed: {
    // 根据步骤获取当前字段名
    fieldKey() {
      const keys = ['basicSalary', 'trafficSalary', 'lunchSalary', 'bonus',
        'pensionPer', 'pensionBase', 'medicalPer', 'medicalBase',
        'accumulationFundPer', 'accumulationFundBase'];
      return keys[this.activeItemIndex] || '';
    }
  },
  mounted() {
    this.initSalaries();
  },
  methods: {
    initSalaries() {
      this.$getRequest('/salary/sob/list.do').then(resp => {
        if (resp && resp.data) {
          this.salaries = resp.data;
        } else if (resp && Array.isArray(resp)) {
          this.salaries = resp;
        } else {
          this.$message.warning('加载数据失败');
        }
      }).catch(() => {
        this.$message.error('请求失败');
      });
    },

    // 打开添加对话框
    showAddSalaryView() {
        this.dialogTitle = '添加工资账套';
        this.editId = null;
        this.resetForm();
        this.activeItemIndex = 0;
        this.dialogVisible = true;
    },

    // 打开编辑对话框
    showEditSalaryView(data) {
      this.dialogTitle = '修改工资账套';
      this.editId = data.id;
      this.salary = {
        basicSalary: data.basicSalary || 0,
        trafficSalary: data.trafficSalary || 0,
        lunchSalary: data.lunchSalary || 0,
        bonus: data.bonus || 0,
        pensionPer: data.pensionPer || 0,
        pensionBase: data.pensionBase || 0,
        medicalPer: data.medicalPer || 0,
        medicalBase: data.medicalBase || 0,
        accumulationFundPer: data.accumulationFundPer || 0,
        accumulationFundBase: data.accumulationFundBase || 0,
        name: data.name || ''
      };
      this.activeItemIndex = 0;
      this.dialogVisible = true;
    },

    deleteSalary(data) {
      this.$confirm('此操作将永久删除【' + data.name + '】账套，是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$deleteRequest('/salary/sob/delete.do?id=' + data.id).then(resp => {
          if (resp&& resp.success) {
            this.$message.success('删除成功');
            this.initSalaries();
          }else {
            this.$message.warning(resp?.message || '删除失败');
          }
        });
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },

    preStep() {
      if (this.activeItemIndex === 0) return;
      if (this.activeItemIndex === 10) {
        this.dialogVisible = false;
        return;
      }
      this.activeItemIndex--;
    },

    nextStep() {
      if (this.activeItemIndex === 10) {
        this.submitSalary();
        return;
      }
      this.activeItemIndex++;
    },

    submitSalary() {
      if (!this.salary.name || this.salary.name.trim() === '') {
        this.$message.warning('请输入账套名称');
        return;
      }
      const data = { ...this.salary };
      if (this.editId) {
        data.id = this.editId;
        this.$putRequest('/salary/sob/update.do', data).then(resp => {
          if (resp) {
            this.$message.success('修改成功');
            this.dialogVisible = false;
            this.initSalaries();
          }
        });
      } else {
        this.$postRequest('/salary/sob/add.do', data).then(resp => {
          if (resp) {
            this.$message.success('添加成功');
            this.dialogVisible = false;
            this.initSalaries();
          }
        });
      }
    },

    resetForm() {
      this.salary = {
        basicSalary: 0,
        trafficSalary: 0,
        lunchSalary: 0,
        bonus: 0,
        pensionPer: 0,
        pensionBase: 0,
        medicalPer: 0,
        medicalBase: 0,
        accumulationFundPer: 0,
        accumulationFundBase: 0,
        name: ''
      };
    },

    closeDialog() {
      this.activeItemIndex = 0;
      this.resetForm();
      this.editId = null;
    },


    formatDate(row, column, cellValue) {
      if (!cellValue) return '';
      const date = new Date(cellValue);
      return date.getFullYear() + '-' +
          String(date.getMonth() + 1).padStart(2, '0') + '-' +
          String(date.getDate()).padStart(2, '0');

    }
  }
};
</script>

<style scoped>
</style>