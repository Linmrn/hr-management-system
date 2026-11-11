<template>
  <div class="dep-mana">
    <div class="dep-toolbar">
      <el-select
        v-model="filterText"
        placeholder="按部门搜索"
        filterable
        clearable
        style="width: 260px"
        @change="handleFilterChange"
      >
        <el-option
          v-for="item in departmentOptions"
          :key="item.id"
          :label="item.name"
          :value="item.name"
        />
      </el-select>
      <el-button @click="resetFilter">重置</el-button>
    </div>

    <el-table
      ref="table"
      :data="deps"
      :row-key="'id'"
      :tree-props="{ children: 'children' }"
      default-expand-all
      border
      stripe
      style="width: 100%; min-width: 920px"
      highlight-current-row
      @current-change="handleCurrentChange"
    >
      <el-table-column prop="name" label="部门名称" min-width="220" />
      <el-table-column prop="id" label="部门ID" width="100" />
      <el-table-column prop="parentId" label="上级部门ID" width="120" />
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="scope">
          <el-button type="primary" size="small" class="depBtn" @click="showAddDepView(scope.row)">
            添加
          </el-button>
          <el-button type="danger" size="small" class="depBtn" @click="deleteDep(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="添加部门" width="30%">
      <el-form label-width="90px">
        <el-form-item label="上级部门">
          <span>{{ pname }}</span>
        </el-form-item>
        <el-form-item label="部门名称">
          <el-input v-model="dep.name" placeholder="请输入部门名称..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doAddDep">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'DepMana',
  data() {
    return {
      dialogVisible: false,
      filterText: '',
      dep: { name: '', parentId: -1 },
      pname: '',
      deps: [],
      selectedDep: null
    }
  },
  computed: {
    departmentOptions() {
      const result = []
      const walk = (nodes = [], prefix = '') => {
        nodes.forEach((node) => {
          const pathName = prefix ? `${prefix} / ${node.name}` : node.name
          result.push({ id: node.id, name: pathName })
          if (node.children && node.children.length) walk(node.children, pathName)
        })
      }
      walk(this.deps)
      return result
    }
  },
  mounted() {
    this.initDeps()
  },
  methods: {
    initDep() {
      this.dep = { name: '', parentId: -1 }
      this.pname = ''
    },
    doAddDep() {
      this.$postRequest('/system/basic/department/add.do', this.dep).then((resp) => {
        if (resp) {
          this.initDeps()
          this.dialogVisible = false
          this.initDep()
        }
      })
    },
    deleteDep(data) {
      if (!data) return
      if (data.parent) {
        this.$message.error('父部门删除失败')
        return
      }
      this.$confirm(`此操作将永久删除【${data.name}】部门, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => this.$deleteRequest('/system/basic/department/delete.do?id=' + data.id))
        .then((resp) => {
          if (resp) {
            this.initDeps()
            this.selectedDep = null
          }
        })
        .catch(() => {
          this.$message({ type: 'info', message: '已取消删除' })
        })
    },
    showAddDepView(data) {
      if (data) {
        this.pname = data.name
        this.dep.parentId = data.id
      } else {
        this.pname = '顶级部门'
        this.dep.parentId = -1
      }
      this.dialogVisible = true
    },
    handleCurrentChange(row) {
      this.selectedDep = row
    },
    initDeps() {
      this.$getRequest('/system/basic/department/list.do').then((resp) => {
        const data = resp?.data?.data || resp?.data || resp
        const list = Array.isArray(data) ? data : data?.records || data || []
        const nodes = list.map((item) => ({
          ...item,
          id: Number(item.id),
          parentId: item.parentId === null || item.parentId === undefined ? -1 : Number(item.parentId),
          children: []
        }))
        const nodeMap = new Map(nodes.map((item) => [item.id, item]))
        const roots = []

        nodes.forEach((node) => {
          const parentId = node.parentId
          if (parentId <= 0 || !nodeMap.has(parentId)) {
            roots.push(node)
            return
          }
          const parent = nodeMap.get(parentId)
          parent.children.push(node)
          parent.parent = true
        })

        this.deps = roots
      })
    },
    handleFilterChange(value) {
      const query = (value || '').trim().toLowerCase()
      if (!query) {
        this.initDeps()
        return
      }
      const all = this.flattenDeps(this.deps)
      const matched = all.filter((item) => (item.name || '').toLowerCase().includes(query))
      const matchedIds = new Set(matched.map((item) => item.id))
      const markVisible = (nodes = []) => nodes
        .map((node) => ({
          ...node,
          children: markVisible(node.children || [])
        }))
        .filter((node) => matchedIds.has(node.id) || (node.children && node.children.length))
      this.deps = markVisible(this.deps)
    },
    resetFilter() {
      this.filterText = ''
      this.initDeps()
    }
  }
}
</script>

<style scoped>
.dep-mana {
  width: 100%;
  min-width: 920px;
}

.dep-toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
  align-items: center;
}

.dep-table {
  width: 100%;
}

.depBtn {
  padding: 4px 8px;
}
</style>