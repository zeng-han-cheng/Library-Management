<template>
  <div class="page-card">
    <h2>分类管理</h2>
    <div class="toolbar">
      <el-button type="primary" @click="openDialog()">新增分类</el-button>
    </div>
    <el-table v-loading="loading" :data="rows" style="margin-top: 16px">
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button link @click="openDialog(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="form.id ? '编辑分类' : '新增分类'" width="420px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称" required>
          <el-input v-model.trim="form.name" maxlength="80" />
        </el-form-item>
        <el-form-item label="分类描述">
          <el-input v-model.trim="form.description" type="textarea" :rows="3" maxlength="255" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const rows = ref([])
const loading = ref(false)
const saving = ref(false)
const visible = ref(false)
const form = reactive({ id: null, name: '', description: '' })

async function load() {
  loading.value = true
  try {
    const response = await http.get('/categories')
    if (response.code !== 200) throw new Error(response.message)
    rows.value = response.data
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || '分类加载失败')
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  Object.assign(form, row ? { ...row } : { id: null, name: '', description: '' })
  visible.value = true
}

async function save() {
  if (!form.name) {
    ElMessage.warning('请输入分类名称')
    return
  }
  saving.value = true
  try {
    const response = form.id ? await http.put('/categories', form) : await http.post('/categories', form)
    if (response.code !== 200) throw new Error(response.message)
    ElMessage.success('保存成功')
    visible.value = false
    await load()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>
