<template>
  <el-container class="layout">
    <el-aside width="220px">
      <div class="brand">图书管理系统</div>
      <el-menu router :default-active="$route.path" background-color="#172554" text-color="#cbd5e1" active-text-color="#fff">
        <el-menu-item index="/dashboard">首页</el-menu-item>
        <template v-if="isAdmin">
          <el-menu-item index="/books">图书管理</el-menu-item>
          <el-menu-item index="/categories">分类管理</el-menu-item>
          <el-menu-item index="/readers">读者管理</el-menu-item>
          <el-menu-item index="/notices">公告管理</el-menu-item>
          <el-menu-item index="/borrows">借阅管理</el-menu-item>
          <el-menu-item index="/statistics">统计分析</el-menu-item>
        </template>
        <template v-else>
          <el-menu-item index="/notices">公告</el-menu-item>
          <el-menu-item index="/borrows">我的借阅</el-menu-item>
        </template>
        <el-menu-item index="/ai">{{ isAdmin ? 'AI运营建议' : 'AI图书推荐' }}</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header><span>欢迎，{{ user?.realName }}（{{ isAdmin ? '管理员' : '读者' }}）</span><el-button link @click="logout">退出登录</el-button></el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()
const user = JSON.parse(localStorage.getItem('library_user') || 'null')
const isAdmin = computed(() => user?.role === 'ADMIN')
function logout() { localStorage.clear(); router.push('/login') }
</script>

<style scoped>
.layout { min-height: 100vh; }.el-aside { background: #172554; }.brand { height: 60px; color: #fff; font-weight: bold; font-size: 18px; display: grid; place-items: center; }.el-header { display: flex; justify-content: space-between; align-items: center; background: #fff; border-bottom: 1px solid #e5e7eb; }.el-main { padding: 24px; }.el-menu { border-right: 0; }
</style>
