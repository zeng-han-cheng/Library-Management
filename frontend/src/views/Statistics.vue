<template>
  <div>
    <h2>统计分析</h2>
    <div class="metric-grid">
      <div class="metric"><small>图书种类</small><h2>{{ totals.books }}</h2></div>
      <div class="metric"><small>总库存</small><h2>{{ totals.stock }}</h2></div>
      <div class="metric"><small>可借库存</small><h2>{{ totals.available }}</h2></div>
      <div class="metric"><small>分类数量</small><h2>{{ rows.length }}</h2></div>
    </div>
    <div class="page-card table-card">
      <div class="toolbar"><h3>按分类统计库存</h3><el-button @click="load">刷新</el-button></div>
      <el-table v-loading="loading" :data="rows" stripe empty-text="暂无统计数据">
        <el-table-column prop="categoryName" label="分类" />
        <el-table-column prop="bookCount" label="图书种类" />
        <el-table-column prop="totalStock" label="总库存" />
        <el-table-column prop="availableStock" label="可借库存" />
        <el-table-column label="可借比例"><template #default="scope">{{ availability(scope.row) }}</template></el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const rows = ref([])
const loading = ref(false)
const totals = reactive({ books: 0, stock: 0, available: 0 })
const number = value => Number(value || 0)

function availability(row) {
  const total = number(row.totalStock)
  return total ? `${Math.round(number(row.availableStock) * 100 / total)}%` : '0%'
}

async function load() {
  loading.value = true
  try {
    const response = await http.get('/books/stats')
    if (response.code !== 200) throw new Error(response.message)
    rows.value = response.data || []
    totals.books = rows.value.reduce((sum, item) => sum + number(item.bookCount), 0)
    totals.stock = rows.value.reduce((sum, item) => sum + number(item.totalStock), 0)
    totals.available = rows.value.reduce((sum, item) => sum + number(item.availableStock), 0)
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || '统计数据加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>.table-card { margin-top: 22px; }</style>
