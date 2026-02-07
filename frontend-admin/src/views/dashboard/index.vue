<template>
  <div class="page-container">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="12" :md="6" v-for="item in statCards" :key="item.key">
        <div class="stat-card" :style="{ borderTop: `3px solid ${item.color}` }">
          <div class="stat-content">
            <div>
              <div class="stat-value">{{ stats[item.key] ?? '-' }}</div>
              <div class="stat-label">{{ item.label }}</div>
            </div>
            <el-icon :size="48" :color="item.color" class="stat-icon">
              <component :is="item.icon" />
            </el-icon>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <div class="table-card" style="margin-top: 16px;">
      <div class="table-toolbar">
        <span class="toolbar-title">快捷操作</span>
      </div>
      <el-row :gutter="16">
        <el-col :xs="12" :sm="12" :md="6" v-for="action in quickActions" :key="action.path">
          <div class="quick-action" @click="$router.push(action.path)">
            <el-icon :size="28" :color="action.color"><component :is="action.icon" /></el-icon>
            <span class="action-text">{{ action.label }}</span>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 系统信息 -->
    <div class="table-card" style="margin-top: 16px;">
      <div class="table-toolbar">
        <span class="toolbar-title">系统信息</span>
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="系统名称">健身房会员管理系统</el-descriptions-item>
        <el-descriptions-item label="技术栈">Spring Boot 3 + Vue 3</el-descriptions-item>
        <el-descriptions-item label="前端框架">Vue 3 + Element Plus + Pinia</el-descriptions-item>
        <el-descriptions-item label="后端框架">Spring Boot 3 + MyBatis-Plus</el-descriptions-item>
        <el-descriptions-item label="数据库">MySQL 8.0</el-descriptions-item>
        <el-descriptions-item label="版本">v1.0.0</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStats } from '@/api/dashboard'

const stats = ref({})

const statCards = [
  { key: 'memberCount', label: '会员总数', icon: 'User', color: '#4A6CF7' },
  { key: 'cardTypeCount', label: '卡种数量', icon: 'CreditCard', color: '#13CE66' },
  { key: 'activeCardCount', label: '有效卡数', icon: 'Postcard', color: '#E6A23C' },
  { key: 'totalRevenue', label: '收入总额(元)', icon: 'Money', color: '#F56C6C' }
]

const quickActions = [
  { path: '/member', label: '会员管理', icon: 'User', color: '#4A6CF7' },
  { path: '/card-type', label: '卡种管理', icon: 'CreditCard', color: '#13CE66' },
  { path: '/member-card', label: '开卡管理', icon: 'Postcard', color: '#E6A23C' },
  { path: '/log', label: '操作日志', icon: 'Document', color: '#909399' }
]

const fetchStats = async () => {
  try {
    const res = await getStats()
    stats.value = res.data
  } catch (e) {
    // 统计数据加载失败时保持默认值
  }
}

onMounted(fetchStats)
</script>

<style lang="scss" scoped>
.stat-row {
  margin-bottom: 0;

  .el-col {
    margin-bottom: 16px;
  }
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s ease, transform 0.3s ease;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-icon {
  opacity: 0.6;
}

.quick-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px;
  margin-bottom: 16px;
  border-radius: 8px;
  background: #F9FAFC;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #EBEEF5;

  &:hover {
    background: #EEF2FF;
    border-color: #4A6CF7;
    transform: translateY(-2px);
  }

  .action-text {
    font-size: 14px;
    color: #606266;
    font-weight: 500;
  }
}
</style>
