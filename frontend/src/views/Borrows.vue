<template>
  <div class="page-card">
    <h2>{{ isAdmin ? '借阅管理' : '我的借阅' }}</h2>
    <div class="toolbar">
      <el-button type="primary" @click="openBorrowDialog">借阅图书</el-button>
      <el-button @click="load">刷新</el-button>
    </div>
    <el-table v-loading="loading" :data="rows" stripe>
      <el-table-column prop="bookTitle" label="图书" />
      <el-table-column v-if="isAdmin" prop="readerName" label="读者" />
      <el-table-column prop="borrowTime" label="借出时间" min-width="160" />
      <el-table-column prop="dueTime" label="应还时间" min-width="160" />
      <el-table-column label="状态"><template #default="scope"><el-tag :type="scope.row.status === 1 ? 'warning' : scope.row.status === 2 ? 'success' : 'danger'">{{ statusText(scope.row.status) }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="170"><template #default="scope"><el-button v-if="scope.row.status === 1" link type="primary" @click="returnBook(scope.row, 2)">正常归还</el-button><el-button v-if="isAdmin && scope.row.status === 1" link type="danger" @click="returnBook(scope.row, 3)">异常归还</el-button></template></el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" layout="prev, pager, next" :total="total" @current-change="load" />

    <el-dialog v-model="borrowVisible" title="借阅图书" width="500px">
      <el-form :model="borrowForm" label-width="100px">
        <el-form-item v-if="isAdmin" label="读者编号" required><el-input-number v-model="borrowForm.readerId" :min="1" /></el-form-item>
        <el-form-item label="图书" required>
          <el-select v-model="borrowForm.bookId" filterable style="width: 100%" placeholder="请选择可借图书" @change="onBookChange">
            <el-option v-for="book in books" :key="book.id" :label="`${book.title}（可借库存：${book.availableStock}）`" :value="book.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="借阅数量" required><el-input-number v-model="borrowForm.quantity" :min="1" :max="quantityLimit" /><span class="inventory-tip">当前图书可借 {{ selectedBook?.availableStock || 0 }} 本</span></el-form-item>
        <el-form-item label="借阅天数" required><el-input-number v-model="borrowForm.days" :min="1" :max="90" /><span class="inventory-tip">最多 90 天</span></el-form-item>
        <el-form-item label="备注"><el-input v-model.trim="borrowForm.remark" maxlength="255" show-word-limit /></el-form-item>
      </el-form>
      <template #footer><el-button @click="borrowVisible = false">取消</el-button><el-button type="primary" :loading="submitting" @click="borrow">确认借阅</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../api/http'

const user = JSON.parse(localStorage.getItem('library_user') || 'null')
const isAdmin = computed(() => user?.role === 'ADMIN')
const rows = ref([])
const books = ref([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)
const submitting = ref(false)
const borrowVisible = ref(false)
const borrowForm = reactive({ readerId: null, bookId: null, quantity: 1, days: 30, remark: '' })
const selectedBook = computed(() => books.value.find(book => book.id === borrowForm.bookId))
const quantityLimit = computed(() => Math.max(1, Number(selectedBook.value?.availableStock || 1)))
const statusText = status => ({ 1: '借阅中', 2: '正常归还', 3: '异常归还', 4: '已逾期' }[status] || '未知')

async function load() {
  loading.value = true
  try {
    const response = await http.get('/borrows', { params: { page: page.value, size: 20 } })
    if (response.code !== 200) throw new Error(response.message)
    rows.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || '借阅记录加载失败')
  } finally {
    loading.value = false
  }
}

async function loadBooks() {
  const response = await http.get('/books', { params: { page: 1, size: 100, status: 1 } })
  if (response.code === 200) books.value = response.data.records.filter(book => Number(book.availableStock) > 0)
}

function onBookChange() {
  borrowForm.quantity = 1
}

async function openBorrowDialog() {
  Object.assign(borrowForm, { readerId: null, bookId: null, quantity: 1, days: 30, remark: '' })
  await loadBooks()
  if (!books.value.length) ElMessage.warning('当前暂无可借图书')
  borrowVisible.value = true
}

async function borrow() {
  if (isAdmin.value && !borrowForm.readerId) {
    ElMessage.warning('请输入读者编号')
    return
  }
  if (!borrowForm.bookId) {
    ElMessage.warning('请选择图书')
    return
  }
  if (borrowForm.quantity > quantityLimit.value) {
    ElMessage.warning(`借阅数量不能超过可借库存 ${quantityLimit.value} 本`)
    return
  }
  submitting.value = true
  try {
    const response = await http.post('/borrows', borrowForm)
    if (response.code !== 200) throw new Error(response.message)
    ElMessage.success('借阅成功')
    borrowVisible.value = false
    await load()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || '借阅失败')
  } finally {
    submitting.value = false
  }
}

async function returnBook(row, status) {
  try {
    await ElMessageBox.confirm(status === 3 ? '确认以异常状态归还吗？' : '确认正常归还吗？', '归还确认')
    const response = await http.put('/borrows/return', { recordId: row.id, status })
    if (response.code !== 200) throw new Error(response.message)
    ElMessage.success('归还成功')
    await load()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') ElMessage.error(error.response?.data?.message || error.message || '归还失败')
  }
}

onMounted(load)
</script>

<style scoped>.inventory-tip { margin-left: 10px; color: #64748b; font-size: 12px; }</style>
