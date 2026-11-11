<template>
    <div>
        <div class="joblevel-toolbar">
            <el-input size="small" v-model="jl.name" class="joblevel-input" :prefix-icon="Plus"
                      placeholder="添加职称..."></el-input>
            <el-select v-model="jl.titleLevel" placeholder="职称等级" size="small" class="joblevel-select">
                <el-option
                        v-for="item in titleLevels"
                        :key="item"
                        :label="item"
                        :value="item">
                </el-option>
            </el-select>
            <el-button :icon="Plus" type="primary" size="small" @click="addJobLevel">添加</el-button>
        </div>
        <div style="margin-top: 10px">
            <el-table
                    :data="jls"
                    border
                    v-loading="loading"
                    element-loading-text="正在加载..."
                    element-loading-background="rgba(0, 0, 0, 0.8)"
                    size="small"
                    style="width: 80%">
                <el-table-column
                        prop="id"
                        label="编号"
                        width="55">
                </el-table-column>
                <el-table-column
                        prop="name"
                        label="职称名称"
                        width="150">
                </el-table-column>
                <el-table-column
                        prop="titleLevel"
                        label="职称级别">
                </el-table-column>
                <el-table-column
                        prop="createDate"
                        label="创建时间">
                </el-table-column>
                <el-table-column
                        label="是否启用">
                    <template #default="scope">
                        <el-tag type="success" v-if="scope && scope.row && scope.row.enabled">已启用</el-tag>
                        <el-tag type="danger" v-else>未启用</el-tag>
                    </template>
                </el-table-column>
                <el-table-column
                        label="操作">
                    <template #default="scope">
                        <el-button size="small" @click="showEditView(scope.row)">编辑</el-button>
                        <el-button size="small" type="danger" @click="deleteHandler(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

        </div>
        <el-dialog
                title="修改职称"
                v-model="dialogVisible"
                width="30%">
            <div>
                <table>
                    <tbody>
                    <tr>
                        <td>
                            <el-tag>职称名</el-tag>
                        </td>
                        <td>
                            <el-input size="small" v-model="updateJl.name"></el-input>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <el-tag>职称级别</el-tag>
                        </td>
                        <td>
                            <el-select v-model="updateJl.titleLevel" placeholder="职称等级" size="small"
                                       style="margin-left: 5px;margin-right: 5px">
                                <el-option
                                        v-for="item in titleLevels"
                                        :key="item"
                                        :label="item"
                                        :value="item">
                                </el-option>
                            </el-select>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <el-tag>是否启用</el-tag>
                        </td>
                        <td>
                            <el-switch
                                    v-model="updateJl.enabled"
                                    active-text="启用"
                                    inactive-text="禁用">
                            </el-switch>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <template #footer>
                <el-button size="small" @click="dialogVisible = false">取 消</el-button>
                <el-button size="small" type="primary" @click="doUpdate">确 定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script>
import { Plus } from '@element-plus/icons-vue'
    export default {
        setup() { return { Plus } },
        name: "JobLevelMana",
        data() {
            return {
                dialogVisible: false,
                loading: false,
                updateJl: {
                    name: '',
                    titleLevel: '',
                    enabled: false
                },
                jl: {
                    name: '',
                    titleLevel: ''
                },
                jls: [],
                titleLevels: [
                    '正高级',
                    '副高级',
                    '中级',
                    '初级',
                    '员级',
                ]
            }
        },
        mounted() {
            this.initJls();
        },
        methods: {
            doUpdate() {
                this.$putRequest("/system/basic/joblevel/update.do", this.updateJl).then(resp => {
                    if (resp) {
                        this.initJls();
                        this.dialogVisible = false;
                    }
                })
            },
            showEditView(data) {
                Object.assign(this.updateJl, data);
                this.dialogVisible = true;
            },
            deleteHandler(data) {
                this.$confirm('此操作将永久【' + data.name + '】职称, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$deleteRequest("/system/basic/joblevel/delete.do?id=" + data.id).then(resp => {
                        if (resp) {
                            this.initJls();
                        }
                    })
                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });
                });
            },
            addJobLevel() {
                if (this.jl.name && this.jl.titleLevel) {

                    this.$postRequest("/system/basic/joblevel/add.do", this.jl).then(resp => {
                        if (resp) {
                            this.initJls();
                        }
                    });
                } else {
                    this.$message.error("添加字段不可以为空!");
                }
            },
            initJls() {
                this.loading = true;
                this.$getRequest("/system/basic/joblevel/list.do").then(resp => {
                    this.loading = false;
                    const data = resp?.data?.data || resp?.data || resp;
                    this.jls = Array.isArray(data) ? data : (data?.records || data || []);
                    this.jl = {
                        name: '',
                        titleLevel: ''
                    };
                })
            }
        }
    }
</script>

<style scoped>
.joblevel-toolbar {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: nowrap;
}

.joblevel-input {
    width: 300px;
}

.joblevel-select {
    width: 160px;
}

@media (max-width: 720px) {
    .joblevel-toolbar {
        flex-wrap: wrap;
    }

    .joblevel-input,
    .joblevel-select {
        width: 100%;
    }
}
</style>