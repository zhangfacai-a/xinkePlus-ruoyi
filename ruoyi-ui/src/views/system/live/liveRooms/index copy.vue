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

        <!-- 数据表格 -->
        <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="ID" prop="id" align="center" />
            <el-table-column label="直播间名" prop="roomName" align="center" />
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                        v-hasPermi="['system:rooms:edit']">修改</el-button>
                    <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                        v-hasPermi="['system:rooms:remove']">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

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

const { proxy } = getCurrentInstance()

// 响应式数据
const list = ref([])
const open = ref(false)
const loading = ref(false)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const dialogTitle = ref('')

// 表单与查询参数
const data = reactive({
    form: { id: null, roomName: '' },
    queryParams: { pageNum: 1, pageSize: 10, roomName: '' },
    rules: {
        roomName: [{ required: true, message: '请输入直播间名', trigger: 'blur' }]
    }
})

const { form, queryParams, rules } = toRefs(data)

// 查询列表
function getList() {
    loading.value = true
    listRooms(queryParams.value).then(response => {
        list.value = response.rows
        total.value = response.total
        loading.value = false
    })
}

// 重置表单
function resetForm() {
    form.value = { id: null, roomName: '' }
    proxy?.resetForm('formRef')
}

// 查询
function handleQuery() {
    queryParams.value.pageNum = 1
    getList()
}

// 重置查询
function resetQuery() {
    proxy?.resetForm('queryRef')
    handleQuery()
}

// 多选切换
function handleSelectionChange(selection) {
    ids.value = selection.map(item => item.id)
    single.value = selection.length !== 1
    multiple.value = selection.length === 0
}

// 新增
function handleAdd() {
    resetForm()
    open.value = true
    dialogTitle.value = '添加直播间名'
}

// 修改
function handleUpdate(row) {
    const _id = row?.id ?? ids.value[0]
    getRooms(_id).then(res => {
        form.value = res.data
        open.value = true
        dialogTitle.value = '修改直播间名'
    })
}

// 提交
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

// 取消
function cancel() {
    open.value = false
    resetForm()
}

// 删除
function handleDelete(row) {
    const _ids = row?.id ?? ids.value
    proxy.$modal.confirm(`是否确认删除直播间名编号为 "${_ids}" 的数据项？`).then(() => {
        return delRooms(_ids)
    }).then(() => {
        getList()
        proxy.$modal.msgSuccess('删除成功')
    })
}

// 初始化
getList()
</script>
