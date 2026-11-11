<template>
  <div>
    <!-- 顶部操作栏 -->
    <div>
      <div style="display: flex; justify-content: space-between; gap: 12px; align-items: flex-start; flex-wrap: wrap">
        <!-- 左侧搜索区 -->
        <div>
          <el-input
              v-model="keyword"
              placeholder="请输入员工名进行搜索，可以直接回车搜索..."
              :prefix-icon="Search"
              clearable
              style="width: 350px; margin-right: 10px"
              :disabled="showAdvanceSearchView"
              @clear="initEmps"
              @keydown.enter="initEmps"
          />
          <el-button type="primary" :icon="Search" :disabled="showAdvanceSearchView" @click="initEmps">
            搜索
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="primary" @click="showAdvanceSearchView = !showAdvanceSearchView">
            <el-icon style="margin-right: 4px">
              <ArrowUp v-if="showAdvanceSearchView" />
              <ArrowDown v-else />
            </el-icon>
            高级搜索
          </el-button>
        </div>

        <!-- 右侧操作按钮 -->
        <div>
          <el-upload :headers="{ 'X-Requested-With': 'XMLHttpRequest' }"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :disabled="importDataDisabled"
              style="display: inline-flex; margin-right: 8px"
              action="/employee/basic/import.do"
              @success="onSuccess"
              @error="onError"
          >
            <el-button :disabled="importDataDisabled" type="success" :icon="importDataBtnIcon">
              {{ importDataBtnText }}
            </el-button>
          </el-upload>
          <el-button type="success" :icon="Download" @click="exportData">导出数据</el-button>
          <el-button type="primary" :icon="Plus" @click="showAddEmpView">添加用户</el-button>
        </div>
      </div>

      <!-- 高级搜索面板 -->
      <transition name="slide-fade">
        <div
            v-show="showAdvanceSearchView"
            style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0"
        >
          <el-row>
            <el-col :span="5">
              政治面貌:
              <el-select v-model="searchValue.politicId" placeholder="政治面貌" size="small" style="width: 130px">
                <el-option v-for="item in politicsstatus" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-col>
            <el-col :span="4">
              民族:
              <el-select v-model="searchValue.nationId" placeholder="民族" size="small" style="width: 130px">
                <el-option v-for="item in nations" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-col>
            <el-col :span="4">
              职位:
              <el-select v-model="searchValue.posId" placeholder="职位" size="small" style="width: 130px">
                <el-option v-for="item in positions" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-col>
            <el-col :span="4">
              职称:
              <el-select v-model="searchValue.jobLevelId" placeholder="职称" size="small" style="width: 130px">
                <el-option v-for="item in joblevels" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-col>
            <el-col :span="7">
              聘用形式:
              <el-radio-group v-model="searchValue.engageForm">
                <el-radio value="劳动合同">劳动合同</el-radio>
                <el-radio value="劳务合同">劳务合同</el-radio>
              </el-radio-group>
            </el-col>
          </el-row>
          <el-row style="margin-top: 10px">
            <el-col :span="5">
              所属部门:
              <el-popover placement="right" title="请选择部门" :width="200" trigger="click" v-model:visible="popVisible2">
                <template #reference>
                  <div
                      style="width: 130px; display: inline-flex; font-size: 13px; border: 1px solid #dedede; height: 26px; border-radius: 5px; cursor: pointer; align-items: center; padding-left: 8px; box-sizing: border-box; margin-left: 3px"
                      @click="showDepView2"
                  >
                    {{ inputDepName }}
                  </div>
                </template>
                <el-tree
                    default-expand-all
                    :data="allDeps"
                    :props="defaultProps"
                    @node-click="searchViewHandleNodeClick"
                />
              </el-popover>
            </el-col>
            <el-col :span="10">
              入职日期:
              <el-date-picker
                  v-model="searchValue.beginDateScope"
                  type="daterange"
                  size="small"
                  unlink-panels
                  value-format="YYYY-MM-DD"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
              />
            </el-col>
            <el-col :span="5" :offset="4">
              <el-button size="small" @click="resetAdvancedSearch">重置</el-button>
              <el-button size="small" type="primary" :icon="Search" @click="initEmps('advanced')">搜索</el-button>
            </el-col>
          </el-row>
        </div>
      </transition>
    </div>

    <!-- 员工表格 -->
    <div style="margin-top: 10px">
      <el-table
          ref="empTableRef"
          :data="emps"
          stripe
          border
          v-loading="loading"
          style="width: 100%"
      >
        <el-table-column prop="name" fixed align="left" label="姓名" width="90" />
        <el-table-column prop="workID" label="工号" align="left" width="85" />
        <el-table-column prop="gender" label="性别" align="left" width="85" />
        <el-table-column prop="birthday" width="95" align="left" label="出生日期" />
        <el-table-column prop="idCard" width="150" align="left" label="身份证号码" />
        <el-table-column prop="wedlock" width="70" label="婚姻状况" />
        <el-table-column prop="nation.name" width="50" label="民族" />
        <el-table-column prop="nativePlace" width="80" label="籍贯" />
        <el-table-column prop="politicsstatus.name" label="政治面貌" />
        <el-table-column prop="email" width="180" align="left" label="电子邮件" />
        <el-table-column prop="phone" width="100" align="left" label="电话号码" />
        <el-table-column prop="address" width="220" align="left" label="联系地址" />
        <el-table-column prop="department.name" width="100" align="left" label="所属部门" />
        <el-table-column prop="position.name" width="100" label="职位" />
        <el-table-column prop="jobLevel.name" width="100" label="职称" />
        <el-table-column prop="engageForm" width="100" align="left" label="聘用形式" />
        <el-table-column prop="tiptopDegree" width="80" align="left" label="最高学历" />
        <el-table-column prop="specialty" width="150" align="left" label="专业" />
        <el-table-column prop="school" width="150" align="left" label="毕业院校" />
        <el-table-column prop="beginDate" width="95" align="left" label="入职日期" />
        <el-table-column prop="conversionTime" width="95" align="left" label="转正日期" />
        <el-table-column prop="beginContract" width="95" align="left" label="合同起始日期" />
        <el-table-column prop="endContract" width="95" align="left" label="合同截止日期" />
        <el-table-column width="100" align="left" label="合同期限">
          <template #default="{ row }">
            <el-tag>{{ row.contractTerm }}</el-tag>
            年
          </template>
        </el-table-column>
        <el-table-column fixed="right" width="200" label="操作">
          <template #default="{ row }">
            <el-button size="small" @click="showEditEmpView(row)">编辑</el-button>
            <el-button size="small" type="primary" @click="showAdvancedDetail(row)">查看高级资料</el-button>
            <el-button size="small" type="danger" @click="deleteEmp(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="display: flex; justify-content: flex-end; margin-top: 12px">
        <el-pagination
            background
            :current-page="page"
            :page-size="size"
            :page-sizes="[10, 20, 30, 50]"
            layout="sizes, prev, pager, next, jumper, ->, total"
            :total="total"
            @current-change="currentChange"
            @size-change="sizeChange"
        />
      </div>
    </div>

    <!-- 新增/编辑员工对话框 -->
    <el-dialog v-model="dialogVisible" :title="title" width="80%">
      <el-form ref="empFormRef" :model="emp" :rules="rules">
        <el-row>
          <el-col :span="6">
            <el-form-item label="姓名:" prop="name">
              <el-input v-model="emp.name" size="small" style="width: 150px" placeholder="请输入员工姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="性别:" prop="gender">
              <el-radio-group v-model="emp.gender">
                <el-radio value="男">男</el-radio>
                <el-radio value="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="出生日期:" prop="birthday">
              <el-date-picker
                  v-model="emp.birthday"
                  type="date"
                  size="small"
                  value-format="YYYY-MM-DD"
                  style="width: 150px"
                  placeholder="出生日期"
              />
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="政治面貌:" prop="politicId">
              <el-select v-model="emp.politicId" placeholder="政治面貌" size="small" style="width: 200px">
                <el-option v-for="item in politicsstatus" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="民族:" prop="nationId">
              <el-select v-model="emp.nationId" placeholder="民族" size="small" style="width: 150px">
                <el-option v-for="item in nations" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="籍贯:" prop="nativePlace">
              <el-input v-model="emp.nativePlace" size="small" style="width: 120px" placeholder="请输入籍贯" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="电子邮箱:" prop="email">
              <el-input v-model="emp.email" size="small" style="width: 150px" placeholder="请输入电子邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="联系地址:" prop="address">
              <el-input v-model="emp.address" size="small" style="width: 200px" placeholder="请输入联系地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="职位:" prop="posId">
              <el-select v-model="emp.posId" placeholder="职位" size="small" style="width: 150px">
                <el-option v-for="item in positions" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="职称:" prop="jobLevelId">
              <el-select v-model="emp.jobLevelId" placeholder="职称" size="small" style="width: 150px">
                <el-option v-for="item in joblevels" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="所属部门:" prop="departmentId">
              <el-popover placement="right" title="请选择部门" :width="200" trigger="click" v-model:visible="popVisible">
                <template #reference>
                  <div
                      style="width: 150px; display: inline-flex; font-size: 13px; border: 1px solid #dedede; height: 26px; border-radius: 5px; cursor: pointer; align-items: center; padding-left: 8px; box-sizing: border-box"
                      @click="showDepView"
                  >
                    {{ inputDepName }}
                  </div>
                </template>
                <el-tree default-expand-all :data="allDeps" :props="defaultProps" @node-click="handleNodeClick" />
              </el-popover>
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="电话号码:" prop="phone">
              <el-input v-model="emp.phone" size="small" style="width: 200px" placeholder="电话号码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="工号:" prop="workID">
              <el-input v-model="emp.workID" size="small" style="width: 150px" disabled placeholder="工号" />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="学历:" prop="tiptopDegree">
              <el-select v-model="emp.tiptopDegree" placeholder="学历" size="small" style="width: 150px">
                <el-option v-for="item in tiptopDegrees" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="毕业院校:" prop="school">
              <el-input v-model="emp.school" size="small" style="width: 150px" placeholder="毕业院校名称" />
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="专业名称:" prop="specialty">
              <el-input v-model="emp.specialty" size="small" style="width: 200px" placeholder="请输入专业名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="入职日期:" prop="beginDate">
              <el-date-picker
                  v-model="emp.beginDate"
                  type="date"
                  size="small"
                  value-format="YYYY-MM-DD"
                  style="width: 130px"
                  placeholder="入职日期"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="转正日期:" prop="conversionTime">
              <el-date-picker
                  v-model="emp.conversionTime"
                  type="date"
                  size="small"
                  value-format="YYYY-MM-DD"
                  style="width: 130px"
                  placeholder="转正日期"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="合同起始日期:" prop="beginContract">
              <el-date-picker
                  v-model="emp.beginContract"
                  type="date"
                  size="small"
                  value-format="YYYY-MM-DD"
                  style="width: 130px"
                  placeholder="合同起始日期"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="合同终止日期:" prop="endContract">
              <el-date-picker
                  v-model="emp.endContract"
                  type="date"
                  size="small"
                  value-format="YYYY-MM-DD"
                  style="width: 150px"
                  placeholder="合同终止日期"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="身份证号码:" prop="idCard">
              <el-input v-model="emp.idCard" size="small" style="width: 180px" placeholder="请输入身份证号码" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="聘用形式:" prop="engageForm">
              <el-radio-group v-model="emp.engageForm">
                <el-radio value="劳动合同">劳动合同</el-radio>
                <el-radio value="劳务合同">劳务合同</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="婚姻状况:" prop="wedlock">
              <el-radio-group v-model="emp.wedlock">
                <el-radio value="已婚">已婚</el-radio>
                <el-radio value="未婚">未婚</el-radio>
                <el-radio value="离异">离异</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="doAddEmp">确 定</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted , markRaw} from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Download,
  Plus,
  ArrowUp,
  ArrowDown,
  Upload,
  Loading,
  Edit,
  Delete
} from '@element-plus/icons-vue'
import {
  getRequest,
  postRequest,
  putRequest,
  deleteRequest
} from '@/utils/api'

