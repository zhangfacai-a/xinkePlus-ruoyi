<template>
  <div class="login-page">
    <!-- 左侧栏 -->
    <div class="login-left">
      <div class="logo-box">
        <img src="@/assets/logo/logo.png" class="logo-img" alt="logo" />
        <span class="logo-title">Xinke Plus</span>
      </div>
      <div class="login-illustration">
        <img src="@/assets/images/login/draw1.png" alt="illustration" />
      </div>
      <div class="login-desc">
        <div class="desc-title">专注于用户体验的后台管理系统</div>
        <div class="desc-subtitle">美观实用的界面，经过视觉优化，带来卓越的用户体验</div>
      </div>
    </div>

    <!-- 右侧栏 -->
    <div class="login-right">
      <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form" @keyup.enter="handleLogin">
        <h2 class="login-title">欢迎回来</h2>
        <div class="login-subtitle">输入您的账号和密码登录</div>
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="账号" size="large" clearable>
            <template #prefix><svg-icon icon-class="user" /></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" size="large" show-password>
            <template #prefix><svg-icon icon-class="password" /></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code" v-if="captchaEnabled" class="code-row">
          <div class="code-flex">
            <el-input v-model="loginForm.code" placeholder="验证码" size="large">
              <template #prefix><svg-icon icon-class="validCode" /></template>
            </el-input>
            <img :src="codeUrl" @click="getCode" class="login-code-img" />
          </div>
        </el-form-item>
        <el-button :loading="loading" class="login-btn" type="success" size="large" @click.prevent="handleLogin"
          style="width: 100%;">
          <span v-if="!loading">登录</span>
          <span v-else>登录中...</span>
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { getCodeImg } from "@/api/login"
import useUserStore from '@/store/modules/user'

const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const { proxy } = getCurrentInstance()

const loginForm = ref({
  username: "admin",
  password: "admin123",
  code: "",
  uuid: ""
})

const loginRules = {
  username: [{ required: true, trigger: "blur", message: "请输入您的账号" }],
  password: [{ required: true, trigger: "blur", message: "请输入您的密码" }],
  code: [{ required: true, trigger: "change", message: "请输入验证码" }]
}

const codeUrl = ref("")
const loading = ref(false)
const captchaEnabled = ref(true)
const redirect = ref(undefined)

watch(route, (newRoute) => {
  redirect.value = newRoute.query && newRoute.query.redirect
}, { immediate: true })

function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (valid) {
      loading.value = true
      userStore.login(loginForm.value).then(() => {
        const query = route.query
        const otherQueryParams = Object.keys(query).reduce((acc, cur) => {
          if (cur !== "redirect") acc[cur] = query[cur]
          return acc
        }, {})
        router.push({ path: redirect.value || "/", query: otherQueryParams })
      }).catch(() => {
        loading.value = false
        if (captchaEnabled.value) getCode()
      })
    }
  })
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img
      loginForm.value.uuid = res.uuid
    }
  })
}

getCode()
</script>

<style lang="scss" scoped>
.login-page {
  display: flex;
  height: 100vh;
  width: 100vw;
  background: #fff;

  .login-left {
    flex: 1.25;
    background-image: url('@/assets/images/login/lf_bg.webp');
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    color: #fff;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    position: relative;
    padding: 40px 0 0 60px;
    min-width: 480px;

    .logo-box {
      display: flex;
      align-items: center;
      margin-bottom: 60px;

      .logo-img {
        height: 36px;
      }

      .logo-title {
        font-size: 20px;
        font-weight: 700;
        margin-left: 14px;
      }
    }

    .login-illustration {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100%;
      width: 100%;

      img {
        max-width: 400px;
        width: 80%;
        height: auto;
        display: block;
        margin: 0 auto;
      }
    }

    .login-desc {
      position: absolute;
      bottom: 48px;
      left: 60px;

      .desc-title {
        font-size: 22px;
        font-weight: 700;
        margin-bottom: 8px;
      }

      .desc-subtitle {
        font-size: 14px;
        color: #e5f1fe;
        opacity: 0.8;
      }
    }
  }

  .login-right {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    background: #fff;

    .login-form {
      width: 350px;
      padding: 0 10px;

      .login-title {
        font-size: 32px;
        font-weight: 700;
        color: #1b2b50;
        margin-bottom: 6px;
        text-align: left;
      }

      .login-subtitle {
        font-size: 15px;
        color: #8892b0;
        margin-bottom: 24px;
        text-align: left;
      }

      .el-form-item {
        margin-bottom: 18px;
      }

      .login-btn {
        background: #5fb878;
        border: none;
        font-size: 18px;
        height: 44px;
        margin-bottom: 16px;

        &:hover,
        &:focus {
          background: #48b25c;
        }
      }
    }

    // 验证码输入框和图片一行显示
    .code-row {
      .code-flex {
        display: flex;
        align-items: center;
        gap: 12px;
        width: 100%;

        .el-input {
          flex: 1 1 0%;

          .el-input__wrapper,
          input {
            height: 40px !important;
            min-height: 40px !important;
          }
        }

        .login-code-img {
          height: 40px;
          width: auto;
          cursor: pointer;
          border-radius: 6px;
          border: 1px solid #e5e6eb;
          box-sizing: border-box;
          display: block;
          object-fit: cover;
          margin-left: 0;
        }
      }
    }
  }
}

@media (max-width: 1000px) {
  .login-page {
    flex-direction: column;

    .login-left {
      display: none;
    }

    .login-right {
      flex: 1;
    }
  }
}
</style>
