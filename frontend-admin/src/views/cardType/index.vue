<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :model="queryForm" inline>
        <el-form-item label="卡种名称">
          <el-input v-model="queryForm.name" placeholder="请输入名称" clearable @clear="loadData" />
        </el-form-item>
        <el-form-item label="卡种类型">
          <el-select v-model="queryForm.type" placeholder="全部" clearable @clear="loadData" style="width: 150px;">
            <el-option v-for="t in typeOptions" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable @clear="loadData" style="width: 150px;">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格区域 -->
    <div class="table-card">
      <div class="table-toolbar">
        <span class="toolbar-title">卡种列表</span>
        <el-button type="primary" @click="openDialog(null)"><el-icon><Plus /></el-icon>新增卡种</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%;">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="name" label="卡种名称" width="150" />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="typeTagMap[row.type]" size="small">{{ typeMap[row.type] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="有效天数" width="100" align="center">
          <template #default="{ row }">{{ row.duration ? row.duration + '天' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="totalCount" label="总次数" width="90" align="center">
          <template #default="{ row }">{{ row.totalCount ?? '-' }}</template>
        </el-table-column>
        <el-table-column prop="price" label="价格(元)" width="110" align="right">
          <template #default="{ row }">
            <span style="color: #F56C6C; font-weight: 600;">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-area">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑卡种' : '新增卡种'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" class="dialog-form">
        <el-form-item label="卡种名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入卡种名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%;">
            <el-option v-for="t in typeOptions" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="有效天数" prop="duration">
          <el-input-number v-model="form.duration" :min="1" :max="9999" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="总次数" v-if="form.type === 1">
          <el-input-number v-model="form.totalCount" :min="1" :max="9999" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="价格(元)" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="10" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { getCardTypePage, createCardType, updateCardType, deleteCardType } from '@/api/cardType'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const editingId = ref(null)

const typeOptions = [
  { label: '次卡', value: 1 },
  { label: '月卡', value: 2 },
  { label: '季卡', value: 3 },
  { label: '年卡', value: 4 }
]
const typeMap = { 1: '次卡', 2: '月卡', 3: '季卡', 4: '年卡' }
const typeTagMap = { 1: 'info', 2: 'primary', 3: 'warning', 4: 'success' }

const queryForm = reactive({ name: '', type: null, status: null })

const form = reactive({
  name: '', type: null, duration: 30, totalCount: null, price: 0, status: 1, description: ''
})

const rules = {
  name: [{ required: true, message: '请输入卡种名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  duration: [{ required: true, message: '请输入有效天数', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  const startTime = Date.now()
  try {
    const res = await getCardTypePage({ pageNum: pageNum.value, pageSize: pageSize.value, ...queryForm })
    tableData.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    const elapsed = Date.now() - startTime
    if (elapsed < 300) {
      setTimeout(() => { loading.value = false }, 300 - elapsed)
    } else {
      loading.value = false
    }
  }
}

const resetQuery = () => {
  queryForm.name = ''
  queryForm.type = null
  queryForm.status = null
  pageNum.value = 1
  loadData()
}

const openDialog = (row) => {
  isEdit.value = !!row
  editingId.value = row ? row.id : null
  if (row) {
    Object.assign(form, { name: row.name, type: row.type, duration: row.duration, totalCount: row.totalCount, price: row.price, status: row.status, description: row.description || '' })
  } else {
    Object.assign(form, { name: '', type: null, duration: 30, totalCount: null, price: 0, status: 1, description: '' })
  }
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateCardType(editingId.value, form)
      ElMessage.success('修改成功')
    } else {
      await createCardType(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除卡种「${row.name}」？`, '警告', {
    type: 'warning',
    confirmButtonText: '确认删除',
    cancelButtonText: '取消'
  }).then(async () => {
    await deleteCardType(row.id)
    ElMessage.success('删除成功')
    if (tableData.value.length === 1 && pageNum.value > 1) {
      pageNum.value--
    }
    loadData()
  }).catch(() => {})
}

onMounted(loadData)
</script>
