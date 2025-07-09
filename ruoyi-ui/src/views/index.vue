<template>
  <div class="dashboard-root">
    <!-- 顶部统计卡片 -->
    <div class="dashboard-top">
      <div class="dashboard-card" v-for="item in statCardList" :key="item.title">
        <div>
          <div class="dashboard-card-title">{{ item.title }}</div>
          <div class="dashboard-card-value">{{ item.value }}</div>
          <div class="dashboard-card-sub">
            <span :class="item.change >= 0 ? 'up' : 'down'">
              较上周 {{ item.change > 0 ? '+' : '' }}{{ item.change }}%
            </span>
          </div>
        </div>
        <div class="dashboard-card-icon">
          <el-icon :size="28" :color="iconColor">
            <component :is="item.icon" />
          </el-icon>
        </div>
      </div>
    </div>

    <!-- 图表区 -->
    <div class="dashboard-row">
      <div class="dashboard-chart-card">
        <div ref="barRef" class="dashboard-chart"></div>
        <div class="dashboard-summary">
          <div>用户概述</div>
          <div class="dashboard-summary-sub">比上周 <span class="up">+23%</span></div>

          <div class="dashboard-summary-grid">
            <div>
              <div class="label">昨日提交次数</div>
              <div class="value">32k</div>
            </div>
            <div>
              <div class="label">昨日总直播时长</div>
              <div class="value">128k</div>
            </div>
            <div>
              <div class="label">昨日总销售金额</div>
              <div class="value">1.2k</div>
            </div>
            <div>
              <div class="label">周同比</div>
              <div class="value up">+5%</div>
            </div>
          </div>
        </div>
      </div>
      <div class="dashboard-chart-card">
        <div class="dashboard-title">
          访问量 <span class="dashboard-growth">今年增长 <span class="up">+15%</span></span>
        </div>
        <div ref="lineRef" class="dashboard-chart"></div>
      </div>
    </div>

    <!-- 下方列表区：昨日直播时长<500分钟主播展示 -->
    <div class="dashboard-bottom">
      <!-- 昨日直播时长<500分钟主播统计 -->
      <div class="user-table-card">
        <div class="user-table-header">
          <div>
            <div class="title">昨日低直播时长主播</div>
            <div class="sub">昨日统计 &lt; 300 分钟</div>
          </div>
        </div>
        <el-table :data="statList" border class="user-table" style="width:100%;">
          <el-table-column prop="nickname" label="姓名" width="90" />
          <el-table-column prop="roomNames" label="直播间" />
          <el-table-column prop="livePeriods" label="直播时段" />
          <el-table-column prop="totalMinutes" label="直播时长" width="140" />
          <el-table-column label="进度" width="170">
            <template #default="scope">
              <el-progress :percentage="Math.min(100, Math.round(scope.row.totalMinutes / 300 * 100))"
                :color="progressColor(scope.row.totalMinutes)" :text-inside="true" :stroke-width="16"
                style="width:120px" />
            </template>
          </el-table-column>
          <el-table-column prop="remarks" label="备注" />
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import * as echarts from 'echarts'
import { User, UserFilled, Promotion, TrendCharts, CircleCheckFilled } from '@element-plus/icons-vue'
import { listNicknames } from '@/api/live/stat' // 使用你的接口

// 顶部卡片区
const statCardList = [
  { title: '月度总直播时长', value: 9120, change: 20, icon: TrendCharts },
  { title: '月度总销售金额', value: 182, change: 10, icon: User },
  { title: '主播', value: 9520, change: -12, icon: Promotion },
  { title: '直播间', value: 156, change: 30, icon: UserFilled },
]
const iconColor = computed(() => 'var(--el-color-success-dark-2, #18b564)')

// 进度条颜色
function progressColor(val) {
  if (val < 200) return '#5AD8A6'
  if (val < 350) return '#5B8FF9'
  if (val < 450) return '#F6BD16'
  return '#E8684A'
}

// 统计列表
const statList = ref([])

onMounted(() => {
  // 拉取昨日直播低时长主播
  listNicknames().then(res => {
    console.log('昨日低直播时长主播数据：', res)
    statList.value = res || []
  })

  nextTick(() => {
    renderCharts()
    // 监听亮/暗色模式切换
    const observer = new MutationObserver(renderCharts)
    observer.observe(document.documentElement, { attributes: true, attributeFilter: ['class'] })
  })
})



