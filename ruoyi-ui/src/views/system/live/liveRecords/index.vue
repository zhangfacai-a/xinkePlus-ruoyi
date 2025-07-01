<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px" @submit.prevent>
      <el-form-item label="主播" prop="nickname">
        <el-input v-model="queryParams.nickname" placeholder="请输入昵称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="直播间" prop="room">
        <el-input v-model="queryParams.room" placeholder="请输入直播间" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="直播日期">
        <el-date-picker v-model="queryParams.liveDateRange" type="daterange" range-separator="至"
          start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd"
          v-hasPermi="['system:records:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['system:records:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['system:records:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="recordsList" @selection-change="handleSelectionChange" style="width: 100%">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="WPS-ID" align="center" prop="fillId" width="80" />
      <el-table-column label="主播" align="center" prop="nickname" width="55" />
      <el-table-column label="直播间" align="center" prop="roomName" />
      <el-table-column label="直播时间" align="center" width="180">
        <template #default="scope">
          <div>{{ parseTime(scope.row.liveDate, '{y}/{m}/{d}') }}</div>
          <div>
            {{ formatTime(scope.row.startTime) }} - {{ formatTime(scope.row.endTime) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="直播时长" align="center" prop="liveMinutes"
        :formatter="(row, col, val) => (val == null ? '-' : val + ' 分钟')" />
      <el-table-column label="断播" align="center" prop="offlineMinutes"
        :formatter="(row, col, val) => val === null ? '-' : val + ' 分钟'" />
      <el-table-column label="观看人数" align="center" prop="totalViewers" />
      <el-table-column label="主播场观" align="center" prop="roomViewers" />
      <el-table-column label="成交人数" align="center" prop="dealUsers" />
      <el-table-column label="销售金额" align="center" prop="salesAmount" width="120">
        <template #default="scope">
          <span>{{ formatMoney(scope.row.salesAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="曝光人数" align="center" prop="exposure" />
      <el-table-column label="主播曝光人数" align="center" prop="anchorExposure" />
      <el-table-column label="转粉数" align="center" prop="newFollowers" />
      <el-table-column label="成交总人数" align="center" prop="totalDeals" />
      <el-table-column label="备注" align="center" prop="remark" width="180" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ scope.row.remark || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['system:records:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['system:records:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加/修改弹窗表单，两列布局 -->
    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="recordsRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="WPS-ID" prop="fillId">
              <el-input v-model.number="form.fillId" placeholder="请输入WPS-ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 主播姓名自动补全并允许新增 -->
            <el-form-item label="昵称" prop="nicknameId">
              <el-autocomplete v-model="form.nicknameInput" :fetch-suggestions="queryNicknames" placeholder="请输入或选择主播姓名"
                @select="handleNicknameSelect" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 直播间下拉 -->
            <el-form-item label="直播间" prop="roomId">
              <el-select v-model="form.roomId" filterable placeholder="请选择直播间" style="width:100%">
                <el-option v-for="room in roomList" :key="room.id" :label="room.roomName" :value="room.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="直播日期" prop="liveDate">
              <el-date-picker clearable v-model="form.liveDate" type="date" value-format="YYYY-MM-DD"
                placeholder="请选择直播日期" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-time-picker v-model="form.startTimeStr" format="HH:mm" value-format="HH:mm" placeholder="请选择开始时间"
                :step="stepTime" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-time-picker v-model="form.endTimeStr" format="HH:mm" value-format="HH:mm" placeholder="请选择结束时间"
                :step="stepTime" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="断播时长(分钟)" prop="offlineMinutes">
              <el-input v-model.number="form.offlineMinutes" placeholder="请输入断播时长" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="累计观看人数" prop="totalViewers">
              <el-input v-model.number="form.totalViewers" placeholder="请输入累计观看人数" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主播场观" prop="roomViewers">
              <el-input v-model.number="form.roomViewers" placeholder="请输入主播场观" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成交人数" prop="dealUsers">
              <el-input v-model.number="form.dealUsers" placeholder="请输入成交人数" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销售金额" prop="salesAmount">
              <el-input v-model.number="form.salesAmount" placeholder="请输入销售金额" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="曝光人数" prop="exposure">
              <el-input v-model.number="form.exposure" placeholder="请输入曝光人数" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主播曝光人数" prop="anchorExposure">
              <el-input v-model.number="form.anchorExposure" placeholder="请输入主播曝光人数" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="转粉数" prop="newFollowers">
              <el-input v-model.number="form.newFollowers" placeholder="请输入转粉数" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注信息" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成交总人数" prop="totalDeals">
              <el-input v-model.number="form.totalDeals" placeholder="请输入成交总人数" />
            </el-form-item>
          </el-col>
        </el-row>
        <div class="dialog-footer" style="margin-top: 20px;text-align:right;">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, toRefs, getCurrentInstance } from 'vue'
import { listRecords, getRecords, delRecords, addRecords, updateRecords, exportRecords } from '@/api/live/records'
import { listRooms } from '@/api/live/room'
import { listNicknames, addNicknames } from "@/api/live/nicknames"

const { proxy } = getCurrentInstance()

// 列表、分页等
const stepTime = '00:01'
const recordsList = ref([])
const total = ref(0)
const loading = ref(false)
const open = ref(false)
const title = ref('')
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const showSearch = ref(true)

// 房间下拉、主播自动补全
const roomList = ref([])
const nicknameCache = ref([])

// 表单和查询参数
const data = reactive({
  form: {
    id: null,
    fillId: null,
    nicknameId: null,         // 主播ID（最终提交）
    nicknameInput: '',        // 自动补全用
    roomId: null,             // 房间ID
    liveDate: null,           // YYYY-MM-DD
    startTimeStr: '',         // "HH:mm"
    endTimeStr: '',           // "HH:mm"
    startTime: null,          // 分钟数
    endTime: null,            // 分钟数
    offlineMinutes: null,
    totalViewers: null,
    roomViewers: null,
    dealUsers: null,
    salesAmount: null,
    exposure: null,
    anchorExposure: null,
    newFollowers: null,
    remark: '',
    totalDeals: null
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    nickname: '',
    room: '',
    liveDateRange: []
  },
  rules: {
    nicknameInput: [{ required: true, message: '昵称不能为空', trigger: 'blur' }],
    roomId: [{ required: true, message: '直播间不能为空', trigger: 'change' }],
    liveDate: [{ required: true, message: '直播日期不能为空', trigger: 'change' }],
    startTimeStr: [{ required: true, message: '开始时间不能为空', trigger: 'change' }],
    endTimeStr: [{ required: true, message: '结束时间不能为空', trigger: 'change' }]
  }
})

const { form, queryParams, rules } = toRefs(data)

// ========== 格式化方法 ==========
function formatTime(minute) {
  if (minute === null || minute === undefined || isNaN(minute)) return ''
  const h = String(Math.floor(minute / 60)).padStart(2, '0')
  const m = String(minute % 60).padStart(2, '0')
  return `${h}:${m}`
}
function formatMoney(value) {
  if (value === null || value === undefined || value === '') return '-'
  const num = Number(value)
  if (isNaN(num)) return '-'
  return '￥' + num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
function parseTime(timestamp, format = '{y}/{m}/{d}') {
  if (!timestamp) return '-'
  let date
  if (typeof timestamp === 'number' && String(timestamp).length === 10) {
    date = new Date(timestamp * 1000)
  } else {
    date = new Date(timestamp)
  }
  const formatObj = {
    y: date.getFullYear(),
    m: String(date.getMonth() + 1).padStart(2, '0'),
    d: String(date.getDate()).padStart(2, '0'),
    h: String(date.getHours()).padStart(2, '0'),
    i: String(date.getMinutes()).padStart(2, '0'),
    s: String(date.getSeconds()).padStart(2, '0')
  }
  return format.replace(/{([ymdhis])+}/g, (_, key) => formatObj[key] || '')
}
function timeStrToMinute(str) {
  if (!str) return null
  const [h, m] = str.split(':').map(Number)
  return h * 60 + m
}
function minuteToTimeStr(min) {
  if (min === null || min === undefined) return ''
  const h = String(Math.floor(min / 60)).padStart(2, '0')
  const m = String(min % 60).padStart(2, '0')
  return `${h}:${m}`
}

// ========== 主播姓名自动补全 ==========
function queryNicknames(queryString, cb) {
  listNicknames({ nickname: queryString }).then(res => {
    const arr = res.rows || []
    nicknameCache.value = arr
    cb(arr.map(i => ({ value: i.nickname, id: i.id })))
  })
}
function handleNicknameSelect(item) {
  form.value.nicknameId = item.id
  form.value.nicknameInput = item.value
}
function setNicknameInput(id) {
  if (!id) {
    form.value.nicknameInput = ''
    return
  }
  const found = nicknameCache.value.find(i => i.id === id)
  if (found) form.value.nicknameInput = found.nickname
  else {
    listNicknames({ id }).then(res => {
      if (res.rows && res.rows.length) {
        form.value.nicknameInput = res.rows[0].nickname
      }
    })
  }
}

// ========== 直播间下拉 ==========
function loadRooms() {
  listRooms().then(res => {
    roomList.value = res.rows || []
  })
}

// ========== 表格相关 ==========
function getList() {
  loading.value = true
  let startTime = null, endTime = null
  if (queryParams.value.liveDateRange && queryParams.value.liveDateRange.length === 2) {
    startTime = Math.floor(new Date(queryParams.value.liveDateRange[0]).setHours(0, 0, 0, 0) / 1000)
    endTime = Math.floor(new Date(queryParams.value.liveDateRange[1]).setHours(0, 0, 0, 0) / 1000)
  }
  const params = {
    pageNum: queryParams.value.pageNum,
    pageSize: queryParams.value.pageSize,
    nickname: queryParams.value.nickname,
    room: queryParams.value.room,
    startTime,
    endTime
  }
  listRecords(params)
    .then((res) => {
      recordsList.value = res.rows || []
      total.value = res.total || 0
    })
    .finally(() => {
      loading.value = false
    })
}
function resetQuery() {
  proxy.resetForm('queryRef')
  queryParams.value.nickname = ''
  queryParams.value.room = ''
  queryParams.value.liveDateRange = []
  queryParams.value.pageNum = 1
  queryParams.value.pageSize = 10
  getList()
}
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id)
  single.value = selection.length !== 1
  multiple.value = selection.length === 0
}

// ========== 弹窗/表单 ==========
function resetForm() {
  form.value = {
    id: null,
    fillId: null,
    nicknameId: null,
    nicknameInput: '',
    roomId: null,
    liveDate: null,
    startTimeStr: '',
    endTimeStr: '',
    startTime: null,
    endTime: null,
    offlineMinutes: null,
    totalViewers: null,
    roomViewers: null,
    dealUsers: null,
    salesAmount: null,
    exposure: null,
    anchorExposure: null,
    newFollowers: null,
    remark: '',
    totalDeals: null
  }
  proxy.resetForm('recordsRef')
}

function handleAdd() {
  resetForm()
  open.value = true
  title.value = '添加上播时间'
}


function handleUpdate(row) {
  resetForm()
  getRecords(row.id).then((res) => {
    Object.assign(form.value, res.data || {})
    // 正确赋值本地日期
    form.value.nicknameInput = res.data.nickname || ''
    if (form.value.liveDate) {
      form.value.liveDate = timestampToDateStr(form.value.liveDate)
    }
    form.value.startTimeStr = minuteToTimeStr(form.value.startTime)
    form.value.endTimeStr = minuteToTimeStr(form.value.endTime)
    open.value = true
    title.value = '修改上播时间'
  })
}

function timestampToDateStr(ts) {
  if (!ts) return ''
  const date = new Date(ts * 1000)
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}


function cancel() {
  open.value = false
  resetForm()
}

// ========== 新增/修改 ==========
async function submitForm() {
  proxy.$refs.recordsRef.validate(async (valid) => {
    if (!valid) return
    // 主播ID不存在则新建
    if (!form.value.nicknameId && form.value.nicknameInput) {
      const res = await listNicknames({ nickname: form.value.nicknameInput })
      let match = (res.rows || []).find(i => i.nickname === form.value.nicknameInput)
      if (match) {
        form.value.nicknameId = match.id
      } else {
        const addRes = await addNicknames({ nickname: form.value.nicknameInput })
        form.value.nicknameId = addRes.data?.id || addRes.data || addRes.id
      }
    }
    // 直播日期转时间戳
    if (form.value.liveDate) {
      const dateObj = new Date(form.value.liveDate + ' 00:00:00')
      form.value.liveDate = Math.floor(dateObj.getTime() / 1000)
    }
    if (form.value.startTimeStr) form.value.startTime = timeStrToMinute(form.value.startTimeStr)
    if (form.value.endTimeStr) form.value.endTime = timeStrToMinute(form.value.endTimeStr)
    // 提交只传ID，去掉多余属性
    const submitData = { ...form.value }
    submitData.nickname = undefined
    submitData.nicknameInput = undefined
    submitData.roomName = undefined
    submitData.startTimeStr = undefined
    submitData.endTimeStr = undefined

    if (form.value.id) {
      updateRecords(submitData).then(() => {
        proxy.$modal.msgSuccess('修改成功')
        open.value = false
        getList()
      })
    } else {
      addRecords(submitData).then(() => {
        proxy.$modal.msgSuccess('新增成功')
        open.value = false
        getList()
      })
    }
  })
}

// ========== 删除/导出 ==========
function handleDelete(row) {
  const delIds = row && row.id ? [row.id] : ids.value
  if (!delIds || delIds.length === 0) {
    proxy.$modal.msgWarning('请选择要删除的数据')
    return
  }
  proxy
    .$modal.confirm(`是否确认删除选中的 ${delIds.length} 条数据？`)
    .then(() => delRecords(delIds))
    .then(() => {
      proxy.$modal.msgSuccess('删除成功')
      getList()
    })
    .catch(() => { })
}
function handleExport() {
  let startTime = null, endTime = null
  if (queryParams.value.liveDateRange && queryParams.value.liveDateRange.length === 2) {
    startTime = Math.floor(new Date(queryParams.value.liveDateRange[0]).setHours(0, 0, 0, 0) / 1000)
    endTime = Math.floor(new Date(queryParams.value.liveDateRange[1]).setHours(0, 0, 0, 0) / 1000)
  }
  const exportParams = {
    pageNum: queryParams.value.pageNum,
    pageSize: queryParams.value.pageSize,
    nickname: queryParams.value.nickname,
    room: queryParams.value.room,
    startTime,
    endTime
  }
  exportRecords(exportParams).then(res => {
    // 这里是前端下载文件的标准写法
    const blob = new Blob([res], { type: 'application/vnd.ms-excel' })
    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blob)
    link.download = `上播时间数据_${new Date().toLocaleDateString().replace(/\//g, '-')}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(link.href)
  })
}

// ====== 初始化加载 ======
loadRooms()
getList()
</script>


<style scoped>
.el-table .cell div {
  line-height: 1.4;
}

.el-form-item {
  margin-bottom: 12px;
}

.el-date-editor {
  width: 100%;
}

.el-date-editor .el-range-separator {
  padding: 0 8px;
  color: #666;
}

.dialog-footer {
  text-align: right;
  margin-top: 20px;
}

.el-button {
  min-width: 80px;
}
</style>
