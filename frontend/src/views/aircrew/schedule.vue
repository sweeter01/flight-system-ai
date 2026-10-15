<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  listCrewScheduleRequest,
  addCrewScheduleRequest,
  updateCrewScheduleRequest,
  deleteCrewScheduleRequest,
  getCrewScheduleDetailRequest,
  getCrewListForSelectRequest,
  getCrewByPositionRequest
} from "../../api/aircrew.js";

const tableData = ref([]);
const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const isEdit = ref(false);
const selectedRows = ref([]);
const pagination = ref({ current: 1, size: 10, total: 0 });
const searchForm = ref({ flightId: "", pilotId: undefined, assistantId: undefined, status: undefined, dutyType: undefined });
const scheduleForm = ref({ id: null, pilotId: null, assistantId: null, flightId: "", dutyType: null, status: 1 });

const pilotOptions = ref([]);
const assistantOptions = ref([]);
const pilotDetailList = ref([]);
const attendantDetailList = ref([]);
const currentScheduleTitle = ref("");

const statusMap = { 0: '取消', 1: '已排班', 2: '已完成' };
const dutyTypeMap = { 1: '执飞', 2: '备份' };
const genderMap = { 1: '男', 2: '女' };

const formatDateTime = (value) => {
  if (!value) return '';
  const date = new Date(value);
  if (isNaN(date.getTime())) return value;
  const y = date.getFullYear();
  const M = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  const h = String(date.getHours()).padStart(2, '0');
  const m = String(date.getMinutes()).padStart(2, '0');
  const s = String(date.getSeconds()).padStart(2, '0');
  return `${y}-${M}-${d} ${h}:${m}:${s}`;
};

const getCrewScheduleList = () => {
  listCrewScheduleRequest({
    flightId: searchForm.value.flightId || undefined,
    pilotId: searchForm.value.pilotId,
    assistantId: searchForm.value.assistantId,
    status: searchForm.value.status,
    dutyType: searchForm.value.dutyType,
    current: pagination.value.current,
    size: pagination.value.size,
  })
    .then((res) => {
      if (res.code === 200 && res.data) {
        tableData.value = res.data.records || [];
        pagination.value.total = res.data.total || 0;
      } else {
        tableData.value = [];
        pagination.value.total = 0;
      }
    })
    .catch((error) => {
      console.error(error);
    });
};

const loadCrewOptions = () => {
    getCrewByPositionRequest("机长")
    .then((res) => {
      if (res.code === 200 && res.data) {
        pilotOptions.value = Object.entries(res.data).map(([id, name]) => ({ id: Number(id), name }));
      }
    })
    .catch(() => { pilotOptions.value = []; });

    getCrewByPositionRequest("乘务长")
    .then((res) => {
      if (res.code === 200 && res.data) {
        assistantOptions.value = Object.entries(res.data).map(([id, name]) => ({ id: Number(id), name }));
      }
    })
    .catch(() => { assistantOptions.value = []; });
};

const handleSearch = () => {
  pagination.value.current = 1;
  getCrewScheduleList();
};

const resetSearch = () => {
  searchForm.value = { flightId: "", pilotId: undefined, assistantId: undefined, status: undefined, dutyType: undefined };
  pagination.value.current = 1;
  getCrewScheduleList();
};

const handleSizeChange = () => {
  pagination.value.current = 1;
  getCrewScheduleList();
};

const handleCurrentChange = () => {
  getCrewScheduleList();
};

const handleSelectionChange = (rows) => {
  selectedRows.value = rows;
};

const openAddDialog = () => {
  isEdit.value = false;
  scheduleForm.value = { id: null, pilotId: null, assistantId: null, flightId: "", dutyType: null, status: 1 };
  loadCrewOptions();
  dialogVisible.value = true;
};

const openEditDialog = (row) => {
  isEdit.value = true;
  scheduleForm.value = { ...row };
  loadCrewOptions();
  dialogVisible.value = true;
};

