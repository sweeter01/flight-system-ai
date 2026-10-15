<template>
  <div class="flight-wrapper">

    <div class="main-content">
      <!-- 条件查询表单 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="航班编号">
            <el-input v-model="searchForm.flightId" placeholder="请输入航班编号" clearable />
          </el-form-item>
          <el-form-item label="客机类型">
            <el-input v-model="searchForm.flightType" placeholder="请输入客机类型" clearable />
          </el-form-item>
          <el-form-item label="出发时间">
            <el-date-picker
              v-model="searchForm.departureTime"
              type="datetime"
              placeholder="请选择出发时间"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DDTHH:mm:ss"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="出发城市">
            <el-input v-model="searchForm.departureCity" placeholder="请输入出发城市" clearable />
          </el-form-item>
          <el-form-item label="到达城市">
            <el-input v-model="searchForm.arrivalCity" placeholder="请输入到达城市" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
            <el-button type="success" @click="openAddDialog">新增航班</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" stripe style="width: 100%" :scroll-x="true">
        <el-table-column prop="id" label="ID" min-width="60" align="center" />
        <el-table-column prop="flightId" label="航班编号"  min-width="100" />
        <el-table-column prop="planeId" label="客机编号" min-width="70" align="center" />
        <el-table-column prop="planeType" label="客机类型" min-width="120" show-overflow-tooltip />
        <el-table-column prop="departureCity" label="出发城市" min-width="70" align="center" />
        <el-table-column prop="arrivalCity" label="到达城市" min-width="70" align="center" />
        <el-table-column prop="departureTime" label="起飞时间" min-width="160" show-overflow-tooltip>
          <template #default="scope">
            {{ formatDateTime(scope.row.departureTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="arrivalTime" label="到达时间" min-width="160" show-overflow-tooltip>
          <template #default="scope">
            {{ formatDateTime(scope.row.arrivalTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalSeats" label="总座位数" min-width="70" align="center" />
        <el-table-column prop="realSeats" label="实际入座" min-width="70" align="center" />
        <el-table-column label="剩余座位" min-width="70" align="center">
          <template #default="scope">
            <span :class="{ 'seat-warning': scope.row.totalSeats - scope.row.realSeats <= 10 }">
              {{ scope.row.totalSeats - scope.row.realSeats }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="70" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '正常' : '停飞' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="140" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑航班' : '新增航班'" width="560px">
      <el-form :model="flightForm" label-width="90px">
        <el-form-item label="航班编号" v-if="isEdit">
          <el-input v-model="flightForm.flightId" :disabled="isEdit" placeholder="请输入航班编号" />
        </el-form-item>
        <el-form-item label="客机编号">
          <el-input v-model.number="flightForm.planeId" placeholder="请输入客机编号" />
        </el-form-item>
        <el-form-item label="客机类型">
          <el-input v-model="flightForm.planeType" placeholder="如：波音737-800、空客A320" />
        </el-form-item>
        <el-form-item label="出发城市">
          <el-input v-model="flightForm.departureCity" placeholder="请输入出发城市" />
        </el-form-item>
        <el-form-item label="到达城市">
          <el-input v-model="flightForm.arrivalCity" placeholder="请输入到达城市" />
        </el-form-item>
        <el-form-item label="起飞时间">
          <el-date-picker
            v-model="flightForm.departureTime"
            type="datetime"
            placeholder="选择起飞时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="到达时间">
          <el-date-picker
            v-model="flightForm.arrivalTime"
            type="datetime"
            placeholder="选择到达时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="总座位数" v-if="isEdit">
          <el-input-number v-model="flightForm.totalSeats" :disabled="isEdit" :min="1" :max="500" style="width: 100%" />
        </el-form-item>
        <el-form-item label="实际入座" v-if="isEdit">
          <el-input-number v-model="flightForm.realSeats" :min="0" :max="500" style="width: 100%" />
        </el-form-item>
        <el-form-item label="航班状态">
          <el-select v-model="flightForm.status" style="width: 100%">
            <el-option label="正常" :value="1" />
            <el-option label="停飞" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitFlight">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getFlightsRequest,
  addFlightRequest,
  updateFlightRequest,
  deleteFlightRequest
} from "../../api/flight.js";

export default {
  name: "flightDetail",
  setup() {
    const tableData = ref([]);
    const dialogVisible = ref(false);
    const isEdit = ref(false);
    const pagination = ref({ current: 1, size: 10, total: 0 });
    const searchForm = ref({ flightId: "", flightType: "", departureTime: "", departureCity: "", arrivalCity: "" });
    const flightForm = ref({
      id: null,
      flightId: "",
      planeId: null,
      planeType: "",
      departureCity: "",
      arrivalCity: "",
      departureTime: "",
      arrivalTime: "",
      totalSeats: 186,
      realSeats: 0,
      status: 1,
    });

    const formatDateTime = (value) => {
      if (!value) return '';
      const date = new Date(value);
      if (isNaN(date.getTime())) return value;
      const y = date.getFullYear();
      const M = String(date.getMonth() + 1).padStart(2, '0');
      const d = String(date.getDate()).padStart(2, '0');
      const h = String(date.getHours()).padStart(2, '0');
      const m = String(date.getMinutes()).padStart(2, '0');
      return `${y}年${M}月${d}日 ${h}:${m}`;
    };

    const getFlights = () => {
      getFlightsRequest({
        flightId: searchForm.value.flightId || undefined,
        flightType: searchForm.value.flightType || undefined,
        departureTime: searchForm.value.departureTime || undefined,
        departureCity: searchForm.value.departureCity || undefined,
        arrivalCity: searchForm.value.arrivalCity || undefined,
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
      getFlights();
    };

    const resetSearch = () => {
      searchForm.value = { flightId: "", flightType: "", departureTime: "", departureCity: "", arrivalCity: "" };
      pagination.value.current = 1;
      getFlights();
    };

    const handleSizeChange = () => {
      pagination.value.current = 1;
      getFlights();
    };

    const handleCurrentChange = () => {
      getFlights();
    };

    const openAddDialog = () => {
      isEdit.value = false;
      flightForm.value = {
        id: null,
        flightId: "",
        planeId: null,
        planeType: "",
        departureCity: "",
        arrivalCity: "",
        departureTime: "",
        arrivalTime: "",
        totalSeats: 186,
        realSeats: 0,
        status: 1,
      };
      dialogVisible.value = true;
    };

    const openEditDialog = (row) => {
      isEdit.value = true;
      flightForm.value = { ...row };
      dialogVisible.value = true;
    };

    const submitFlight = () => {
      if (isEdit.value) {
        updateFlightRequest(flightForm.value)
          .then(() => {
              ElMessage.success("修改成功");
              dialogVisible.value = false;
              getFlights();
          })
          .catch(() => ElMessage.error("修改失败"));
      } else {
        addFlightRequest(flightForm.value)
          .then(() => {
              ElMessage.success("添加成功");
              dialogVisible.value = false;
              getFlights();
          })
          .catch(() => ElMessage.error("添加失败"));
      }
    };

    const handleDelete = (row) => {
      ElMessageBox.confirm(`确定要删除航班 ${row.flightId} 吗？`, "提示", { type: "warning" })
        .then(() => {
          deleteFlightRequest(row.id)
            .then(() => {
                ElMessage.success("删除成功");
                getFlights();
            })
            .catch(() => ElMessage.error("删除失败"));
        })
        .catch(() => {});
    };

    onMounted(() => {
      getFlights();
    });

    return {
      tableData,
      searchForm,
      flightForm,
      dialogVisible,
      isEdit,
      pagination,
      formatDateTime,
      getFlights,
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      openAddDialog,
      openEditDialog,
      submitFlight,
      handleDelete,
    };
  },
};
</script>

<style scoped>
.flight-wrapper {
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
  font-size: 13px;
}

:deep(.el-table .el-table__cell) {
  padding: 8px 6px;
}

:deep(.el-table .el-table__header th) {
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
  background: rgba(0, 212, 255, 0.03);
}

:deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar) {
  height: 6px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
  background: rgba(0, 212, 255, 0.3);
  border-radius: 3px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-track) {
  background: rgba(255, 255, 255, 0.03);
}

.seat-warning {
  color: #ff6b6b;
  font-weight: 700;
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