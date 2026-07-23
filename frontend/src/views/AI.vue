<template>
  <div class="page-card">
    <h2>{{ isAdmin ? 'AI 图书馆运营建议' : 'AI 个性化图书推荐' }}</h2>
    <p class="tip">{{ isAdmin ? '例如：计算机类图书库存不足，如何优化采购？' : '例如：我喜欢科幻和人工智能，请推荐适合借阅的图书。' }}</p>
    <el-input v-model.trim="prompt" type="textarea" :rows="6" :placeholder="isAdmin ? '输入库存或运营问题' : '输入阅读偏好或想借阅的图书类型'" />
    <el-button type="primary" style="margin-top: 16px" :loading="loading" @click="ask">{{ isAdmin ? '获取运营建议' : '获取借阅推荐' }}</el-button>
    <el-card v-if="answer" class="answer"><template #header>AI 回复</template><div class="answer-content">{{ answer }}</div></el-card>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'
const prompt = ref(''); const answer = ref(''); const loading = ref(false)
const isAdmin = computed(() => JSON.parse(localStorage.getItem('library_user') || '{}').role === 'ADMIN')
async function ask() { if (!prompt.value) { ElMessage.warning('请输入咨询内容'); return } loading.value = true; try { const response = await http.post(isAdmin.value ? '/ai/advice' : '/ai/recommend', prompt.value, { headers: { 'Content-Type': 'text/plain' } }); if (response.code !== 200) throw new Error(response.message); answer.value = response.data } catch (error) { ElMessage.error(error.response?.data?.message || error.message || 'AI 咨询失败') } finally { loading.value = false } }
</script>

<style scoped>.tip { color: #64748b; }.answer { margin-top: 20px; }.answer-content { white-space: pre-wrap; line-height: 1.8; }</style>
