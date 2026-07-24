<template>
  <el-container class="layout">
    <el-aside width="252px" class="sidebar">
      <div class="brand">
        <div class="brand-mark">L</div>
        <div><strong>Library</strong><small>智慧图书馆</small></div>
      </div>
      <div class="side-label">工作台</div>
      <el-menu class="side-menu" router :default-active="$route.path" background-color="transparent" text-color="#aeb9d1" active-text-color="#fff">
        <el-menu-item index="/dashboard"><span class="menu-dot"></span>首页总览</el-menu-item>
        <template v-if="isAdmin">
          <el-menu-item index="/books"><span class="menu-dot"></span>图书管理</el-menu-item>
          <el-menu-item index="/categories"><span class="menu-dot"></span>分类管理</el-menu-item>
          <el-menu-item index="/readers"><span class="menu-dot"></span>读者管理</el-menu-item>
          <el-menu-item index="/notices"><span class="menu-dot"></span>公告管理</el-menu-item>
          <el-menu-item index="/borrows"><span class="menu-dot"></span>借阅管理</el-menu-item>
          <el-menu-item index="/statistics"><span class="menu-dot"></span>统计分析</el-menu-item>
        </template>
        <template v-else>
          <el-menu-item index="/notices"><span class="menu-dot"></span>公告中心</el-menu-item>
          <el-menu-item index="/borrows"><span class="menu-dot"></span>我的借阅</el-menu-item>
        </template>
        <div class="side-label side-label-ai">智能服务</div>
        <el-menu-item index="/ai" class="ai-menu-item"><span class="menu-dot"></span>{{ isAdmin ? 'AI 运营建议' : 'AI 图书推荐' }}</el-menu-item>
      </el-menu>
      <div class="sidebar-footer"><span class="status-dot"></span>系统运行正常<small>Library OS · v1.0</small></div>
    </el-aside>
    <el-container>
      <el-header class="topbar">
        <div><div class="topbar-kicker">LIBRARY MANAGEMENT</div><strong>{{ isAdmin ? '管理工作台' : '读者服务中心' }}</strong></div>
        <div class="user-area"><div class="user-avatar">{{ user?.realName?.slice(0, 1) || 'U' }}</div><div class="user-copy"><b>{{ user?.realName }}</b><small>{{ isAdmin ? '管理员账户' : '读者账户' }}</small></div><el-button link @click="logout">退出登录</el-button></div>
      </el-header>
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
.layout { min-height: 100vh; }
.sidebar { position: relative; display: flex; flex-direction: column; min-height: 100vh; padding: 0 14px; background: linear-gradient(180deg, #111936 0%, #1b2450 100%); box-shadow: 14px 0 35px rgba(15, 23, 42, .08); }
.brand { display: flex; align-items: center; gap: 12px; height: 84px; padding: 0 10px; color: #fff; }
.brand strong { display: block; font-size: 19px; letter-spacing: .02em; }
.brand small { display: block; margin-top: 3px; color: #8492b3; font-size: 11px; }
.brand-mark { display: grid; width: 40px; height: 40px; place-items: center; color: #fff; border-radius: 13px; background: linear-gradient(135deg, #818cf8, #4f46e5); box-shadow: 0 8px 20px rgba(99, 102, 241, .35); font-size: 22px; font-weight: 800; }
.side-label { padding: 14px 14px 9px; color: #6f7da2; font-size: 11px; font-weight: 700; letter-spacing: .12em; text-transform: uppercase; }
.side-label-ai { margin-top: 10px; }
.side-menu { border-right: 0; }
.side-menu :deep(.el-menu-item) { height: 46px; margin: 4px 0; border-radius: 12px; font-size: 14px; }
.side-menu :deep(.el-menu-item:hover) { background: rgba(129, 140, 248, .12); }
.side-menu :deep(.el-menu-item.is-active) { background: linear-gradient(90deg, rgba(99, 102, 241, .85), rgba(99, 102, 241, .35)); box-shadow: inset 3px 0 #c7d2fe, 0 8px 18px rgba(15, 23, 42, .14); }
.menu-dot { display: inline-block; width: 7px; height: 7px; margin-right: 13px; border: 1px solid #8290b0; border-radius: 50%; }
.is-active .menu-dot { border-color: #fff; background: #fff; }
.sidebar-footer { margin-top: auto; padding: 18px 12px 22px; color: #aeb9d1; font-size: 12px; }
.sidebar-footer small { display: block; margin-top: 8px; color: #6f7da2; }
.status-dot { display: inline-block; width: 7px; height: 7px; margin-right: 8px; border-radius: 50%; background: #34d399; box-shadow: 0 0 0 4px rgba(52, 211, 153, .12); }
.topbar { display: flex; justify-content: space-between; align-items: center; height: 84px; padding: 0 34px; border-bottom: 1px solid rgba(226, 232, 240, .8); background: rgba(255, 255, 255, .72); backdrop-filter: blur(18px); }
.topbar-kicker { margin-bottom: 4px; color: #99a5ba; font-size: 10px; letter-spacing: .14em; }
.topbar strong { color: #27334d; font-size: 16px; }
.user-area { display: flex; align-items: center; gap: 11px; }
.user-avatar { display: grid; width: 36px; height: 36px; place-items: center; color: #fff; border-radius: 12px; background: linear-gradient(135deg, #6366f1, #a78bfa); font-weight: 700; }
.user-copy b, .user-copy small { display: block; }
.user-copy b { color: #334155; font-size: 13px; }
.user-copy small { margin-top: 2px; color: #94a3b8; font-size: 11px; }
.topbar :deep(.el-button) { margin-left: 10px; color: #7c3aed; }
.el-main { padding: 30px 34px 42px; }
@media (max-width: 760px) { .sidebar { width: 76px !important; padding: 0 8px; } .brand { justify-content: center; padding: 0; } .brand > div:last-child, .side-label, .sidebar-footer, .side-menu :deep(.el-menu-item) { font-size: 0; } .side-menu :deep(.el-menu-item) { justify-content: center; padding: 0 !important; } .menu-dot { margin: 0; } .topbar { padding: 0 16px; } .user-copy { display: none; } }
</style>
