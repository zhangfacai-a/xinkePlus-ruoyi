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

                <el-button type="primary" plain icon="Plus" @click="handleAdd"
                    v-hasPermi="['system:rooms:add']">新增</el-button>

            </el-form-item>
        </el-form>

        <!-- 操作按钮
        <el-row :gutter="10" class="mb8">

            <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row> -->

        <!-- 卡片列表（CSS Grid 自适应） -->
        <div class="card-grid">
            <div class="live-room-card" v-for="item in list" :key="item.id" @mouseenter="hoverId = item.id"
                @mouseleave="hoverId = null" :class="{ hover: hoverId === item.id }">
                <div class="card-header">
                    <div class="room-title" :title="item.roomName">{{ item.roomName }}</div>
                    <!-- <span class="room-badge" v-if="item.id < 10">HOT</span> -->
                </div>
                <!-- <div class="card-info">
                    <span class="room-id">房间号：{{ item.id }}</span>
                </div> -->
                <div class="card-actions">
                    <el-button size="small" type="success" plain icon="Edit" @click="handleUpdate(item)"
                        v-hasPermi="['system:rooms:edit']">详情</el-button>
                    <el-button size="small" type="warning" plain icon="Edit" @click="handleUpdate(item)"
                        v-hasPermi="['system:rooms:edit']">修改</el-button>
                    <el-button size="small" plain type="danger" icon="Delete" @click="handleDelete(item)"
                        v-hasPermi="['system:rooms:remove']">删除</el-button>
                </div>
            </div>
            <el-empty v-if="list.length === 0" description="暂无数据" />
        </div>

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
const hoverId = ref(null)  // 新增，用于卡片hover高亮

const data = reactive({
    form: { id: null, roomName: '' },
    queryParams: { pageNum: 1, pageSize: 20, roomName: '' },
    rules: {
        roomName: [{ required: true, message: '请输入直播间名', trigger: 'blur' }]
    }
})

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
    proxy.$modal.info(`这里显示直播间[${row.roomName}]的详情。`)
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
    proxy.$modal.confirm(`是否确认删除直播间名[${row.roomName}]的数据项？`).then(() => {
        return delRooms(_ids)
    }).then(() => {
        getList()
        proxy.$modal.msgSuccess('删除成功')
    })
}

getList()
</script>

<style scoped>
.card-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, 260px);
    gap: 28px 24px;
    justify-content: start;
    align-items: stretch;
    margin: 32px 0 30px 0;
    /* min-height: 200px; */
}

.live-room-card {
    width: 260px;
    /* background: #fff; */
    border-radius: 16px;
    box-shadow: 0 1px 4px 0 rgba(52, 195, 143, 0.03);
    border: 2px solid #c9ebe0;
    padding: 22px 18px 16px 18px;
    transition: box-shadow 0.23s, border-color 0.19s, transform 0.13s;
    min-height: 126px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    position: relative;
}

.live-room-card.hover {
    border-color: #34c38f;
    box-shadow: 0 4px 18px 0 rgba(52, 195, 143, 0.10);
    transform: translateY(-3px) scale(1.018);
}

.card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 6px;
    margin-bottom: 4px;
}

.room-title {
    font-size: 17px;
    font-weight: 700;
    /* color: #19a780; */
    letter-spacing: 0.1px;
    line-height: 1.12;
    max-width: 85%;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
}

.room-badge {
    background: linear-gradient(93deg, #34c38f 80%, #70e7bc 100%);
    color: #fff;
    font-size: 11px;
    font-weight: 500;
    border-radius: 7px;
    padding: 0.5px 8px;
    box-shadow: 0 1px 4px 0 rgba(80, 180, 120, 0.08);
    letter-spacing: 1px;
}

.card-info {
    font-size: 13px;
    color: #45ad8b;
    margin-bottom: 15px;
    margin-top: 3px;
}

.card-actions {
    display: flex;
    gap: 5px;
    justify-content: flex-end;
    align-items: center;
    margin-top: auto;
}
</style>