// 图表
const barRef = ref()
const lineRef = ref()
function getVars() {
  const style = getComputedStyle(document.documentElement)
  return {
    main: style.getPropertyValue('#52d18e') || '#18b564',
    mainLight: style.getPropertyValue('--el-color-success-dark-2') || '#52d18e',
    bg: style.getPropertyValue('--el-bg-color') || '#fff',
    axis: style.getPropertyValue('--el-border-color-light') || '#eee',
    text: style.getPropertyValue('--el-text-color-regular') || '#666',
    split: style.getPropertyValue('--el-border-color-light') || '#f0f0f0',
    hover: style.getPropertyValue('--menu-hover') || '#e5f7ed'
  }
}
function renderCharts() {
  const vars = getVars()
  const bar = echarts.init(barRef.value)
  bar.setOption({
    color: [vars.mainLight.trim()],
    tooltip: {},
    grid: { left: 24, right: 24, top: 24, bottom: 24 },
    xAxis: {
      type: 'category',
      data: [1, 2, 3, 4, 5, 6, 7, 8, 9],
      axisTick: { show: false },
      axisLine: { lineStyle: { color: vars.axis.trim() } },
      axisLabel: { color: vars.text.trim() },
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: vars.split.trim() } },
      axisLine: { show: false },
      axisLabel: { color: vars.text.trim() },
    },
    series: [{
      type: 'bar',
      data: [150, 80, 140, 60, 180, 200, 120, 100, 160],
      barWidth: 30,
      itemStyle: {
        borderRadius: [6, 6, 0, 0],
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: vars.mainLight.trim() },
            { offset: 1, color: vars.hover.trim() }
          ]
        }
      }
    }]
  })
  const line = echarts.init(lineRef.value)
  line.setOption({
    color: [vars.main.trim()],
    tooltip: { trigger: 'axis' },
    grid: { left: 24, right: 24, top: 24, bottom: 24 },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
      axisTick: { show: false },
      axisLine: { lineStyle: { color: vars.axis.trim() } },
      axisLabel: { color: vars.text.trim() },
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: vars.split.trim() } },
      axisLine: { show: false },
      axisLabel: { color: vars.text.trim() },
    },
    series: [{
      type: 'line',
      data: [50, 30, 35, 40, 65, 40, 60, 25, 28, 18, 32, 40],
      smooth: true,
      lineStyle: { width: 3 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: vars.main.trim() + '40' },
          { offset: 1, color: vars.main.trim() + '0d' }
        ])
      },
      symbol: 'circle',
      symbolSize: 8
    }]
  })
  window.addEventListener('resize', () => { bar.resize(); line.resize(); })
}
</script>

