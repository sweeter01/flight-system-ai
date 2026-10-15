<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  listCrewGroupRequest,
  addCrewGroupRequest,
  updateCrewGroupRequest,
  deleteCrewGroupRequest,
  getCityListRequest,
  getCrewGroupMapRequest,
  getGroupMemberDetailRequest,
  getCrewByPositionRequest
} from "../../api/aircrew.js";

const tableData = ref([]);
const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const isEdit = ref(false);
const pagination = ref({ current: 1, size: 10, total: 0 });
const searchForm = ref({ groupName: "", groupCode: "", status: undefined, leaderId: undefined, baseCityId: undefined });
const groupForm = ref({ id: null, groupCode: "", groupName: "", leaderId: null, baseCityId: null, status: 1 });

const cityOptions = ref([]);
const crewOptions = ref([]);
const detailCrewList = ref([]);
const currentGroupName = ref("");
const currentLeaderId = ref(null);

const statusMap = { 1: '正常', 0: '休息中' };

const positionTypeMap = {
  PILOT: '机长',
  COPILOT: '副机长',
  FLIGHT_ATTENDANT: '乘务员',
  FLIGHT_ATTENDANT_LEADER: '乘务长',
  FLIGHT_ENGINEER: '飞行工程师',
  OTHER: '其他'
};

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

const loadCityOptions = () => {
  getCityListRequest()
    .then((res) => {
      if (res.code === 200 && res.data) {
        cityOptions.value = Object.entries(res.data).map(([id, name]) => ({ id: Number(id), cityName: name }));
      }
    })
    .catch(() => {});
};

const getCityName = (cityId) => {
  const city = cityOptions.value.find(c => c.id === cityId);
  return city ? city.cityName : cityId;
};

const getCrewGroupList = () => {
  listCrewGroupRequest({
    groupName: searchForm.value.groupName || undefined,
    groupCode: searchForm.value.groupCode || undefined,
    status: searchForm.value.status,
    leaderId: searchForm.value.leaderId,
    baseCityId: searchForm.value.baseCityId,
    current: pagination.value.current,
    size: pagination.value.size,
  })
    .then((res) => {
      if (res.code === 200 && res.data) {
        tableData.value = res.data.records || res.data || [];
        pagination.value.total = res.data.total || res.data.length || 0;
      } else {
        tableData.value = [];
        pagination.value.total = 0;
      }
    })
    .catch((error) => {
      console.error(error);
    });
};

const handleSearch = () => {
  pagination.value.current = 1;
  getCrewGroupList();
};

const resetSearch = () => {
  searchForm.value = { groupName: "", groupCode: "", status: undefined, leaderId: undefined, baseCityId: undefined };
  pagination.value.current = 1;
  getCrewGroupList();
};

const handleSizeChange = () => {
  pagination.value.current = 1;
  getCrewGroupList();
};

const handleCurrentChange = () => {
  getCrewGroupList();
};

const openAddDialog = () => {
  isEdit.value = false;
  groupForm.value = { id: null, groupCode: "", groupName: "", leaderId: null, baseCityId: null, status: 1 };
   addCrewGroupRequest(groupForm.value)
    .then((res) => {
      if (res.code === 200 && res.data) {
        crewOptions.value = Object.entries(res.data).map(([id, name]) => ({ id: Number(id), name }));
      }
    })
    .catch(() => { crewOptions.value = []; });
  dialogVisible.value = true;
};

const openEditDialog = (row) => {
  isEdit.value = true;
  groupForm.value = { ...row };
  getCrewGroupMapRequest(row.id)
    .then((res) => {
      if (res.code === 200 && res.data) {
        crewOptions.value = Object.entries(res.data).map(([id, name]) => ({ id: Number(id), name }));
      }
    })
    .catch(() => { crewOptions.value = []; });
  dialogVisible.value = true;
};

