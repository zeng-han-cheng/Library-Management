<template>
  <div>
    <template v-if="isAdmin">
      <h2>管理端首页</h2>
      <div class="metric-grid">
        <div class="metric"><small>图书总数</small><h2>{{ adminStats.total }}</h2></div>
        <div class="metric"><small>可借库存</small><h2>{{ adminStats.available }}</h2></div>
        <div class="metric"><small>借阅中</small><h2>{{ adminStats.borrowing }}</h2></div>
        <div class="metric"><small>分类数量</small><h2>{{ adminStats.categories }}</h2></div>
      </div>
      <div class="page-card welcome"><h3>系统概览</h3><p>数据会根据图书入库、借阅和归还操作实时更新。</p></div>
    </template>
    <template v-else>
      <h2>读者首页</h2>
      <div class="metric-grid">
        <div class="metric"><small>可借图书种类</small><h2>{{ readerStats.availableBooks }}</h2></div>
        <div class="metric"><small>可借库存</small><h2>{{ readerStats.availableStock }}</h2></div>
        <div class="metric"><small>我的借阅中</small><h2>{{ readerStats.borrowing }}</h2></div>
        <div class="metric"><small>历史借阅记录</small><h2>{{ readerStats.records }}</h2></div>
      </div>
      <div class="page-card welcome"><h3>欢迎使用图书管理系统</h3><p>可前往“我的借阅”选择图书、设置借阅天数和借阅数量。</p></div>
    </template>
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

async function loadOverview() {
  try {
    if (isAdmin.value) {
      const response = await http.get('/books/overview')
      const data = response.data || {}
      adminStats.total = Number(data.bookCount || 0)
      adminStats.available = Number(data.availableStock || 0)
      adminStats.borrowing = Number(data.borrowingCount || 0)
      adminStats.categories = Number(data.categoryCount || 0)
      return
    }
    const [bookResponse, borrowResponse] = await Promise.all([http.get('/books/available-overview'), http.get('/borrows/overview')])
    const books = bookResponse.data || {}
    const borrows = borrowResponse.data || {}
    readerStats.availableBooks = Number(books.availableBookCount || 0)
    readerStats.availableStock = Number(books.availableStock || 0)
    readerStats.borrowing = Number(borrows.borrowingCount || 0)
    readerStats.records = Number(borrows.recordCount || 0)
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || '首页数据加载失败')
  }
}

onMounted(() => {
  loadOverview()
  timer = window.setInterval(loadOverview, 15000)
})
onUnmounted(() => window.clearInterval(timer))
</script>

<style scoped>.welcome { margin-top: 22px; }</style>
