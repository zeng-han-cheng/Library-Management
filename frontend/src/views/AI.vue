<template>
  <div class="ai-page">
    <div class="ai-heading">
      <div>
        <div class="eyebrow">SMART LIBRARY ASSISTANT</div>
        <h2>{{ isAdmin ? 'AI 运营建议' : 'AI 图书推荐' }}</h2>
        <p>{{ isAdmin ? '基于馆藏、借阅和读者数据，生成可执行的运营决策建议。' : '告诉我你的阅读兴趣，发现下一本值得借阅的书。' }}</p>
      </div>
      <div class="ai-orb">✦</div>
    </div>

    <div class="ai-layout">
      <div class="page-card ai-form-card">
        <div class="form-label"><span class="label-icon">?</span>{{ isAdmin ? '你想分析什么' : '你的阅读需求' }}</div>
        <p class="tip">{{ isAdmin ? '系统会自动读取最新库存、借阅、逾期、分类和热门图书数据。' : '描述喜欢的题材、难度、作者或阅读目的，推荐会更贴合。' }}</p>
        <div v-if="isAdmin" class="quick-prompts">
          <button v-for="item in adminPrompts" :key="item" type="button" @click="prompt = item">{{ item }}</button>
        </div>
        <el-input
          v-model="prompt"
          type="textarea"
          :rows="8"
          :placeholder="isAdmin ? '例如：分析当前库存和借阅情况，给出本周采购与运营优先级。' : '例如：我喜欢科幻和人工智能，希望推荐适合周末阅读的书。'"
          maxlength="1000"
          show-word-limit
          @keydown.ctrl.enter.prevent="ask"
        />
        <div class="form-footer">
          <span>Ctrl + Enter 快速生成</span>
          <el-button type="primary" :loading="loading" @click="ask">
            {{ isAdmin ? '生成运营建议' : '获取图书推荐' }} <span class="button-arrow">→</span>
          </el-button>
        </div>
      </div>

      <div class="page-card ai-answer-card" :class="{ empty: !answer }">
        <template v-if="answer">
          <div class="answer-head">
            <div><span class="eyebrow">AI RESPONSE</span><h3>智能助手回复</h3></div>
            <span class="answer-badge">已生成</span>
          </div>
          <div class="answer-content">{{ answer }}</div>
          <el-button class="copy-button" link @click="copyAnswer">复制结果</el-button>
        </template>
        <template v-else>
          <div class="empty-orb">✦</div>
          <h3>等待你的问题</h3>
          <p>提交后，DeepSeek 会在这里整理清晰、可执行的回答。</p>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const prompt = ref('')
const answer = ref('')
const loading = ref(false)
const user = JSON.parse(localStorage.getItem('library_user') || '{}')
const isAdmin = computed(() => user.role === 'ADMIN')
const adminPrompts = [
  '分析当前库存和借阅情况，给出本周采购与运营优先级。',
  '找出库存风险和逾期管理问题，并给出可量化的改进措施。',
  '结合热门图书和分类数据，给出下一轮馆藏优化建议。'
]

async function ask() {
  if (loading.value) return
  loading.value = true
  try {
    const response = await http.post(isAdmin.value ? '/ai/advice' : '/ai/recommend', { prompt: prompt.value.trim() })
    if (response.code !== 200) throw new Error(response.message)
    answer.value = response.data || 'AI 没有返回可展示的内容。'
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'AI 咨询失败')
  } finally {
    loading.value = false
  }
}

async function copyAnswer() {
  try {
    await navigator.clipboard.writeText(answer.value)
    ElMessage.success('已复制到剪贴板')
  } catch {
    ElMessage.warning('复制失败，请手动选择文本')
  }
}
</script>

<style scoped>
.ai-heading { display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; }
.ai-heading h2 { margin: 8px 0; }
.ai-heading p { margin: 0; color: #7b879c; }
.ai-orb, .empty-orb { display: grid; width: 76px; height: 76px; place-items: center; color: #fff; border-radius: 25px; background: linear-gradient(135deg, #4f46e5, #a78bfa); box-shadow: 0 15px 28px rgba(79, 70, 229, .25); font-size: 30px; }
.ai-layout { display: grid; grid-template-columns: 1.05fr .95fr; gap: 20px; }
.ai-form-card, .ai-answer-card { min-height: 420px; }
.form-label { display: flex; align-items: center; gap: 9px; color: #334155; font-size: 15px; font-weight: 800; }
.label-icon { display: grid; width: 28px; height: 28px; place-items: center; color: #6366f1; border-radius: 9px; background: #eef2ff; }
.tip { margin: 14px 0 16px; color: #8490a5; font-size: 13px; line-height: 1.6; }
.quick-prompts { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 14px; }
.quick-prompts button { padding: 7px 10px; color: #5b5bd6; border: 1px solid #e0e7ff; border-radius: 9px; background: #f8f9ff; cursor: pointer; font-size: 12px; text-align: left; }
.quick-prompts button:hover { border-color: #a5b4fc; background: #eef2ff; }
.form-footer { display: flex; justify-content: space-between; align-items: center; gap: 12px; margin-top: 17px; color: #9aa5b8; font-size: 11px; }
.button-arrow { margin-left: 12px; font-size: 16px; }
.ai-answer-card { display: flex; flex-direction: column; }
.ai-answer-card.empty { align-items: center; justify-content: center; text-align: center; }
.empty-orb { width: 62px; height: 62px; margin-bottom: 18px; border-radius: 20px; font-size: 25px; }
.ai-answer-card.empty h3 { margin-bottom: 8px; }
.ai-answer-card.empty p { max-width: 260px; margin: 0; color: #96a1b4; font-size: 13px; line-height: 1.7; }
.answer-head { display: flex; justify-content: space-between; align-items: flex-start; }
.answer-head h3 { margin: 8px 0 0; }
.answer-badge { padding: 7px 10px; color: #4f46e5; border-radius: 20px; background: #eef2ff; font-size: 11px; font-weight: 700; }
.answer-content { overflow: auto; max-height: 470px; margin-top: 24px; padding: 18px; color: #475569; border: 1px solid #edf0f7; border-radius: 14px; background: #fafbff; white-space: pre-wrap; line-height: 1.9; }
.copy-button { align-self: flex-end; margin-top: 10px; color: #6366f1; }
@media (max-width: 800px) { .ai-layout { grid-template-columns: 1fr; } .ai-orb { width: 58px; height: 58px; } .form-footer { align-items: flex-start; flex-direction: column; } }
</style>