<style scoped>
.dashboard-root {
  padding: 20px 18px 0 18px;
  background: var(--el-bg-color, #f5f6fa);
}

.dashboard-top {
  display: flex;
  gap: 16px;
  margin-bottom: 18px;
}

.dashboard-card {
  background: var(--el-bg-color, #fff);
  border-radius: 12px;
  box-shadow: 0 2px 8px 0 var(--el-border-color-light, #f0f1f2);
  flex: 1;
  min-width: 180px;
  padding: 20px 24px 16px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  overflow: hidden;
}

.dashboard-card-title {
  color: var(--el-text-color-regular, #7c8389);
  font-size: 15px;
  margin-bottom: 6px;
}

.dashboard-card-value {
  font-size: 30px;
  font-weight: bold;
  color: var(--el-text-color-primary, #484848);
  margin-bottom: 2px;
}

.dashboard-card-sub {
  font-size: 14px;
  color: var(--el-text-color-regular, #7c8389);
  margin-bottom: 8px;
}

.dashboard-card-icon {
  background: var(--menu-hover, #e5f7ed);
  border-radius: 8px;
  padding: 7px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dashboard-card-sub .up {
  color: #22c97a;
}

.dashboard-card-sub .down {
  color: #f5222d;
}

.up {
  color: #22c97a;
}

.down {
  color: #f5222d;
}

.dashboard-row {
  display: flex;
  gap: 18px;
  margin-bottom: 18px;
}

.dashboard-chart-card {
  background: var(--el-bg-color, #fff);
  border-radius: 12px;
  box-shadow: 0 2px 8px 0 var(--el-border-color-light, #f0f1f2);
  padding: 20px;
  flex: 1;
  min-width: 350px;
  color: var(--el-text-color-primary, #232d3c);
  display: flex;
  flex-direction: column;
  margin-bottom: 0;
}

.dashboard-chart {
  width: 100%;
  height: 220px;
  margin-bottom: 12px;
  background: transparent;
}

.dashboard-summary {
  margin-top: 8px;
}

.dashboard-summary-sub {
  color: #22c97a;
  margin-top: 2px;
}

.dashboard-summary-desc {
  color: var(--el-text-color-regular, #888);
  font-size: 13px;
  margin: 7px 0 14px 0;
}

.dashboard-summary-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px 24px;
}

.dashboard-summary-grid .label {
  font-size: 13px;
  color: var(--el-text-color-regular, #7c8389);
}

.dashboard-summary-grid .value {
  font-weight: bold;
  font-size: 17px;
  color: var(--el-text-color-primary, #222);
}

.dashboard-summary-grid .up {
  color: #22c97a;
}

.dashboard-title {
  font-size: 16px;
  font-weight: bold;
  color: var(--el-text-color-primary, #232d3c);
  margin-bottom: 10px;
}

.dashboard-growth {
  font-size: 13px;
  color: var(--el-text-color-regular, #888);
  margin-left: 8px;
}

.dashboard-bottom {
  display: flex;
  gap: 18px;
  margin-top: 18px;
}

.user-table-card {
  background: var(--el-bg-color, #fff);
  border-radius: 16px;
  box-shadow: 0 2px 8px 0 var(--el-border-color-light, #f0f1f2);
  padding: 24px 24px 16px 24px;
  min-width: 300px;
  flex: 1.4;
  display: flex;
  flex-direction: column;
}

.user-table-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 12px;
}

.title {
  font-size: 20px;
  font-weight: bold;
  color: var(--el-text-color-primary, #232d3c);
}

.sub {
  font-size: 14px;
  color: var(--el-text-color-regular, #888);
  margin-top: 2px;
}

.user-table-tabs {
  margin-left: auto;

  .el-radio-button__inner {
    border-radius: 6px !important;
    font-size: 15px !important;
    min-width: 56px !important;
    color: #18b564 !important;
    border-color: #e5f7ed !important;
    background: #f6faf7 !important;
  }

  .el-radio-button__original-radio:checked+.el-radio-button__inner {
    color: #fff !important;
    background: #18b564 !important;
    border-color: #18b564 !important;
  }
}

.user-table ::v-deep .el-table__header th {
  background: none !important;
  font-size: 16px;
  color: #7c8389;
  font-weight: 400;
}

.user-table ::v-deep .el-table__body td {
  background: none !important;
  font-size: 16px;
  color: #232d3c;
  border-bottom: 1px solid #f5f6fa !important;
}

.user-avatar {
  border-radius: 50%;
  width: 36px;
  height: 36px;
}

.user-name {
  font-size: 16px;
  color: #232d3c;
  margin-left: 8px;
}

.dashboard-list-card {
  background: var(--el-bg-color, #fff);
  border-radius: 16px;
  box-shadow: 0 2px 8px 0 var(--el-border-color-light, #f0f1f2);
  padding: 24px 20px 16px 20px;
  min-width: 240px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.dashboard-list-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 18px;
  color: var(--el-text-color-primary, #232d3c);
}

.dashboard-activity {
  list-style: none;
  padding: 0;
  margin: 0;
}

.dashboard-activity li {
  margin-bottom: 14px;
  font-size: 15px;
  color: var(--el-text-color-primary, #232d3c);
}

.dashboard-activity .user {
  color: #18b564;
  margin-right: 2px;
}

.dashboard-activity .tag {
  display: inline-block;
  background: #e5f7ed;
  color: #18b564;
  border-radius: 4px;
  font-size: 12px;
  padding: 1px 6px;
  margin: 0 2px;
}

.dashboard-activity .time {
  color: #888;
  font-size: 12px;
  margin-left: 8px;
}

.dashboard-todo {
  list-style: none;
  padding: 0;
  margin: 0;
}

.dashboard-todo li {
  display: flex;
  align-items: center;
  margin-bottom: 18px;
  color: var(--el-text-color-primary, #232d3c);
  font-size: 15px;
}

.dashboard-todo .time {
  color: #888;
  font-size: 12px;
  margin-left: auto;
}
</style>
<style>
.el-icon svg {
  fill: currentColor !important;
}
</style>
