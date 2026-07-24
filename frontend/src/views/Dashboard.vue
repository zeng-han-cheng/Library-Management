<template>
  <div class="dashboard-page">
    <div class="hero-row">
      <div><div class="eyebrow">{{ isAdmin ? 'ADMIN DASHBOARD' : 'READER DASHBOARD' }}</div><h2>{{ isAdmin ? '欢迎回来，开始管理今天的馆藏' : '欢迎回来，开启今天的阅读' }}</h2><p class="hero-copy">{{ isAdmin ? '实时掌握馆藏、借阅和分类数据，让图书馆运营更清晰。' : '发现好书、管理借阅记录，享受更轻松的阅读体验。' }}</p></div>
      <div class="hero-badge"><span class="pulse"></span>数据每 15 秒自动同步</div>
    </div>
    <div class="metric-grid">
      <div class="metric"><small>{{ isAdmin ? '馆藏图书' : '可借图书种类' }}</small><h2>{{ isAdmin ? adminStats.total : readerStats.availableBooks }}</h2><span class="metric-note">{{ isAdmin ? '当前馆藏种类' : '等你探索' }}</span></div>
      <div class="metric"><small>可借库存</small><h2>{{ isAdmin ? adminStats.available : readerStats.availableStock }}</h2><span class="metric-note">库存实时更新</span></div>
      <div class="metric"><small>{{ isAdmin ? '借阅中' : '我的借阅中' }}</small><h2>{{ isAdmin ? adminStats.borrowing : readerStats.borrowing }}</h2><span class="metric-note">当前进行中的记录</span></div>
      <div class="metric"><small>{{ isAdmin ? '分类数量' : '历史借阅记录' }}</small><h2>{{ isAdmin ? adminStats.categories : readerStats.records }}</h2><span class="metric-note">持续积累中</span></div>
    </div>
    <div class="dashboard-grid">
      <div class="page-card welcome-card"><div class="card-title"><div><span class="eyebrow">QUICK START</span><h3>{{ isAdmin ? '运营工作台' : '阅读服务' }}</h3></div><div class="spark-icon">✦</div></div><p>{{ isAdmin ? '从图书、读者、公告和借阅四个维度管理图书馆，让每一项运营决策都有数据支持。' : '从公告中心了解最新动态，在我的借阅中选择图书，还可以让 AI 助手帮你发现下一本好书。' }}</p><div class="quick-links"><router-link v-if="isAdmin" to="/books">管理馆藏 <span>→</span></router-link><router-link v-else to="/borrows">开始借阅 <span>→</span></router-link><router-link to="/ai">{{ isAdmin ? '获取运营建议' : '让 AI 推荐一本' }} <span>→</span></router-link></div></div>
      <div class="page-card insight-card"><div class="card-title"><div><span class="eyebrow">SYSTEM STATUS</span><h3>系统状态</h3></div><span class="status-pill"><i></i>运行正常</span></div><div class="status-line"><span>数据服务</span><b>稳定</b></div><div class="status-line"><span>权限认证</span><b>已保护</b></div><div class="status-line"><span>智能助手</span><b class="purple">{{ isAdmin ? '运营模式' : '推荐模式' }}</b></div></div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'
const user = JSON.parse(localStorage.getItem('library_user') || 'null')
const isAdmin = computed(() => user?.role === 'ADMIN')
const adminStats = reactive({ total: 0, available: 0, borrowing: 0, categories: 0 })
const readerStats = reactive({ availableBooks: 0, availableStock: 0, borrowing: 0, records: 0 })
let timer
async function loadOverview() { try { if (isAdmin.value) { const response = await http.get('/books/overview'); const data = response.data || {}; adminStats.total = Number(data.bookCount || 0); adminStats.available = Number(data.availableStock || 0); adminStats.borrowing = Number(data.borrowingCount || 0); adminStats.categories = Number(data.categoryCount || 0); return } const [bookResponse, borrowResponse] = await Promise.all([http.get('/books/available-overview'), http.get('/borrows/overview')]); const books = bookResponse.data || {}; const borrows = borrowResponse.data || {}; readerStats.availableBooks = Number(books.availableBookCount || 0); readerStats.availableStock = Number(books.availableStock || 0); readerStats.borrowing = Number(borrows.borrowingCount || 0); readerStats.records = Number(borrows.recordCount || 0) } catch (error) { ElMessage.error(error.response?.data?.message || error.message || '首页数据加载失败') } }
onMounted(() => { loadOverview(); timer = window.setInterval(loadOverview, 15000) })
onUnmounted(() => window.clearInterval(timer))
</script>

<style scoped>
.hero-row { display: flex; justify-content: space-between; align-items: flex-end; gap: 20px; margin-bottom: 26px; }.eyebrow { color: #818cf8; font-size: 11px; font-weight: 800; letter-spacing: .13em; }.hero-row h2 { margin: 8px 0 9px; font-size: 28px; }.hero-copy { margin: 0; color: #7b879c; font-size: 14px; }.hero-badge { padding: 10px 14px; color: #64748b; border: 1px solid #e2e8f0; border-radius: 12px; background: rgba(255, 255, 255, .7); font-size: 12px; white-space: nowrap; }.pulse { display: inline-block; width: 7px; height: 7px; margin-right: 7px; border-radius: 50%; background: #34d399; box-shadow: 0 0 0 4px rgba(52, 211, 153, .12); }.metric-note { position: relative; z-index: 1; display: block; margin-top: 12px; color: rgba(255, 255, 255, .7); font-size: 11px; }.dashboard-grid { display: grid; grid-template-columns: 1.4fr 1fr; gap: 18px; margin-top: 22px; }.welcome-card, .insight-card { min-height: 235px; }.card-title { display: flex; justify-content: space-between; align-items: flex-start; }.card-title h3 { margin: 8px 0 0; font-size: 19px; }.spark-icon { display: grid; width: 40px; height: 40px; place-items: center; color: #6366f1; border-radius: 13px; background: #eef2ff; font-size: 20px; }.welcome-card > p { max-width: 530px; margin: 22px 0; color: #738098; line-height: 1.8; }.quick-links { display: flex; gap: 14px; flex-wrap: wrap; }.quick-links a { padding: 11px 14px; color: #4f46e5; border: 1px solid #e0e7ff; border-radius: 11px; background: #f8f9ff; font-size: 13px; font-weight: 700; text-decoration: none; }.quick-links a span { margin-left: 10px; }.status-pill { padding: 7px 10px; color: #059669; border-radius: 20px; background: #ecfdf5; font-size: 11px; font-weight: 700; }.status-pill i { display: inline-block; width: 6px; height: 6px; margin-right: 6px; border-radius: 50%; background: #10b981; }.status-line { display: flex; justify-content: space-between; padding: 18px 0; color: #8792a6; border-bottom: 1px solid #edf1f6; font-size: 13px; }.status-line:last-child { border-bottom: 0; }.status-line b { color: #059669; }.status-line .purple { color: #7c3aed; }@media (max-width: 760px) { .hero-row { display: block; }.hero-badge { display: inline-block; margin-top: 14px; }.dashboard-grid { grid-template-columns: 1fr; } }
</style>
