<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :model="queryForm" inline>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable @clear="loadData" style="width: 150px;">
            <el-option label="有效" :value="1" />
            <el-option label="已过期" :value="2" />
            <el-option label="已停用" :value="3" />
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
        <span class="toolbar-title">开卡记录</span>
        <el-button type="primary" @click="openDialog(null)"><el-icon><Plus /></el-icon>新增开卡</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%;">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="memberName" label="会员姓名" width="120" />
        <el-table-column prop="memberPhone" label="手机号" width="130" />
        <el-table-column prop="cardTypeName" label="卡种" width="130" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="remainingCount" label="剩余次数" width="100" align="center">
          <template #default="{ row }">{{ row.remainingCount ?? '-' }}</template>
        </el-table-column>
        <el-table-column prop="amountPaid" label="实付金额" width="110" align="right">
          <template #default="{ row }">
            <span style="color: #F56C6C; font-weight: 600;">¥{{ row.amountPaid }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagMap[row.status]" size="small">{{ statusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑开卡' : '新增开卡'" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" class="dialog-form">
        <el-form-item label="会员" prop="memberId">
          <el-select v-model="form.memberId" filterable placeholder="请搜索选择会员" style="width: 100%;">
            <el-option v-for="m in memberList" :key="m.id" :label="`${m.name} (${m.phone})`" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="卡种" prop="cardTypeId">
          <el-select v-model="form.cardTypeId" placeholder="请选择卡种" style="width: 100%;" @change="onCardTypeChange">
            <el-option v-for="ct in cardTypeList" :key="ct.id" :label="`${ct.name} (¥${ct.price})`" :value="ct.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="form.startDate" type="date" placeholder="选择开始日期" value-format="YYYY-MM-DD" :disabled-date="(date) => {
            const today = new Date()
            today.setHours(0, 0, 0, 0)
            return date < today
          }" style="width: 100%;" @change="onStartDateChange" />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="form.endDate" type="date" placeholder="选择结束日期" value-format="YYYY-MM-DD" :disabled-date="(date) => {
            if (form.startDate) {
              const startDate = new Date(form.startDate)
              startDate.setHours(0, 0, 0, 0)
              return date < startDate
            }
            const today = new Date()
            today.setHours(0, 0, 0, 0)
            return date < today
          }" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="剩余次数" v-if="selectedCardType && selectedCardType.type === 1">
          <el-input-number v-model="form.remainingCount" :min="0" :max="9999" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="实付金额" prop="amountPaid">
          <el-input-number v-model="form.amountPaid" :min="0" :precision="2" :step="10" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%;">
            <el-option label="有效" :value="1" />
            <el-option label="已过期" :value="2" />
            <el-option label="已停用" :value="3" />
          </el-select>
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
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { getMemberCardPage, createMemberCard, updateMemberCard, deleteMemberCard } from '@/api/memberCard'
import { getMemberPage } from '@/api/member'
import { getCardTypeList } from '@/api/cardType'
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
const memberList = ref([])
const cardTypeList = ref([])

const statusMap = { 1: '有效', 2: '已过期', 3: '已停用' }
const statusTagMap = { 1: 'success', 2: 'info', 3: 'danger' }

const queryForm = reactive({ status: null })

const form = reactive({
  memberId: null, cardTypeId: null, startDate: '', endDate: '', remainingCount: null, amountPaid: 0, status: 1
})

const validateStartDate = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请选择开始日期'))
  } else {
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const selectedDate = new Date(value)
    selectedDate.setHours(0, 0, 0, 0)
    if (selectedDate < today) {
      callback(new Error('开始日期不能小于当前日期'))
    } else {
      callback()
    }
  }
}

const validateEndDate = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请选择结束日期'))
  } else if (form.startDate && value <= form.startDate) {
    callback(new Error('结束日期必须大于开始日期'))
  } else {
    callback()
  }
}

const rules = {
  memberId: [{ required: true, message: '请选择会员', trigger: 'change' }],
  cardTypeId: [{ required: true, message: '请选择卡种', trigger: 'change' }],
  startDate: [{ required: true, validator: validateStartDate, trigger: 'change' }],
  endDate: [{ required: true, validator: validateEndDate, trigger: 'change' }],
  amountPaid: [{ required: true, message: '请输入实付金额', trigger: 'blur' }]
}

const selectedCardType = computed(() => {
  return cardTypeList.value.find(ct => ct.id === form.cardTypeId)
})

const onCardTypeChange = (val) => {
  const ct = cardTypeList.value.find(c => c.id === val)
  if (ct) {
    form.amountPaid = ct.price
    if (ct.type === 1) {
      form.remainingCount = ct.totalCount
    } else {
      form.remainingCount = null
    }
    // 如果开始日期为空，自动设置为当前日期
    if (!form.startDate) {
      const today = new Date()
      form.startDate = today.toISOString().split('T')[0]
    }
    // 自动计算结束日期
    if (form.startDate && ct.duration) {
      const start = new Date(form.startDate)
      start.setDate(start.getDate() + ct.duration)
      form.endDate = start.toISOString().split('T')[0]
    }
  }
}

const onStartDateChange = () => {
  // 开始日期变化时，重新验证结束日期
  if (form.endDate) {
    formRef.value?.validateField('endDate')
  }
  // 如果已经选择了卡种，重新计算结束日期
  const ct = cardTypeList.value.find(c => c.id === form.cardTypeId)
  if (ct && ct.duration) {
    const start = new Date(form.startDate)
    start.setDate(start.getDate() + ct.duration)
    form.endDate = start.toISOString().split('T')[0]
  }
}

const loadData = async () => {
  loading.value = true
  const startTime = Date.now()
  try {
    const res = await getMemberCardPage({ pageNum: pageNum.value, pageSize: pageSize.value, ...queryForm })
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

const loadOptions = async () => {
  try {
    const [memberRes, cardTypeRes] = await Promise.all([
      getMemberPage({ pageNum: 1, pageSize: 500 }),
      getCardTypeList()
    ])
    memberList.value = memberRes.data.records
    cardTypeList.value = cardTypeRes.data
  } catch (e) {
    // 选项加载失败
  }
}

const resetQuery = () => {
  queryForm.status = null
  pageNum.value = 1
  loadData()
}

const openDialog = (row) => {
  isEdit.value = !!row
  editingId.value = row ? row.id : null
  if (row) {
    Object.assign(form, {
      memberId: row.memberId, cardTypeId: row.cardTypeId, startDate: row.startDate,
      endDate: row.endDate, remainingCount: row.remainingCount, amountPaid: row.amountPaid, status: row.status
    })
  } else {
    Object.assign(form, { memberId: null, cardTypeId: null, startDate: '', endDate: '', remainingCount: null, amountPaid: 0, status: 1 })
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
      await updateMemberCard(editingId.value, form)
      ElMessage.success('修改成功')
    } else {
      await createMemberCard(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该开卡记录？', '警告', {
    type: 'warning',
    confirmButtonText: '确认删除',
    cancelButtonText: '取消'
  }).then(async () => {
    await deleteMemberCard(row.id)
    ElMessage.success('删除成功')
    if (tableData.value.length === 1 && pageNum.value > 1) {
      pageNum.value--
    }
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadData()
  loadOptions()
})
</script>