const submitSchedule = () => {
  if (!scheduleForm.value.flightId) {
    ElMessage.warning("航班编号不能为空");
    return;
  }
  if (isEdit.value) {
    updateCrewScheduleRequest(scheduleForm.value)
      .then(() => {
        ElMessage.success("修改成功");
        dialogVisible.value = false;
        getCrewScheduleList();
      })
      .catch(() => ElMessage.error("修改失败"));
  } else {
    addCrewScheduleRequest(scheduleForm.value)
      .then(() => {
        ElMessage.success("添加成功");
        dialogVisible.value = false;
        getCrewScheduleList();
      })
      .catch(() => ElMessage.error("添加失败"));
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除日程安排「${row.flightId}」吗？`, "提示", { type: "warning" })
    .then(() => {
      deleteCrewScheduleRequest([row.id])
        .then(() => {
          ElMessage.success("删除成功");
          getCrewScheduleList();
        })
        .catch(() => ElMessage.error("删除失败"));
    })
    .catch(() => {});
};

const handleBatchDelete = () => {
  const ids = selectedRows.value.map(row => row.id);
  ElMessageBox.confirm(`确定要批量删除选中的 ${ids.length} 条日程安排吗？`, "批量删除", { type: "warning" })
    .then(() => {
      deleteCrewScheduleRequest(ids)
        .then(() => {
          ElMessage.success("批量删除成功");
          selectedRows.value = [];
          getCrewScheduleList();
        })
        .catch(() => ElMessage.error("批量删除失败"));
    })
    .catch(() => {});
};

const viewScheduleDetail = (row) => {
  currentScheduleTitle.value = row.flightId;
  getCrewScheduleDetailRequest([row.pilotId, row.assistantId])
    .then((res) => {
      if (res.code === 200 && res.data) {
        const keys = Object.keys(res.data);
        pilotDetailList.value = res.data[keys[0]] || [];
        attendantDetailList.value = res.data[keys[1]] || [];
        detailDialogVisible.value = true;
      } else {
        pilotDetailList.value = [];
        attendantDetailList.value = [];
      }
    })
    .catch(() => {
      pilotDetailList.value = [];
      attendantDetailList.value = [];
    });
};

onMounted(() => {
  getCrewScheduleList();
});
</script>

<template>
  <div class="schedule-wrapper">
    <div class="main-content">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="航班编号">
            <el-input v-model="searchForm.flightId" placeholder="请输入航班编号" clearable />
          </el-form-item>
          <el-form-item label="机长ID">
            <el-input-number v-model="searchForm.pilotId" placeholder="机长ID" :min="1" controls-position="right" clearable style="width: 150px" />
          </el-form-item>
          <el-form-item label="乘务长ID">
            <el-input-number v-model="searchForm.assistantId" placeholder="乘务长ID" :min="1" controls-position="right" clearable style="width: 150px" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
              <el-option label="取消" :value="0" />
              <el-option label="已排班" :value="1" />
              <el-option label="已完成" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="任务类型">
            <el-select v-model="searchForm.dutyType" placeholder="全部" clearable style="width: 130px">
              <el-option label="执飞" :value="1" />
              <el-option label="备份" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
            <el-button type="success" @click="openAddDialog">新增安排</el-button>
            <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">批量删除</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" stripe style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="flightId" label="航班编号" width="130" />
        <el-table-column prop="pilotName" label="机长" width="100" />
        <el-table-column prop="assistantName" label="乘务长" width="100" />
        <el-table-column prop="dutyType" label="任务类型" width="100">
          <template #default="scope">
            {{ dutyTypeMap[scope.row.dutyType] || scope.row.dutyType }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : scope.row.status === 2 ? 'info' : 'warning'" size="small">
              {{ statusMap[scope.row.status] || scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" min-width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="240">
          <template #default="scope">
            <el-button size="small" type="info" @click="viewScheduleDetail(scope.row)">查看详情</el-button>
            <el-button size="small" type="primary" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[5, 10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑日程安排' : '新增日程安排'" width="520px">
      <el-form :model="scheduleForm" label-width="90px">
        <el-form-item label="航班编号">
          <el-input v-model="scheduleForm.flightId" :disabled="isEdit" placeholder="请输入航班编号" />
        </el-form-item>
        <el-form-item label="重选机长">
          <el-select v-model="scheduleForm.pilotId" placeholder="请选择机长" clearable style="width: 100%">
            <el-option v-for="c in pilotOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="重选乘务长">
          <el-select v-model="scheduleForm.assistantId" placeholder="请选择乘务长" clearable style="width: 100%">
            <el-option v-for="c in assistantOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务类型">
          <el-select v-model="scheduleForm.dutyType" placeholder="请选择任务类型" style="width: 100%">
            <el-option label="执飞" :value="1" />
            <el-option label="备份" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="scheduleForm.status" style="width: 100%">
            <el-option label="取消" :value="0" />
            <el-option label="已排班" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSchedule">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" :title="`日程人员详情 - ${currentScheduleTitle}`" width="800px">
      <h4 class="detail-section-title">飞行员大队</h4>
      <el-table :data="pilotDetailList" stripe style="width: 100%; margin-bottom: 20px" max-height="200">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="crewNo" label="机组编号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="70">
          <template #default="scope">
            {{ genderMap[scope.row.gender] || scope.row.gender }}
          </template>
        </el-table-column>
        <el-table-column prop="positionName" label="职务" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
      </el-table>
      <h4 class="detail-section-title">乘务大队</h4>
      <el-table :data="attendantDetailList" stripe style="width: 100%" max-height="200">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="crewNo" label="机组编号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="70">
          <template #default="scope">
            {{ genderMap[scope.row.gender] || scope.row.gender }}
          </template>
        </el-table-column>
        <el-table-column prop="positionName" label="职务" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
      </el-table>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.schedule-wrapper {
  width: 100%;
}

.main-content {
  padding: 20px 30px 0;
}

.search-bar {
  margin-bottom: 16px;
  padding: 16px 20px;
  background: rgba(10, 14, 39, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.12);
  border-radius: 8px;
}

.search-form :deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.8);
}

.search-form :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(0, 212, 255, 0.2);
  box-shadow: none;
}

.search-form :deep(.el-input__inner) {
  color: #fff;
}

.search-form :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4);
}

:deep(.el-dialog) {
  background: #1a1f3a;
  border: 1px solid rgba(0, 212, 255, 0.2);
}

:deep(.el-dialog__title) {
  color: #00d4ff;
}

:deep(.el-dialog .el-form-item__label) {
  color: rgba(255, 255, 255, 0.8);
}

:deep(.el-dialog .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(0, 212, 255, 0.2);
  box-shadow: none;
}

:deep(.el-dialog .el-input__inner) {
  color: #fff;
}

:deep(.el-table) {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(0, 212, 255, 0.12);
  border-radius: 8px;
  --el-table-bg-color: rgba(10, 14, 39, 0.8);
  --el-table-tr-bg-color: rgba(20, 26, 60, 0.6);
  --el-table-header-bg-color: rgba(0, 212, 255, 0.08);
  --el-table-row-hover-bg-color: rgba(0, 212, 255, 0.1);
  --el-table-border-color: rgba(0, 212, 255, 0.1);
  --el-table-text-color: rgba(255, 255, 255, 0.85);
  --el-table-header-text-color: #00d4ff;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
  background: rgba(0, 212, 255, 0.03);
}

.pagination-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding: 12px 0;
}

.pagination-bar :deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
  --el-pagination-button-bg-color: rgba(12, 125, 5, 0.7);
  --el-pagination-hover-color: #00d4ff;
}

.detail-section-title {
  color: #00d4ff;
  font-size: 15px;
  margin: 0 0 10px;
  letter-spacing: 2px;
}
</style>