const router = useRouter()

// ---------- 响应式数据 ----------
const keyword = ref('')
const showAdvanceSearchView = ref(false)
const loading = ref(false)
const importDataBtnText = ref('导入数据')
const importDataBtnIcon = ref(markRaw(Upload))   // 导入按钮图标对象
const importDataDisabled = ref(false)
const dialogVisible = ref(false)
const title = ref('')
const total = ref(0)
const page = ref(1)
const size = ref(10)

const emps = ref([])
const empTableRef = ref(null)
const nations = ref([])
const joblevels = ref([])
const politicsstatus = ref([])
const positions = ref([])
const allDeps = ref([])
const tiptopDegrees = ['本科', '大专', '硕士', '博士', '高中', '初中', '小学', '其他']

const inputDepName = ref('所属部门')
const popVisible = ref(false)
const popVisible2 = ref(false)
const bulkPopVisible = ref(false)
const bulkInputDepName = ref('所属部门')

const empFormRef = ref(null) // 表单引用

const searchValue = reactive({
  politicId: null,
  nationId: null,
  jobLevelId: null,
  posId: null,
  engageForm: null,
  departmentId: null,
  beginDateScope: null
})

const emp = reactive({
  name: '',
  gender: '',
  birthday: '',
  idCard: '',
  wedlock: '',
  nationId: 1,
  nativePlace: '',
  politicId: 13,
  email: '',
  phone: '',
  address: '',
  departmentId: null,
  jobLevelId: 9,
  posId: 29,
  engageForm: '',
  tiptopDegree: '',
  specialty: '',
  school: '',
  beginDate: '',
  workID: '',
  workState: '在职',
  contractTerm: 2,
  conversionTime: '',
  notWorkDate: null,
  beginContract: '',
  endContract: '',
  workAge: null
})

