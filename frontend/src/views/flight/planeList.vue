<template>
  <div class="plane-wrapper">

    <div class="main-content">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="客机编号">
            <el-input v-model="searchForm.id" placeholder="请输入客机编号" clearable />
          </el-form-item>
          <el-form-item label="客机类型">
            <el-input v-model="searchForm.planeType" placeholder="请输入客机类型" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
            <el-button type="success" @click="openAddDialog">新增客机</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="id" label="客机编号" min-width="100" align="center" />
        <el-table-column prop="planeType" label="客机类型" min-width="160" show-overflow-tooltip />
        <el-table-column prop="totalSeats" label="总座位数" min-width="100" align="center" />
        <el-table-column label="状态" min-width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '正常' : '停飞' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="180" align="center">
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑客机' : '新增客机'" width="480px">
      <el-form :model="planeForm" label-width="90px">
        <el-form-item label="客机编号" v-if="isEdit">
          <el-input v-model="planeForm.id" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="客机类型">
          <el-input v-model="planeForm.planeType" placeholder="如：波音737-800、空客A320" />
        </el-form-item>
        <el-form-item label="总座位数">
          <el-input-number v-model="planeForm.totalSeats" :min="1" :max="500" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="planeForm.status" style="width: 100%">
            <el-option label="正常" :value="1" />
            <el-option label="停飞" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPlane">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getPlanesRequest,
  addPlaneRequest,
  updatePlaneRequest,
  deletePlaneRequest
} from "../../api/flight.js";

export default {
  name: "planeList",
  setup() {
    const tableData = ref([]);
    const dialogVisible = ref(false);
    const isEdit = ref(false);
    const pagination = ref({ current: 1, size: 10, total: 0 });
    const searchForm = ref({ id: "", planeType: "" });
    const planeForm = ref({
      id: null,
      planeType: "",
      totalSeats: 186,
      status: 1,
    });

    const getPlanes = () => {
      getPlanesRequest({
        id: searchForm.value.id || undefined,
        planeType: searchForm.value.planeType || undefined,
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

    const handleSearch = () => {
      pagination.value.current = 1;
      getPlanes();
    };

    const resetSearch = () => {
      searchForm.value = { id: "", planeType: "" };
      pagination.value.current = 1;
      getPlanes();
    };

    const handleSizeChange = () => {
      pagination.value.current = 1;
      getPlanes();
    };

    const handleCurrentChange = () => {
      getPlanes();
    };

    const openAddDialog = () => {
      isEdit.value = false;
      planeForm.value = { id: null, planeType: "", totalSeats: 186, status: 1 };
      dialogVisible.value = true;
    };

    const openEditDialog = (row) => {
      isEdit.value = true;
      planeForm.value = { ...row };
      dialogVisible.value = true;
    };

    const submitPlane = () => {
      if (isEdit.value) {
        updatePlaneRequest(planeForm.value)
          .then(() => {
              ElMessage.success("修改成功");
              dialogVisible.value = false;
              getPlanes();
          })
          .catch(() => ElMessage.error("修改失败"));
      } else {
        addPlaneRequest(planeForm.value)
          .then(() => {
              ElMessage.success("添加成功");
              dialogVisible.value = false;
              getPlanes();
          })
          .catch(() => ElMessage.error("添加失败"));
      }
    };

    const handleDelete = (row) => {
      ElMessageBox.confirm(`确定要删除客机 ${row.id} (${row.planeType}) 吗？`, "提示", { type: "warning" })
        .then(() => {
          deletePlaneRequest(row.id)
            .then(() => {
                ElMessage.success("删除成功");
                getPlanes();
            })
            .catch(() => ElMessage.error("删除失败"));
        })
        .catch(() => {});
    };

    onMounted(() => {
      getPlanes();
    });

    return {
      tableData,
      searchForm,
      planeForm,
      dialogVisible,
      isEdit,
      pagination,
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      openAddDialog,
      openEditDialog,
      submitPlane,
      handleDelete,
    };
  },
};
</script>

<style scoped>
.plane-wrapper {
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