const submitGroup = () => {
  if (!groupForm.value.groupName || !groupForm.value.groupCode) {
    ElMessage.warning("小队名称和编码不能为空");
    return;
  }
  if (isEdit.value) {
    updateCrewGroupRequest(groupForm.value)
      .then(() => {
          ElMessage.success("修改成功");
          dialogVisible.value = false;
          getCrewGroupList();
      })
      .catch(() => ElMessage.error("修改失败"));
  } else {
    addCrewGroupRequest(groupForm.value)
      .then(() => {
          ElMessage.success("添加成功");
          dialogVisible.value = false;
          getCrewGroupList();
      })
      .catch(() => ElMessage.error("添加失败"));
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除机组小队「${row.groupName}」吗？`, "提示", { type: "warning" })
    .then(() => {
      deleteCrewGroupRequest(String(row.id))
        .then(() => {
            ElMessage.success("删除成功");
            getCrewGroupList();
        })
        .catch(() => ElMessage.error("删除失败"));
    })
    .catch(() => {});
};

const viewGroupDetail = (row) => {
  currentGroupName.value = row.groupName;
  currentLeaderId.value = row.leaderId;
  getGroupMemberDetailRequest(row.id)
    .then((res) => {
      if (res.code === 200 && res.data) {
        detailCrewList.value = res.data || [];
        detailDialogVisible.value = true;
      } else {
        detailCrewList.value = [];
      }
    })
    .catch(() => {
      detailCrewList.value = [];
    });
};

onMounted(() => {
  getCrewGroupList();
  loadCityOptions();
});
</script>

<template>
  <div class="group-wrapper">
    <div class="main-content">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="分组名称">
            <el-input v-model="searchForm.groupName" placeholder="请输入分组名称" clearable />
          </el-form-item>
          <el-form-item label="分组编码">
            <el-input v-model="searchForm.groupCode" placeholder="请输入分组编码" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
              <el-option label="正常" :value="1" />
              <el-option label="休息中" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item label="基地城市">
            <el-select v-model="searchForm.baseCityId" placeholder="全部" clearable style="width: 150px">
              <el-option v-for="c in cityOptions" :key="c.id" :label="c.cityName" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
            <el-button type="success" @click="openAddDialog">新增分组</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="groupCode" label="分组编码" width="130" />
        <el-table-column prop="groupName" label="分组名称" width="150" />
        <el-table-column prop="leaderName" label="组长" width="100" />
        <el-table-column prop="baseCityName" label="基地城市" width="120">
          <template #default="scope">
            {{ scope.row.baseCityName || getCityName(scope.row.baseCityId) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ statusMap[scope.row.status] || scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="240">
          <template #default="scope">
            <el-button size="small" type="info" @click="viewGroupDetail(scope.row)">查看成员</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑机组分组' : '新增机组分组'" width="520px">
      <el-form :model="groupForm" label-width="90px">
        <el-form-item label="分组编码">
          <el-input v-model="groupForm.groupCode" placeholder="请输入分组编码" />
        </el-form-item>
        <el-form-item label="小队名称">
          <el-input v-model="groupForm.groupName" placeholder="请输入小队名称" />
        </el-form-item>
        <el-form-item label="组长">
          <el-select v-model="groupForm.leaderId" placeholder="请选择组长" clearable style="width: 100%">
            <el-option v-for="c in crewOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="基地城市">
          <el-select v-model="groupForm.baseCityId" placeholder="请选择基地城市" clearable style="width: 100%">
            <el-option v-for="c in cityOptions" :key="c.id" :label="c.cityName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="groupForm.status" style="width: 100%">
            <el-option label="正常" :value="1" />
            <el-option label="休息中" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitGroup">确定</el-button>
      </template>
    </el-dialog>


<!-- 查看分组成员详情信息   -->
    <el-dialog v-model="detailDialogVisible" :title="`小队成员 - ${currentGroupName}`" width="700px">
      <el-table :data="detailCrewList" stripe style="width: 100%" max-height="400"
        :row-class-name="({ row }) => row.id === currentLeaderId ? 'leader-row' : ''">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="crewNo" label="机组编号" width="120" />
        <el-table-column prop="name" label="姓名" width="100">
          <template #default="scope">
            {{ scope.row.name }}{{ scope.row.id === currentLeaderId ? ' (组长)' : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="70">
          <template #default="scope">
            {{ scope.row.gender === 1 ? '男' : scope.row.gender === 2 ? '女' : scope.row.gender }}
          </template>
        </el-table-column>
        <el-table-column prop="positionType" label="职务" width="100">
          <template #default="scope">
            {{ positionTypeMap[scope.row.positionType] || scope.row.positionType }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : scope.row.status === 2 ? 'warning' : 'danger'" size="small">
              {{ { 1: '在岗', 0: '离岗', 2: '休假' }[scope.row.status] || scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.group-wrapper {
  width: 100%;
}

:deep(.el-table .leader-row td) {
  background-color: rgba(0, 212, 255, 0.15) !important;
  font-weight: 600;
  color: #00d4ff;
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
</style>