<template>
  <div class="flight-wrapper">

    <div class="main-content">
      <!-- 条件查询表单 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="预订号">
            <el-input v-model="searchForm.bookingNumber" placeholder="请输入预订号" clearable />
          </el-form-item>
          <el-form-item label="航班批次">
            <el-input v-model="searchForm.flightId" placeholder="请输入航班批次编号" clearable />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="searchForm.name" placeholder="请输入客户姓名" clearable />
          </el-form-item>
          <el-form-item label="出发地">
            <el-input v-model="searchForm.from" placeholder="请输入出发地" clearable />
          </el-form-item>
          <el-form-item label="目的地">
            <el-input v-model="searchForm.to" placeholder="请输入目的地" clearable />
          </el-form-item>
          <el-form-item label="出发时间">
            <el-date-picker
              v-model="searchForm.date"
              type="datetime"
              placeholder="选择出发时间"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DDTHH:mm:ss"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.bookingStatus" placeholder="全部" clearable style="width: 130px">
              <el-option label="已确认" value="CONFIRMED" />
              <el-option label="已取消" value="CANCELLED" />
              <el-option label="已完成" value="COMPLETED" />
            </el-select>
          </el-form-item>
          <el-form-item label="舱位">
            <el-select v-model="searchForm.bookingClass" placeholder="全部" clearable style="width: 130px">
              <el-option label="经济舱" value="ECONOMY" />
              <el-option label="商务舱" value="BUSINESS" />
              <el-option label="头等舱" value="FIRST" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
            <el-button type="success" @click="openAddDialog">新增预订</el-button>
            <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchCancel">批量退订</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" stripe style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" :selectable="row => row.bookingStatus !== 'CANCELLED'" />
        <el-table-column prop="bookingNumber" label="预定号" />
        <el-table-column prop="flightId" label="航班批次编号" width="130" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="date" label="出发时间" min-width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.date) }}
          </template>
        </el-table-column>
        <el-table-column prop="from" label="出发地" />
        <el-table-column prop="to" label="目的地" />
        <el-table-column prop="bookingStatus" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.bookingStatus === 'CONFIRMED' ? 'success' : scope.row.bookingStatus === 'CANCELLED' ? 'danger' : 'info'">
              {{ { CONFIRMED: '已确认', CANCELLED: '已取消', COMPLETED: '已完成' }[scope.row.bookingStatus] || scope.row.bookingStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="bookingClass" label="舱位">
          <template #default="scope">
            {{ { ECONOMY: '经济舱', PREMIUM_ECONOMY: '高级经济舱', BUSINESS: '商务舱', FIRST: '头等舱' }[scope.row.bookingClass] || scope.row.bookingClass }}
          </template>
        </el-table-column>
        <el-table-column prop="certificateSubstring" label="证件号后6位" />
        <el-table-column label="操作" fixed="right" width="260">
          <template #default="scope">
            <el-button size="small" type="primary" @click="openEditDialog(scope.row)">更改预定</el-button>
            <el-button size="small" type="danger" @click="handleCancel(scope.row)" v-if="scope.row.bookingStatus !== 'CANCELLED'">退订</el-button>
            <el-button size="small" type="warning" @click="handleRecovery(scope.row)" v-if="scope.row.bookingStatus === 'CANCELLED'">恢复</el-button>
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

    <!-- 新增/编辑 对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '更改预定' : '新增预订'" width="520px">
      <el-form :model="bookingForm" label-width="80px">
        <el-form-item label="客户姓名">
          <el-input v-model="bookingForm.name" placeholder="请输入客户姓名" />
        </el-form-item>
        <el-form-item label="航班批次">
          <el-input v-model="bookingForm.flightId" :disabled="isEdit" placeholder="请输入航班批次编号" />
        </el-form-item>
        <el-form-item label="舱位等级">
          <el-select v-model="bookingForm.bookingClass" placeholder="请选择舱位等级" style="width: 100%">
            <el-option label="经济舱" value="ECONOMY" />
            <el-option label="高级经济舱" value="PREMIUM_ECONOMY" />
            <el-option label="商务舱" value="BUSINESS" />
            <el-option label="头等舱" value="FIRST" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBooking">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getBookingsRequest,
  addBookingRequest,
  changeBookingRequest,
  cancelBookingRequest,
  recoveryBookingRequest
} from "../../api/flight.js";

export default {
  name: "flight",
  setup() {
    const tableData = ref([]);
    const dialogVisible = ref(false);
    const isEdit = ref(false);
    const selectedRows = ref([]);
    const pagination = ref({ current: 1, size: 10, total: 0 });
    const searchForm = ref({ bookingNumber: "", name: "", flightId: "", from: "", to: "", date: "", bookingStatus: "", bookingClass: "" });
    const bookingForm = ref({
      name: "",
      flightId: "",
      bookingClass: "",
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

    const getBookings = () => {
      getBookingsRequest({
        bookingNumber: searchForm.value.bookingNumber || undefined,
        name: searchForm.value.name || undefined,
        flightId: searchForm.value.flightId || undefined,
        from: searchForm.value.from || undefined,
        to: searchForm.value.to || undefined,
        date: searchForm.value.date || undefined,
        bookingStatus: searchForm.value.bookingStatus || undefined,
        bookingClass: searchForm.value.bookingClass || undefined,
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
      getBookings();
    };

    const resetSearch = () => {
      searchForm.value = { bookingNumber: "", name: "", flightId: "", from: "", to: "", date: "", bookingStatus: "", bookingClass: "" };
      pagination.value.current = 1;
      getBookings();
    };

    const handleSizeChange = () => {
      pagination.value.current = 1;
      getBookings();
    };

    const handleCurrentChange = () => {
      getBookings();
    };

    const openAddDialog = () => {
      isEdit.value = false;
      bookingForm.value = { name: "", flightId: "", bookingClass: "" };
      dialogVisible.value = true;
    };

    const openEditDialog = (row) => {
      isEdit.value = true;
      bookingForm.value = { ...row };
      dialogVisible.value = true;
    };

    const submitBooking = () => {
      if (isEdit.value) {
        changeBookingRequest(bookingForm.value)
          .then(() => {
              ElMessage.success("修改成功");
              dialogVisible.value = false;
              getBookings();
          })
          .catch(() => ElMessage.error("修改失败"));
      } else {
        addBookingRequest(bookingForm.value)
          .then(() => {
              ElMessage.success("添加成功");
              dialogVisible.value = false;
              getBookings();
          })
          .catch(() => ElMessage.error("添加失败"));
      }
    };

    const handleSelectionChange = (rows) => {
      selectedRows.value = rows;
    };

    const handleCancel = (row) => {
      ElMessageBox.confirm("确定要退订该航班吗？", "提示", { type: "warning" })
        .then(() => {
          cancelBookingRequest([row.bookingNumber])
            .then(() => {
                ElMessage.success("退订成功");
                getBookings();
            })
            .catch(() => ElMessage.error("退订失败"));
        })
        .catch(() => {});
    };

    const handleBatchCancel = () => {
      const bookingNumbers = selectedRows.value.map(row => row.bookingNumber);
      ElMessageBox.confirm(`确定要批量退订选中的 ${bookingNumbers.length} 条航班记录吗？`, "批量退订", { type: "warning" })
        .then(() => {
          cancelBookingRequest(bookingNumbers)
            .then(() => {
                ElMessage.success("批量退订成功");
                selectedRows.value = [];
                getBookings();
            })
            .catch(() => ElMessage.error("批量退订失败"));
        })
        .catch(() => {});
    };

    const handleRecovery = (row) => {
      ElMessageBox.confirm("确定要恢复该预订吗？", "提示", { type: "warning" })
        .then(() => {
          recoveryBookingRequest({ bookingNumber: row.bookingNumber, name: row.name })
            .then(() => {
                ElMessage.success("恢复成功");
                getBookings();
            })
            .catch(() => ElMessage.error("恢复失败"));
        })
        .catch(() => {});
    };

    onMounted(() => {
      getBookings();
    });

    return {
      tableData,
      searchForm,
      bookingForm,
      dialogVisible,
      isEdit,
      selectedRows,
      pagination,
      formatDateTime,
      getBookings,
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      openAddDialog,
      openEditDialog,
      submitBooking,
      handleCancel,
      handleBatchCancel,
      handleSelectionChange,
      handleRecovery,
    };
  },
};
</script>

<style scoped>
.flight-wrapper {
  width: 100%;
}


@keyframes fly {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  25% { transform: translateY(-6px) rotate(-3deg); }
  75% { transform: translateY(4px) rotate(3deg); }
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

/* ========== 主内容区 ========== */
.main-content {
  padding: 20px 30px 0;
}

/* ========== 查询栏 ========== */
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

/* ========== 对话框暗色适配 ========== */
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

/* ========== 表格样式适配暗色主题 ========== */
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

/* ========== 分页栏 ========== */
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