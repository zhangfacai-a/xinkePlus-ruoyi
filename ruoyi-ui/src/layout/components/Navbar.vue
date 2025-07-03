<template>
  <div class="navbar">
    <div class="navbar-left">
      <hamburger id="hamburger-container" :is-active="appStore.sidebar.opened" class="hamburger-container"
        @toggleClick="toggleSideBar" />
      <breadcrumb id="breadcrumb-container" class="breadcrumb-container" />
    </div>
    <!-- <top-nav v-if="settingsStore.topNav" id="topmenu-container" class="topmenu-container" /> -->
    <div class="navbar-right">
      <div class="right-menu">
        <template v-if="appStore.device !== 'mobile'">
          <el-tooltip content="主题模式" effect="dark" placement="bottom">
            <div class="right-menu-item hover-effect theme-switch-wrapper" @click="toggleTheme">
              <svg-icon v-if="settingsStore.isDark" icon-class="sunny" />
              <svg-icon v-if="!settingsStore.isDark" icon-class="moon" />
            </div>
          </el-tooltip>
        </template>
        <el-dropdown @command="handleCommand" class="avatar-container right-menu-item hover-effect" trigger="hover">
          <div class="avatar-wrapper">
            <img :src="userStore.avatar" class="user-avatar" />
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <router-link to="/user/profile">
                <el-dropdown-item>个人中心</el-dropdown-item>
              </router-link>
              <el-dropdown-item divided command="logout">
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ElMessageBox } from 'element-plus'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import useAppStore from '@/store/modules/app'
import useUserStore from '@/store/modules/user'
import useSettingsStore from '@/store/modules/settings'

const appStore = useAppStore()
const userStore = useUserStore()
const settingsStore = useSettingsStore()

function toggleSideBar() {
  appStore.toggleSideBar()
}

function handleCommand(command) {
  switch (command) {
    case "setLayout":
      setLayout()
      break
    case "logout":
      logout()
      break
    default:
      break
  }
}

function logout() {
  ElMessageBox.confirm('确定注销并退出系统吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logOut().then(() => {
      location.href = '/index'
    })
  }).catch(() => { })
}

const emits = defineEmits(['setLayout'])
function setLayout() {
  emits('setLayout')
}

function toggleTheme() {
  settingsStore.toggleTheme()
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 30px;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--navbar-bg);
  // padding: 0 18px;
  margin: 8px 0px 0px 10px;
  box-sizing: border-box;

  .navbar-left {
    display: flex;
    align-items: center;
    flex: 1 1 auto;
    min-width: 0;

    .hamburger-container {
      margin-right: 10px;
      height: 40px;
      display: flex;
      align-items: center;
      cursor: pointer;

      &:hover {
        background: rgba(0, 0, 0, 0.025);
      }
    }

    .breadcrumb-container {
      display: flex;
      align-items: center;
    }
  }

  .navbar-right {
    display: flex;
    align-items: center;
    flex-shrink: 0;

    .right-menu {
      display: flex;
      align-items: center;

      .right-menu-item {
        display: flex;
        align-items: center;
        padding: 0 6px;
        font-size: 18px;
        color: #5a5e66;
        height: 40px;

        &.hover-effect {
          cursor: pointer;
          border-radius: 50%;
          transition: background 0.3s;

          &:hover {
            background: rgba(0, 0, 0, 0.04);
          }
        }

        &.theme-switch-wrapper {
          padding-right: 3px;

          svg {
            transition: transform 0.3s;

            &:hover {
              transform: scale(1.15);
            }
          }
        }
      }

      .avatar-container {
        margin-left: 10px;

        .avatar-wrapper {
          display: flex;
          align-items: center;

          .user-avatar {
            cursor: pointer;
            width: 36px;
            height: 36px;
            border-radius: 50%;
            border: 2px solid #eee;
          }
        }
      }
    }
  }
}
</style>
