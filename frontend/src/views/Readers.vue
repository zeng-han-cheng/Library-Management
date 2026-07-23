<template>
  <div class="page-card">
    <h2>读者管理</h2>
    <div class="toolbar">
      <el-input v-model.trim="keyword" placeholder="证号/姓名/账号" style="width: 240px" @keyup.enter="load" />
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="openDialog">新增读者</el-button>
    </div>
    <el-table v-loading="loading" :data="rows">
      <el-table-column prop="readerNo" label="证号" />
      <el-table-column prop="username" label="账号" />
      <el-table-column prop="realName" label="姓名" />
      <el-table-column prop="phone" label="电话" />
      <el-table-column label="状态"><template #default="scope">{{ scope.row.status === 1 ? '启用' : '禁用' }}</template></el-table-column>
      <el-table-column label="操作" width="100"><template #default="scope"><el-button link @click="openDialog(scope.row)">编辑</el-button></template></el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" layout="prev, pager, next" :total="total" @current-change="load" />

    <el-dialog v-model="visible" :title="form.id ? '编辑读者' : '新增读者'" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="读者证号" required><el-input v-model.trim="form.readerNo" /></el-form-item>
        <el-form-item label="登录账号" required><el-input v-model.trim="form.username" /></el-form-item>
        <el-form-item :label="form.id ? '新密码' : '登录密码'" :required="!form.id"><el-input v-model="form.password" type="password" show-password placeholder="编辑时留空则不修改" /></el-form-item>
        <el-form-item label="姓名" required><el-input v-model.trim="form.realName" /></el-form-item>
        <el-form-item label="电话"><el-input v-model.trim="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model.trim="form.email" /></el-form-item>
        <el-form-item label="最大借阅"><el-input-number v-model="form.maxBorrow" :min="1" :max="20" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="visible = false">取消</el-button><el-button type="primary" :loading="saving" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const rows = ref([]); const total = ref(0); const page = ref(1); const keyword = ref('')
const loading = ref(false); const saving = ref(false); const visible = ref(false)
const form = reactive({})

async function load() {
  loading.value = true
  try {
    const response = await http.get('/readers', { params: { page: page.value, size: 10, keyword: keyword.value } })
    if (response.code !== 200) throw new Error(response.message)
    rows.value = response.data.records; total.value = response.data.total
  } catch (error) { ElMessage.error(error.response?.data?.message || error.message || '读者加载失败') } finally { loading.value = false }
}
function openDialog(row) { Object.assign(form, row ? { ...row, password: '' } : { id: null, readerNo: '', username: '', password: '', realName: '', phone: '', email: '', maxBorrow: 5, status: 1 }); visible.value = true }
async function save() {
  if (!form.readerNo || !form.username || !form.realName || (!form.id && !form.password)) { ElMessage.warning('请填写必填项'); return }
  saving.value = true
  try { const response = form.id ? await http.put('/readers', form) : await http.post('/readers', form); if (response.code !== 200) throw new Error(response.message); ElMessage.success('保存成功'); visible.value = false; await load() } catch (error) { ElMessage.error(error.response?.data?.message || error.message || '保存失败') } finally { saving.value = false }
}
onMounted(load)
</script>
