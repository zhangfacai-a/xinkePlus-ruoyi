<template>
    <div class="app-container">
        <!-- 搜索表单 -->
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px"
            @submit.prevent>
            <el-form-item label="直播间名" prop="roomName">
                <el-input v-model="queryParams.roomName" placeholder="请输入直播间名" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
        </el-form>

        <!-- 操作按钮 -->
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" plain icon="Plus" @click="handleAdd"
                    v-hasPermi="['system:rooms:add']">新增</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
                    v-hasPermi="['system:rooms:edit']">修改</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
                    v-hasPermi="['system:rooms:remove']">删除</el-button>
            </el-col>
            <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <!-- 卡片列表 替换表格 -->
        <el-row :gutter="24">
            <el-col :span="6" v-for="(item, idx) in list" :key="item.id">
                <div class="dashboard-card" :style="{
                    background: pastelColors[idx % pastelColors.length].bg,
                    color: pastelColors[idx % pastelColors.length].color
                }">
                    <el-icon class="card-icon"><i class="el-icon-chat-dot-square"></i></el-icon>
                    <div class="card-main">
                        <div class="card-title">{{ item.roomName }}</div>
                        <!-- 可以有副标题或其它字段 -->
                        <div class="card-actions">
                            <el-button link type="primary" icon="View" @click="handleDetail(item)"
                                v-hasPermi="['system:rooms:view']" style="margin-right: 8px;">详情</el-button>
                            <el-button link icon="Edit" @click="handleUpdate(item)" v-hasPermi="['system:rooms:edit']"
                                style="margin-right: 8px;">修改</el-button>
                            <el-button link type="danger" icon="Delete" @click="handleDelete(item)"
                                v-hasPermi="['system:rooms:remove']">删除</el-button>
                        </div>
                    </div>
                </div>
            </el-col>
            <el-empty v-if="list.length === 0" description="暂无数据" />
        </el-row>

        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize" @pagination="getList" />

        <!-- 新增/修改 弹窗 -->
        <el-dialog :title="dialogTitle" v-model="open" width="500px" append-to-body>
            <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                <el-form-item label="直播间名" prop="roomName">
                    <el-input v-model="form.roomName" placeholder="请输入直播间名" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="submitForm">确定</el-button>
                    <el-button @click="cancel">取消</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, toRefs, getCurrentInstance } from 'vue'
import { listRooms, getRooms, delRooms, addRooms, updateRooms } from '@/api/live/room'
import { Edit, Delete, View } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const list = ref([])
const open = ref(false)
const loading = ref(false)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const dialogTitle = ref('')

const data = reactive({
    form: { id: null, roomName: '' },
    queryParams: { pageNum: 1, pageSize: 10, roomName: '' },
    rules: {
        roomName: [{ required: true, message: '请输入直播间名', trigger: 'blur' }]
    }
})

const pastelColors = [
    { bg: "#eef6ff", color: "#409EFF" }, // 蓝
    { bg: "#fff8ec", color: "#ffb302" }, // 黄
    { bg: "#eaf9fa", color: "#00bfbf" }, // 青
    { bg: "#ffecec", color: "#fa5a55" }, // 红
    { bg: "#f5f6fa", color: "#6366f1" }, // 紫
    { bg: "#e7faed", color: "#00c48f" }, // 绿
]

const { form, queryParams, rules } = toRefs(data)

function getList() {
    loading.value = true
    listRooms(queryParams.value).then(response => {
        list.value = response.rows
        total.value = response.total
        loading.value = false
    })
}

function resetForm() {
    form.value = { id: null, roomName: '' }
    proxy?.resetForm('formRef')
}

function handleQuery() {
    queryParams.value.pageNum = 1
    getList()
}

function resetQuery() {
    proxy?.resetForm('queryRef')
    handleQuery()
}

function handleSelectionChange(selection) {
    ids.value = selection.map(item => item.id)
    single.value = selection.length !== 1
    multiple.value = selection.length === 0
}

function handleAdd() {
    resetForm()
    open.value = true
    dialogTitle.value = '添加直播间名'
}

function handleUpdate(row) {
    const _id = row?.id ?? ids.value[0]
    getRooms(_id).then(res => {
        form.value = res.data
        open.value = true
        dialogTitle.value = '修改直播间名'
    })
}

function handleDetail(row) {
    // 你可以在这里弹出详情弹窗，或者跳转详情页面
    proxy.$modal.info(`这里显示直播间[${row.roomName}]的详情。`) // 仅做示例
}

function submitForm() {
    proxy.$refs['formRef'].validate(valid => {
        if (valid) {
            const req = form.value.id ? updateRooms(form.value) : addRooms(form.value)
            req.then(() => {
                proxy.$modal.msgSuccess(form.value.id ? '修改成功' : '新增成功')
                open.value = false
                getList()
            })
        }
    })
}

function cancel() {
    open.value = false
    resetForm()
}

function handleDelete(row) {
    const _ids = row?.id ?? ids.value
    proxy.$modal.confirm(`是否确认删除直播间名编号为 "${_ids}" 的数据项？`).then(() => {
        return delRooms(_ids)
    }).then(() => {
        getList()
        proxy.$modal.msgSuccess('删除成功')
    })
}

getList()
</script>

<style scoped>
.dashboard-card {
    display: flex;
    align-items: flex-start;
    border-radius: 16px;
    padding: 24px;
    min-height: 120px;
    box-shadow: 0 2px 8px #f1f1f1;
    margin-bottom: 18px;
    transition: box-shadow 0.2s;
    position: relative;
}

.dashboard-card:hover {
    box-shadow: 0 4px 16px #e0e7ef;
}

.card-icon {
    font-size: 36px;
    margin-right: 18px;
    margin-top: 6px;
    flex-shrink: 0;
}

.card-main {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.card-title {
    font-size: 20px;
    font-weight: 600;
    margin-bottom: 18px;
    word-break: break-all;
}

.card-actions {
    margin-top: auto;
    display: flex;
    gap: 12px;
    align-items: center;
}
</style>
