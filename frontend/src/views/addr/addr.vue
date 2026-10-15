<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getCityListRequest, addCityRequest, updateCityRequest, deleteCityRequest } from "../../api/addr.js";

const tableData = ref([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const pagination = ref({ current: 1, size: 10, total: 0 });
const searchForm = ref({ cityCode: "", cityName: "" });
const cityForm = ref({ id: null, cityCode: "", cityName: "", airportName: "", status: 1 });

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

const getCityList = () => {
  getCityListRequest({
    cityCode: searchForm.value.cityCode || undefined,
    cityName: searchForm.value.cityName || undefined,
    current: pagination.value.current,
    size: pagination.value.size,
  })
    .then((res) => {
      if (res.data.code === 200 && res.data) {
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

const handleSearch = () => {
  pagination.value.current = 1;
  getCityList();
};

const resetSearch = () => {
  searchForm.value = { cityCode: "", cityName: "" };
  pagination.value.current = 1;
  getCityList();
};

const handleSizeChange = () => {
  pagination.value.current = 1;
  getCityList();
};

const handleCurrentChange = () => {
  getCityList();
};

const openAddDialog = () => {
  isEdit.value = false;
  cityForm.value = { id: null, cityCode: "", cityName: "", airportName: "", status: 1 };
  dialogVisible.value = true;
};

const openEditDialog = (row) => {
  isEdit.value = true;
  cityForm.value = { ...row };
  dialogVisible.value = true;
};

const submitCity = () => {
  if (!cityForm.value.cityCode || !cityForm.value.cityName) {
    ElMessage.warning("城市编码和城市名称不能为空");
    return;
  }
  if (isEdit.value) {
    updateCityRequest(cityForm.value)
      .then((res) => {
        if (res.data.code === 200) {
          ElMessage.success("修改成功");
          dialogVisible.value = false;
          getCityList();
        } else {
          ElMessage.error(res.data.message || "修改失败");
        }
      })
      .catch(() => ElMessage.error("修改失败"));
  } else {
    addCityRequest(cityForm.value)
      .then((res) => {
        if (res.data.code === 200) {
          ElMessage.success("添加成功");
          dialogVisible.value = false;
          getCityList();
        } else {
          ElMessage.error(res.data.message || "添加失败");
        }
      })
      .catch(() => ElMessage.error("添加失败"));
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除城市「${row.cityName}」吗？`, "提示", { type: "warning" })
    .then(() => {
      deleteCityRequest(row.id)
        .then((res) => {
          if (res.data.code === 200) {
            ElMessage.success("删除成功");
            getCityList();
          } else {
            ElMessage.error(res.data.message || "删除失败");
          }
        })
        .catch(() => ElMessage.error("删除失败"));
    })
    .catch(() => {});
};

onMounted(() => {
  getCityList();
});
</script>

<template>
  <div class="addr-wrapper">
    <header class="tech-header">
      <div class="header-line"></div>
      <div class="header-content">
        <h1 class="main-title">
          <span class="title-icon">🏙</span>
          开通城市信息
          <span class="title-divider">/</span>
          <span class="title-sub">运营管理</span>
        </h1>
        <p class="sub-title">Operation Address Management</p>
      </div>
      <div class="header-line"></div>
    </header>

    <div class="main-content">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="城市编码">
            <el-input v-model="searchForm.cityCode" placeholder="请输入城市编码" clearable />
          </el-form-item>
          <el-form-item label="城市名称">
            <el-input v-model="searchForm.cityName" placeholder="请输入城市名称" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
            <el-button type="success" @click="openAddDialog">新增城市</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="cityCode" label="城市编码" />
        <el-table-column prop="cityName" label="城市名称" />
        <el-table-column prop="airportName" label="机场名称" />
        <el-table-column prop="status" label="运营状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '运营中' : '暂停运营' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑城市' : '新增城市'" width="520px">
      <el-form :model="cityForm" label-width="80px">
        <el-form-item label="城市编码">
          <el-input v-model="cityForm.cityCode" placeholder="请输入城市编码，如PEK" />
        </el-form-item>
        <el-form-item label="城市名称">
          <el-input v-model="cityForm.cityName" placeholder="请输入城市名称，如北京" />
        </el-form-item>
        <el-form-item label="机场名称">
          <el-input v-model="cityForm.airportName" placeholder="请输入机场名称" />
        </el-form-item>
        <el-form-item label="运营状态">
          <el-select v-model="cityForm.status" placeholder="请选择" style="width: 100%">
            <el-option label="运营中" :value="1" />
            <el-option label="暂停运营" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCity">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.addr-wrapper {
  width: 100%;
}

.tech-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 40px 20px;
}

.header-line {
  width: 75%;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, #00d4ff 30%, #00ffcc 50%, #00d4ff 70%, transparent 100%);
  box-shadow: 0 0 8px #00d4ff, 0 0 20px rgba(0, 212, 255, 0.3);
  position: relative;
}

.header-line::before {
  content: '';
  position: absolute;
  top: -3px;
  left: 50%;
  transform: translateX(-50%);
  width: 8px;
  height: 7px;
  background: #00ffcc;
  border-radius: 50%;
  box-shadow: 0 0 10px #00ffcc, 0 0 25px #00ffcc;
}

.header-content {
  text-align: center;
  padding: 22px 50px;
  position: relative;
}

.header-content::before,
.header-content::after {
  content: '';
  position: absolute;
  width: 14px;
  height: 40px;
  border: 2px solid rgba(0, 212, 255, 0.6);
  top: 50%;
  transform: translateY(-50%);
}

.header-content::before {
  left: 0;
  border-right: none;
  border-radius: 4px 0 0 4px;
  box-shadow: -3px 0 10px rgba(0, 212, 255, 0.2);
}

.header-content::after {
  right: 0;
  border-left: none;
  border-radius: 0 4px 4px 0;
  box-shadow: 3px 0 10px rgba(0, 0, 0, 0.2);
}

.main-title {
  font-size: 34px;
  font-weight: 700;
  letter-spacing: 6px;
  margin: 0;
  background: linear-gradient(180deg, #ffffff 0%, #60d5ff 40%, #0090ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title-icon {
  display: inline-block;
  margin-right: 10px;
  font-size: 30px;
  -webkit-text-fill-color: initial;
  filter: drop-shadow(0 0 8px rgba(0, 212, 255, 0.8));
}

.title-divider {
  margin: 0 10px;
  -webkit-text-fill-color: #00ffcc;
  opacity: 0.6;
  font-weight: 300;
}

.title-sub {
  background: linear-gradient(180deg, #00ffcc 0%, #00d4ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 28px;
  font-weight: 600;
  letter-spacing: 4px;
}

.sub-title {
  margin: 8px 0 0 0;
  font-size: 12px;
  letter-spacing: 8px;
  text-transform: uppercase;
  color: rgba(0, 212, 255, 0.45);
  font-weight: 300;
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
