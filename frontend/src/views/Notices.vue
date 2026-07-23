<template>
  <div class="page-card">
    <h2>公告管理</h2>
    <div class="toolbar" v-if="isAdmin">
      <el-button type="primary" @click="openDialog">发布公告</el-button>
    </div>
    <el-empty v-if="!loading && rows.length === 0" description="暂无公告" />
    <el-timeline v-else v-loading="loading" style="margin-top: 20px">
      <el-timeline-item v-for="notice in rows" :key="notice.id" :timestamp="notice.publishedAt || notice.createdAt">
        <h3>{{ notice.title }}</h3>
        <p>{{ notice.content }}</p>
      </el-timeline-item>
    </el-timeline>

    <el-dialog v-model="visible" title="发布公告" width="560px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="公告标题" required>
          <el-input v-model.trim="form.title" maxlength="200" />
        </el-form-item>
        <el-form-item label="公告内容" required>
          <el-input v-model.trim="form.content" type="textarea" :rows="6" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="publish">发布</el-button>
      </template>
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
const loading = ref(false)
const saving = ref(false)
const visible = ref(false)
const form = reactive({ title: '', content: '' })

async function load() {
  loading.value = true
  try {
    const response = await http.get('/notices/public')
    if (response.code !== 200) throw new Error(response.message)
    rows.value = response.data
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || '公告加载失败')
  } finally {
    loading.value = false
  }
}

function openDialog() {
  Object.assign(form, { title: '', content: '' })
  visible.value = true
}

async function publish() {
  if (!form.title || !form.content) {
    ElMessage.warning('请填写公告标题和内容')
    return
  }
  saving.value = true
  try {
    const response = await http.post('/notices', { ...form, adminId: user.id, publishStatus: 1 })
    if (response.code !== 200) throw new Error(response.message)
    ElMessage.success('公告发布成功')
    visible.value = false
    await load()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || '公告发布失败')
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>