const defaultProps = {
  children: 'children',
  label: 'name'
}

const rules = {
  name: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  gender: [{ required: true, message: '请输入性别', trigger: 'blur' }],
  birthday: [{ required: true, message: '请输入出生日期', trigger: 'blur' }],
  idCard: [
    { required: true, message: '请输入身份证号码', trigger: 'blur' },
    {
      pattern: /(^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$)|(^[1-9]\d{5}\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{2}$)/,
      message: '身份证号码格式不正确',
      trigger: 'blur'
    }
  ],
  wedlock: [{ required: true, message: '请输入婚姻状况', trigger: 'blur' }],
  nationId: [{ required: true, message: '请选择民族', trigger: 'blur' }],
  nativePlace: [{ required: true, message: '请输入籍贯', trigger: 'blur' }],
  politicId: [{ required: true, message: '请选择政治面貌', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [{ required: true, message: '请输入电话号码', trigger: 'blur' }],
  address: [{ required: true, message: '请输入员工地址', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  jobLevelId: [{ required: true, message: '请选择职称', trigger: 'change' }],
  posId: [{ required: true, message: '请选择职位', trigger: 'change' }],
  engageForm: [{ required: true, message: '请选择聘用形式', trigger: 'blur' }],
  tiptopDegree: [{ required: true, message: '请选择学历', trigger: 'blur' }],
  specialty: [{ required: true, message: '请输入专业', trigger: 'blur' }],
  school: [{ required: true, message: '请输入毕业院校', trigger: 'blur' }],
  beginDate: [{ required: true, message: '请选择入职日期', trigger: 'blur' }],
  conversionTime: [{ required: true, message: '请选择转正日期', trigger: 'blur' }],
  beginContract: [{ required: true, message: '请选择合同起始日期', trigger: 'blur' }],
  endContract: [{ required: true, message: '请选择合同结束日期', trigger: 'blur' }]
}

// ---------- 工具方法 ----------
// 清空员工表单
const emptyEmp = () => {
  Object.assign(emp, {
    name: '',
    gender: '',
    birthday: '',
    idCard: '',
    wedlock: '',
    nationId: 1,
    nativePlace: '',
    politicId: 13,
    email: '',
    phone: '',
    address: '',
    departmentId: null,
    jobLevelId: 9,
    posId: 29,
    engageForm: '',
    tiptopDegree: '',
    specialty: '',
    school: '',
    beginDate: '',
    workID: '',
    workState: '在职',
    contractTerm: 2,
    conversionTime: '',
    notworkDate: null,
    beginContract: '',
    endContract: '',
    workAge: null
  })
  inputDepName.value = '所属部门'
}

// ---------- 数据请求 ----------
const unwrapResponse = (resp) => {
  const payload = resp?.data ?? resp
  return payload?.data ?? payload
}

const initEmps = async (type) => {
  loading.value = true
  let url = `/employee/basic/list.do?page=${page.value}&size=${size.value}&_t=${Date.now()}`

  if (type === 'advanced') {
    if (searchValue.politicId) url += `&politicId=${searchValue.politicId}`
    if (searchValue.nationId) url += `&nationId=${searchValue.nationId}`
    if (searchValue.jobLevelId) url += `&jobLevelId=${searchValue.jobLevelId}`
    if (searchValue.posId) url += `&posId=${searchValue.posId}`
    if (searchValue.engageForm) url += `&engageForm=${searchValue.engageForm}`
    if (searchValue.departmentId) url += `&departmentId=${searchValue.departmentId}`
    if (searchValue.beginDateScope) {
      url += `&beginDateScope[0]=${searchValue.beginDateScope[0]}`
      url += `&beginDateScope[1]=${searchValue.beginDateScope[1]}`
    }
  } else if (keyword.value) {
    url += `&name=${keyword.value}`
  }

  try {
    const resp = await getRequest(url)
    const payload = resp?.data?.data ?? resp?.data ?? resp
    emps.value = Array.isArray(payload?.records) ? payload.records : []
    total.value = Number(payload?.count ?? payload?.total ?? 0)
  } catch (e) {
    emps.value = []
    total.value = 0
    ElMessage.error('加载员工数据失败')
  } finally {
    loading.value = false
  }
}
const unwrapList = (resp) => unwrapResponse(resp) || []

const initData = async () => {
  if (!sessionStorage.getItem('nations')) {
    try {
      const resp = await getRequest('/employee/basic/nations.do')
      nations.value = unwrapList(resp)
      sessionStorage.setItem('nations', JSON.stringify(nations.value))
    } catch (e) {
      console.error('加载民族数据失败', e)
    }
  } else {
    nations.value = JSON.parse(sessionStorage.getItem('nations'))
  }

  if (!sessionStorage.getItem('joblevels')) {
    try {
      const resp = await getRequest('/employee/basic/joblevels.do')
      joblevels.value = unwrapList(resp)
      sessionStorage.setItem('joblevels', JSON.stringify(joblevels.value))
    } catch (e) {
      console.error('加载职称数据失败', e)
    }
  } else {
    joblevels.value = JSON.parse(sessionStorage.getItem('joblevels'))
  }

  if (!sessionStorage.getItem('politicsstatus')) {
    try {
      const resp = await getRequest('/employee/basic/politicsstatus.do')
      politicsstatus.value = unwrapList(resp)
      sessionStorage.setItem('politicsstatus', JSON.stringify(politicsstatus.value))
    } catch (e) {
      console.error('加载政治面貌数据失败', e)
    }
  } else {
    politicsstatus.value = JSON.parse(sessionStorage.getItem('politicsstatus'))
  }

  if (!sessionStorage.getItem('deps')) {
    try {
      const resp = await getRequest('/employee/basic/deps.do')
      allDeps.value = unwrapList(resp)
      sessionStorage.setItem('deps', JSON.stringify(allDeps.value))
    } catch (e) {
      console.error('加载部门数据失败', e)
    }
  } else {
    allDeps.value = JSON.parse(sessionStorage.getItem('deps'))
  }
}

const initPositions = async () => {
  try {
    positions.value = unwrapList(await getRequest('/employee/basic/positions.do'))
  } catch (e) {
    console.error('加载职位数据失败', e)
  }
}

const getMaxWorkID = async () => {
  try {
    const resp = await getRequest('/employee/basic/maxWorkID.do')
    const value = resp?.data?.data ?? resp?.data ?? ''
    if (value !== '') {
      emp.workID = String(value)
    }
  } catch (e) {
    console.error('获取工号失败', e)
  }
}

// ---------- 事件处理 ----------
const resetSearch = () => {
  keyword.value = ''
  page.value = 1
  initEmps()
}

const resetAdvancedSearch = () => {
  searchValue.politicId = null
  searchValue.nationId = null
  searchValue.jobLevelId = null
  searchValue.posId = null
  searchValue.engageForm = null
  searchValue.departmentId = null
  searchValue.beginDateScope = null
  inputDepName.value = '所属部门'
  page.value = 1
  initEmps('advanced')
}

const currentChange = (val) => {
  page.value = val
  initEmps()
}
const sizeChange = (val) => {
  size.value = val
  initEmps()
}
const showAddEmpView = () => {
  emptyEmp()
  title.value = '添加员工'
  getMaxWorkID()
  dialogVisible.value = true
}

const showEditEmpView = (row) => {
  initPositions()
  title.value = '编辑员工信息'
  Object.assign(emp, {
    ...row,
    department: undefined,
    position: undefined,
    jobLevel: undefined,
    nation: undefined,
    politicsstatus: undefined
  })
  inputDepName.value = row.department ? row.department.name : ''
  dialogVisible.value = true
}

const showAdvancedDetail = (row) => {
  router.push({ path: '/home/per/emp', query: { id: row.id } })
}

const deleteEmp = async (row) => {
  try {
    await ElMessageBox.confirm(
        `此操作将把【${row.name}】标记为离职，是否继续?`,
        '提示',
        { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteRequest('/employee/basic/delete.do', { id: row.id })
    ElMessage.success('已标记为离职')
    initEmps()
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') {
      ElMessage.error('操作失败')
    }
  }
}

const doAddEmp = async () => {
  const valid = await new Promise((resolve) => empFormRef.value.validate(resolve))
  if (!valid) return

  const payload = {
    ...emp,
    workID: String(emp.workID || ''),
    workState: emp.workState || '在职',
    nationId: Number(emp.nationId),
    politicId: Number(emp.politicId),
    departmentId: Number(emp.departmentId),
    jobLevelId: Number(emp.jobLevelId),
    posId: Number(emp.posId)
  }

  if (payload.id) {
    await putRequest('/employee/basic/update.do', payload)
  } else {
    await postRequest('/employee/basic/add.do', payload)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  initEmps()
}

const exportData = () => {
  window.open('/employee/basic/export.do', '_parent')
}

// 导入相关
const beforeUpload = () => {
  importDataBtnText.value = '正在导入'
  importDataBtnIcon.value = markRaw(Loading)
  importDataDisabled.value = true
  return true
}
const onSuccess = (response, file) => {
  importDataBtnText.value = '导入数据'
  importDataBtnIcon.value =  markRaw(Upload)
  importDataDisabled.value = false
  initEmps()
}
const onError = (err, file) => {
  importDataBtnText.value = '导入数据'
  importDataBtnIcon.value =  markRaw(Upload)
  importDataDisabled.value = false
  ElMessage.error('导入失败')
}

// 部门树点击（搜索视图）
const searchViewHandleNodeClick = (data) => {
  inputDepName.value = data.name
  searchValue.departmentId = data.id
  popVisible2.value = false
}
// 添加/编辑对话框中的部门树点击
const handleNodeClick = (data) => {
  inputDepName.value = data.name
  emp.departmentId = data.id
  popVisible.value = false
}
const showDepView = () => {
  popVisible.value = !popVisible.value
}
const showDepView2 = () => {
  popVisible2.value = !popVisible2.value
}

// ---------- 生命周期 ----------
onMounted(() => {
  initEmps()
  initData()
  initPositions()
})
</script>

<style scoped>
.slide-fade-enter-active {
  transition: all 0.8s ease;
}
.slide-fade-leave-active {
  transition: all 0.8s cubic-bezier(1, 0.5, 0.8, 1);
}
.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateX(10px);
  opacity: 0;
}
</style>