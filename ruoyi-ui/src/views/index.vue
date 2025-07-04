<template>
  <div class="dashboard-root">
    <!-- 顶部统计卡片 -->
    <div class="dashboard-top">
      <div class="dashboard-card" v-for="item in statList" :key="item.title">
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
          <div class="dashboard-summary-desc">
            我们为您创建了多个选项，可将它们组合在一起并定制为优雅完美的页面
          </div>
          <div class="dashboard-summary-grid">
            <div>
              <div class="label">总用户量</div>
              <div class="value">32k</div>
            </div>
            <div>
              <div class="label">总访问量</div>
              <div class="value">128k</div>
            </div>
            <div>
              <div class="label">日访问量</div>
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

    <!-- 下方列表区 -->
    <div class="dashboard-bottom">
      <!-- 新用户 -->
      <div class="user-table-card">
        <div class="user-table-header">
          <div>
            <div class="title">新用户</div>
            <div class="sub">这个月增长 <span class="up">+20%</span></div>
          </div>
          <el-radio-group v-model="tab" size="small" class="user-table-tabs">
            <el-radio-button label="本月" />
            <el-radio-button label="上月" />
            <el-radio-button label="今年" />
          </el-radio-group>
        </div>
        <el-table :data="userList" border :header-cell-style="{ background: 'none' }" class="user-table"
          style="width:100%;">
          <el-table-column label="头像" width="60">
            <template #default="scope">
              <el-avatar :src="scope.row.avatar" :size="40" />
            </template>
          </el-table-column>
          <el-table-column prop="name" label="" width="100">
            <template #default="scope">
              <span class="user-name">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="area" label="地区" width="80" />
          <el-table-column prop="sex" label="性别" width="60">
            <template #default="scope">
              {{ scope.row.sex === '1' ? '男' : '女' }}
            </template>
          </el-table-column>
          <el-table-column label="进度">
            <template #default="scope">
              <el-progress :percentage="scope.row.progress" :color="progressColor(scope.$index)" :stroke-width="6"
                show-text style="width:140px;" />
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- 动态 -->
      <div class="dashboard-list-card">
        <div class="dashboard-list-title">动态 <span class="dashboard-growth up">新增 +6</span></div>
        <ul class="dashboard-activity">
          <li v-for="(item, i) in activityList" :key="i">
            <span class="user">{{ item.user }}</span>
            <span v-html="item.action"></span>
            <span class="time">{{ item.time }}</span>
          </li>
        </ul>
      </div>
      <!-- 待办事项 -->
      <div class="dashboard-list-card">
        <div class="dashboard-list-title">代办事项 <span class="dashboard-growth down">待处理 3</span></div>
        <ul class="dashboard-todo">
          <li v-for="todo in todoList" :key="todo.text">
            <span>{{ todo.text }}</span>
            <span class="time">{{ todo.time }}</span>
            <el-icon v-if="todo.done" color="#18b564" style="margin-left:6px;">
              <CircleCheckFilled />
            </el-icon>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import * as echarts from 'echarts'
import { User, UserFilled, Promotion, TrendCharts, CircleCheckFilled } from '@element-plus/icons-vue'

// 顶部卡片数据
const statList = [
  { title: '总访问次数', value: 9120, change: 20, icon: TrendCharts },
  { title: '在线访客数', value: 182, change: 10, icon: User },
  { title: '点击量', value: 9520, change: -12, icon: Promotion },
  { title: '新用户', value: 156, change: 30, icon: UserFilled },
]
const iconColor = computed(() => 'var(--el-color-primary, #18b564)')
const progressColors = [
  '#5B8FF9', // 蓝
  '#5AD8A6', // 青
  '#F6BD16', // 橙
  '#5B8FF9', // 蓝
  '#E8684A', // 红橙
  '#5AD8A6', // 青
]
function progressColor(idx) {
  return progressColors[idx % progressColors.length]
}

// 新用户列表
const tab = ref('本月')
const userList = [
  { avatar: 'https://randomuser.me/api/portraits/men/32.jpg', name: '中小鱼', area: '北京', sex: '2', progress: 60 },
  { avatar: 'https://randomuser.me/api/portraits/men/33.jpg', name: '何小荷', area: '深圳', sex: '1', progress: 20 },
  { avatar: 'https://randomuser.me/api/portraits/men/34.jpg', name: '諸榮淞', area: '上海', sex: '1', progress: 60 },
  { avatar: 'https://randomuser.me/api/portraits/women/65.jpg', name: '发呆草', area: '长沙', sex: '2', progress: 50 },
  { avatar: 'https://randomuser.me/api/portraits/women/15.jpg', name: '甜简', area: '浙江', sex: '2', progress: 70 },
  { avatar: 'https://randomuser.me/api/portraits/men/45.jpg', name: '冷月呆呆', area: '湖北', sex: '1', progress: 90 },
]

// 动态列表
const activityList = [
  { user: '中小鱼', action: '关注了 <b style="color:#18b564;">许梓洵</b>', time: '5分钟前' },
  { user: '何小荷', action: '发表文章 <span class="tag">Vue3</span> <span class="tag">Typescript</span> <span class="tag">Vite</span>', time: '10分钟前' },
  { user: '许梓洵', action: '评论了 <b style="color:#18b564;">设计专栏</b>', time: '20分钟前' },
  { user: '发呆草', action: '收藏了 <b style="color:#18b564;">UI 灵感集</b>', time: '30分钟前' },
  { user: '甜简', action: '点赞了 <b style="color:#18b564;">产品思考</b>', time: '50分钟前' },
  { user: '冷月呆呆', action: '加入了 <b style="color:#18b564;">设计交流群</b>', time: '1小时前' },
]

// 待办事项
const todoList = [
  { text: '查看今天工作内容', time: '上午 09:30', done: true },
  { text: '回复邮件', time: '上午 10:30', done: true },
  { text: '工作汇报整理', time: '上午 11:00', done: false },
]

// 图表
const barRef = ref()
const lineRef = ref()
function getVars() {
  const style = getComputedStyle(document.documentElement)
  return {
    main: style.getPropertyValue('--el-color-primary') || '#18b564',
    mainLight: style.getPropertyValue('--el-color-primary-light-3') || '#52d18e',
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

onMounted(() => {
  nextTick(() => {
    renderCharts()
    // 监听亮/暗色模式切换
    const observer = new MutationObserver(renderCharts)
    observer.observe(document.documentElement, { attributes: true, attributeFilter: ['class'] })
  })
})
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
