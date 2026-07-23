<template>
  <div class="page-card">
    <h2>图书管理</h2>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="书名、作者或 ISBN" clearable @keyup.enter="load" style="width: 240px" />
      <el-button type="primary" @click="load">查询</el-button>
      <el-button v-if="isAdmin" type="success" @click="open">新增图书</el-button>
      <el-button v-if="isAdmin" @click="repairInventory">修复库存</el-button>
    </div>
    <el-table :data="rows" stripe>
      <el-table-column prop="isbn" label="ISBN" />
      <el-table-column prop="title" label="书名" />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="categoryName" label="分类" />
      <el-table-column prop="totalStock" label="总库存" />
      <el-table-column prop="availableStock" label="可借库存" />
      <el-table-column v-if="isAdmin" label="操作">
        <template #default="scope"><el-button link @click="open(scope.row)">编辑</el-button></template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" layout="prev, pager, next" :total="total" @current-change="load" />

    <el-dialog v-model="visible" title="图书信息">
      <el-form :model="form" label-width="100px">
        <el-form-item label="ISBN"><el-input v-model="form.isbn" /></el-form-item>
        <el-form-item label="书名"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="作者"><el-input v-model="form.author" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" style="width: 100%"><el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" /></el-select>
        </el-form-item>
        <el-form-item label="总库存"><el-input-number v-model="form.totalStock" :min="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="visible = false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const user = JSON.parse(localStorage.getItem('library_user') || 'null')
const isAdmin = computed(() => user?.role === 'ADMIN')
const rows = ref([])
const categories = ref([])
const total = ref(0)
const page = ref(1)
const keyword = ref('')
const visible = ref(false)
const form = reactive({})

async function load() {
  const response = await http.get('/books', { params: { page: page.value, size: 10, keyword: keyword.value } })
  rows.value = response.data.records
  total.value = response.data.total
}

function open(row) {
  Object.assign(form, row || { isbn: '', title: '', author: '', categoryId: null, totalStock: 0 })
  visible.value = true
}

async function save() {
  const response = form.id ? await http.put('/books', form) : await http.post('/books', form)
  if (response.code === 200) {
    visible.value = false
    ElMessage.success('保存成功')
    await load()
  }
}

async function repairInventory() {
  const response = await http.put('/books/inventory/repair')
  if (response.code === 200) {
    ElMessage.success('已按借阅记录重新计算库存')
    await load()
  }
}

onMounted(async () => {
  await load()
  const response = await http.get('/categories')
  categories.value = response.data
})
</script>
