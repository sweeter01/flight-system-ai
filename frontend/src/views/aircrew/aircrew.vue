
<template>
  <div class="aircrew-wrapper">


    <div class="main-content">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="姓名">
            <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="searchForm.gender" placeholder="全部" clearable style="width: 120px">
              <el-option label="男" :value="1" />
              <el-option label="女" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item label="机组编号">
            <el-input v-model="searchForm.crewNo" placeholder="请输入机组编号" clearable />
          </el-form-item>
          <el-form-item label="职务类型">
            <el-select v-model="searchForm.positionType" placeholder="全部" clearable style="width: 130px">
              <el-option v-for="(label, key) in positionTypeMap" :key="key" :label="label" :value="key" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属小队">
            <el-select v-model="searchForm.groupId" placeholder="全部" clearable style="width: 150px">
              <el-option v-for="g in groupOptions" :key="g.id" :label="g.groupName" :value="g.id" />
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
            <el-button type="success" @click="openAddDialog">新增人员</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="crewNo" label="机组编号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="70">
          <template #default="scope">
            {{ genderMap[scope.row.gender] || scope.row.gender }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="positionName" label="职务" width="100">
          <template #default="scope">
            {{ positionTypeMap[scope.row.positionType] || scope.row.positionName || scope.row.positionType }}
          </template>
        </el-table-column>
        <el-table-column prop="groupId" label="所属小队" width="120">
          <template #default="scope">
            {{ scope.row.crewGroup?.groupName || getGroupName(scope.row.groupId) }}
          </template>
        </el-table-column>
        <el-table-column prop="baseCityId" label="基地城市" width="100">
          <template #default="scope">
            {{ scope.row.city?.cityName || getCityName(scope.row.baseCityId) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : scope.row.status === 2 ? 'warning' : 'danger'" size="small">
              {{ statusMap[scope.row.status] || scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="flightHours" label="飞行时长(h)" width="110" />
        <el-table-column prop="createTime" label="创建时间" min-width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="scope">
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑机组人员' : '新增机组人员'" width="600px">
      <el-form :model="crewForm" label-width="90px">
        <el-form-item label="机组编号">
          <el-input v-model="crewForm.crewNo" placeholder="请输入机组编号" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="crewForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="crewForm.gender" style="width: 100%">
            <el-option label="男" :value="1" />
            <el-option label="女" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="crewForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="职务类型">
          <el-select v-model="crewForm.positionType" placeholder="请选择职务类型" style="width: 100%">
            <el-option v-for="(label, key) in positionTypeMap" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属小队">
          <el-select v-model="crewForm.groupId" placeholder="请选择所属小队" clearable style="width: 100%">
            <el-option v-for="g in groupOptions" :key="g.id" :label="g.groupName" :value="g.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="基地城市">
          <el-select v-model="crewForm.baseCityId" placeholder="请选择基地城市" clearable style="width: 100%">
            <el-option v-for="c in cityOptions" :key="c.id" :label="c.cityName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="crewForm.status" style="width: 100%">
            <el-option label="在岗" :value="1" />
            <el-option label="离岗" :value="0" />
            <el-option label="休假" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="执照编号">
          <el-input v-model="crewForm.licenseNo" placeholder="请输入执照编号" />
        </el-form-item>
        <el-form-item label="飞行时长">
          <el-input-number v-model="crewForm.flightHours" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="入职日期">
          <el-date-picker v-model="crewForm.hireDate" type="date" placeholder="选择入职日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCrew">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { listCrewRequest, addCrewRequest, updateCrewRequest, deleteCrewRequest, listCrewGroupRequest, getCityListRequest } from "../../api/aircrew.js";

const tableData = ref([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const pagination = ref({ current: 1, size: 10, total: 0 });
const searchForm = ref({ name: "", gender: undefined, crewNo: "", positionType: undefined, groupId: undefined, baseCityId: undefined });
const crewForm = ref({ id: null, crewNo: "", name: "", gender: 1, phone: "", positionType: "CAPTAIN", groupId: null, baseCityId: null, status: 1, licenseNo: "", flightHours: 0, hireDate: "" });

const groupOptions = ref([]);
const cityOptions = ref([]);

const positionTypeMap = { CAPTAIN: '机长', FIRST_OFFICER: '副驾驶', PURSER: '乘务长', FLIGHT_ATTENDANT: '乘务员', ENGINEER: '机械师' };
const genderMap = { 1: '男', 0: '女' };
const statusMap = { 1: '在岗', 0: '离岗', 2: '休假' };

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

const loadGroupOptions = () => {
  listCrewGroupRequest({ current: 1, size: 1000 })
      .then((res) => {
        if (res.code === 200 && res.data) {
          //res.code这是一个误报，实际是200，因为在request.js中返回了res.data,不需要res.data.code了
          groupOptions.value = res.data.records || res.data || [];
        }
      })
      .catch(() => {});
};

const loadCityOptions = () => {
  getCityListRequest()
      .then((res) => {
        if (res.code === 200 && res.data) {
          cityOptions.value = res.data || [];
        }
      })
      .catch(() => {});
};

const getGroupName = (groupId) => {
  const group = groupOptions.value.find(g => g.id === groupId);
  return group ? group.groupName : groupId;
};

const getCityName = (cityId) => {
  const city = cityOptions.value.find(c => c.id === cityId);
  return city ? city.cityName : cityId;
};

const getCrewList = () => {
  listCrewRequest({
    name: searchForm.value.name || undefined,
    gender: searchForm.value.gender,
    crewNo: searchForm.value.crewNo || undefined,
    positionType: searchForm.value.positionType || undefined,
    groupId: searchForm.value.groupId,
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
  getCrewList();
};

const resetSearch = () => {
  searchForm.value = { name: "", gender: undefined, crewNo: "", positionType: undefined, groupId: undefined, baseCityId: undefined };
  pagination.value.current = 1;
  getCrewList();
};

const handleSizeChange = () => {
  pagination.value.current = 1;
  getCrewList();
};

const handleCurrentChange = () => {
  getCrewList();
};

const openAddDialog = () => {
  isEdit.value = false;
  crewForm.value = { id: null, crewNo: "", name: "", gender: 1, phone: "", positionType: "CAPTAIN", groupId: null, baseCityId: null, status: 1, licenseNo: "", flightHours: 0, hireDate: "" };
  dialogVisible.value = true;
};

const openEditDialog = (row) => {
  isEdit.value = true;
  crewForm.value = { ...row };
  dialogVisible.value = true;
};

const submitCrew = () => {
  if (!crewForm.value.name || !crewForm.value.crewNo) {
    ElMessage.warning("机组编号和姓名不能为空");
    return;
  }
  if (isEdit.value) {
    updateCrewRequest(crewForm.value)
        .then(() => {
          ElMessage.success("修改成功");
          dialogVisible.value = false;
          getCrewList();
        })
        .catch(() => ElMessage.error("修改失败"));
  } else {
    addCrewRequest(crewForm.value)
        .then(() => {
          ElMessage.success("添加成功");
          dialogVisible.value = false;
          getCrewList();
        })
        .catch(() => ElMessage.error("添加失败"));
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除机组人员「${row.name}」吗？`, "提示", { type: "warning" })
      .then(() => {
        deleteCrewRequest([row.id])
            .then(() => {
              ElMessage.success("删除成功");
              getCrewList();
            })
            .catch(() => ElMessage.error("删除失败"));
      })
      .catch(() => {});
};

onMounted(() => {
  getCrewList();
  loadGroupOptions();
  loadCityOptions();
});
</script>

<style scoped>
.aircrew-wrapper {
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
</style>