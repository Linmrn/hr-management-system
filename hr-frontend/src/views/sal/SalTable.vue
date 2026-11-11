<template>
  <div>
    <!-- 搜索栏 -->
    <div style="display: flex; justify-content: space-between; margin-bottom: 10px">
      <div>
        <el-input
            v-model="searchYearMonth"
            placeholder="请输入年月，如 2026-07"
            size="small"
            style="width: 200px; margin-right: 10px"
            clearable
        />
        <el-input
            v-model="keyword"
            placeholder="请输入员工姓名或工号"
            size="small"
            style="width: 200px; margin-right: 10px"
            clearable
        />
        <el-button type="primary" size="small" @click="searchRecords">搜索</el-button>
        <el-button size="small" @click="resetSearch">重置</el-button>
      </div>
      <div>
        <!-- 🔥 新增：添加工资记录按钮 -->
        <el-button type="primary" size="small" @click="showAddView">添加工资记录</el-button>
        <el-button type="success" size="small" @click="exportExcel">导出 Excel</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table :data="records" border stripe v-loading="loading">
      <el-table-column prop="eid" label="员工ID" width="80"></el-table-column>
      <el-table-column prop="yearMonth" label="年月" width="100"></el-table-column>
      <el-table-column prop="basicSalary" label="基本工资" width="90"></el-table-column>
      <el-table-column prop="bonus" label="奖金" width="70"></el-table-column>
      <el-table-column prop="lunchSalary" label="餐补" width="70"></el-table-column>
      <el-table-column prop="trafficSalary" label="交通补" width="70"></el-table-column>
      <el-table-column prop="allSalary" label="应发合计" width="90" style="font-weight: bold"></el-table-column>
      <el-table-column prop="pension" label="养老保险" width="90"></el-table-column>
      <el-table-column prop="medical" label="医疗保险" width="90"></el-table-column>
      <el-table-column prop="accumulationFund" label="公积金" width="80"></el-table-column>
      <el-table-column prop="tax" label="个税" width="70"></el-table-column>
      <el-table-column prop="actualSalary" label="实发工资" width="90" style="font-weight: bold; color: #409eff"></el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 1" type="success">已结算</el-tag>
          <el-tag v-else type="warning">未结算</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="showEditView(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteRecord(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="display: flex; justify-content: flex-end; margin-top: 10px">
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

    <!-- 🔥 添加工资记录对话框 -->
    <el-dialog title="添加工资记录" v-model="addDialogVisible" width="40%">
      <el-form :model="addForm" label-width="100px" size="small">
        <el-form-item label="员工ID">
          <el-input v-model="addForm.eid" type="number" placeholder="请输入员工ID"></el-input>
        </el-form-item>
        <el-form-item label="年月">
          <el-input v-model="addForm.yearMonth" placeholder="如 2026-07"></el-input>
        </el-form-item>
        <el-form-item label="基本工资">
          <el-input v-model="addForm.basicSalary" type="number"></el-input>
        </el-form-item>
        <el-form-item label="奖金">
          <el-input v-model="addForm.bonus" type="number"></el-input>
        </el-form-item>
        <el-form-item label="餐补">
          <el-input v-model="addForm.lunchSalary" type="number"></el-input>
        </el-form-item>
        <el-form-item label="交通补助">
          <el-input v-model="addForm.trafficSalary" type="number"></el-input>
        </el-form-item>
        <el-form-item label="养老保险">
          <el-input v-model="addForm.pension" type="number"></el-input>
        </el-form-item>
        <el-form-item label="医疗保险">
          <el-input v-model="addForm.medical" type="number"></el-input>
        </el-form-item>
        <el-form-item label="公积金">
          <el-input v-model="addForm.accumulationFund" type="number"></el-input>
        </el-form-item>
        <el-form-item label="个税">
          <el-input v-model="addForm.tax" type="number"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="addForm.status">
            <el-radio :value="0">未结算</el-radio>
            <el-radio :value="1">已结算</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer><span>
                <el-button @click="addDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="doAdd">确定</el-button>
            </span></template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog title="编辑工资记录" v-model="editDialogVisible" width="40%">
      <el-form :model="editForm" label-width="100px" size="small">
        <el-form-item label="基本工资">
          <el-input v-model="editForm.basicSalary" type="number"></el-input>
        </el-form-item>
        <el-form-item label="奖金">
          <el-input v-model="editForm.bonus" type="number"></el-input>
        </el-form-item>
        <el-form-item label="餐补">
          <el-input v-model="editForm.lunchSalary" type="number"></el-input>
        </el-form-item>
        <el-form-item label="交通补助">
          <el-input v-model="editForm.trafficSalary" type="number"></el-input>
        </el-form-item>
        <el-form-item label="养老保险">
          <el-input v-model="editForm.pension" type="number"></el-input>
        </el-form-item>
        <el-form-item label="医疗保险">
          <el-input v-model="editForm.medical" type="number"></el-input>
        </el-form-item>
        <el-form-item label="公积金">
          <el-input v-model="editForm.accumulationFund" type="number"></el-input>
        </el-form-item>
        <el-form-item label="个税">
          <el-input v-model="editForm.tax" type="number"></el-input>
        </el-form-item>
      </el-form>
      <template #footer><span>
                <el-button @click="editDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="doUpdate">确定</el-button>
            </span></template>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "SalTable",
  data() {
    return {
      loading: false,
      records: [],
      total: 0,
      currentPage: 1,
      currentSize: 10,
      searchYearMonth: '',
      keyword: '',
      // 🔥 新增：添加对话框相关
      addDialogVisible: false,
      addForm: {
        eid: null,
        yearMonth: '',
        basicSalary: 0,
        bonus: 0,
        lunchSalary: 0,
        trafficSalary: 0,
        pension: 0,
        medical: 0,
        accumulationFund: 0,
        tax: 0,
        status: 0
      },
      // 编辑对话框相关
      editDialogVisible: false,
      editForm: {
        id: null,
        basicSalary: 0,
        bonus: 0,
        lunchSalary: 0,
        trafficSalary: 0,
        pension: 0,
        medical: 0,
        accumulationFund: 0,
        tax: 0
      }
    }
  },
  mounted() {
    this.initRecords();
  },
  methods: {
    resetSearch() {
      this.searchYearMonth = '';
      this.keyword = '';
      this.currentPage = 1;
      this.initRecords();
    },
    searchRecords() {
      this.currentPage = 1;
      this.initRecords();
    },
    sizeChange(size) {
      this.currentSize = size;
      this.currentPage = 1;
      this.initRecords();
    },
    currentChange(page) {
      this.currentPage = page;
      this.initRecords();
    },
    initRecords() {
      this.loading = true;
      const params = {
        page: this.currentPage,
        size: this.currentSize
      };
      const yearMonth = this.searchYearMonth.trim();
      const keyword = this.keyword.trim();
      if (yearMonth) params.yearMonth = yearMonth;
      if (keyword) params.keyword = keyword;

      this.$getRequest('/salary/table/list.do', params).then(resp => {
        const data = resp?.data?.data || resp?.data || resp || {};
        this.records = data.records || data.list || [];
        this.total = Number(data.total ?? data.count ?? 0);
      }).catch(() => {
        this.records = [];
        this.total = 0;
      }).finally(() => {
        this.loading = false;
      });
    },
    // 🔥 新增：显示添加对话框
    showAddView() {
      this.addForm = {
        eid: null,
        yearMonth: '',
        basicSalary: 0,
        bonus: 0,
        lunchSalary: 0,
        trafficSalary: 0,
        pension: 0,
        medical: 0,
        accumulationFund: 0,
        tax: 0,
        status: 0
      };
      this.addDialogVisible = true;
    },
    // 🔥 新增：提交添加
    doAdd() {
      if (!this.addForm.eid || !this.addForm.yearMonth) {
        this.$message.warning('请填写员工ID和年月');
        return;
      }
      this.$postRequest('/salary/table/add.do', this.addForm).then(resp => {
        if (resp) {
          this.$message.success('添加成功');
          this.addDialogVisible = false;
          this.initRecords();
        }
      });
    },
    // 编辑
    showEditView(row) {
      if (row.status === 1) {
        this.$message.warning('已结算的记录不能编辑');
        return;
      }
      this.editDialogVisible = true;
      this.editForm = {
        id: row.id,
        basicSalary: row.basicSalary || 0,
        bonus: row.bonus || 0,
        lunchSalary: row.lunchSalary || 0,
        trafficSalary: row.trafficSalary || 0,
        pension: row.pension || 0,
        medical: row.medical || 0,
        accumulationFund: row.accumulationFund || 0,
        tax: row.tax || 0
      };
    },
    doUpdate() {
      this.$putRequest('/salary/table/update.do', this.editForm).then(resp => {
        if (resp) {
          this.$message.success('更新成功');
          this.editDialogVisible = false;
          this.initRecords();
        }
      });
    },
    // 删除
    deleteRecord(row) {
      if (row.status === 1) {
        this.$message.warning('已结算的记录不能删除');
        return;
      }
      this.$confirm('此操作将永久删除该工资记录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$deleteRequest('/salary/table/deleteRecord.do?id=' + row.id).then(resp => {
          if (resp) {
            this.$message.success('删除成功');
            this.initRecords();
          }
        });
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },
    // 导出 Excel
    exportExcel() {
      let url = '/salary/table/export.do';
      if (this.searchYearMonth) {
        url += '?yearMonth=' + this.searchYearMonth;
      }
      if (this.keyword) {
        url += (this.searchYearMonth ? '&' : '?') + 'keyword=' + this.keyword;
      }
      window.open(url, '_blank');
    }
  }
}
</script>

<style scoped>
</style>