<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getCouponListRequest,
  addCouponRequest,
  updateCouponRequest,
  deleteCouponRequest,
} from "../../api/coupon.js";

const tableData = ref([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const selectedIds = ref([]);
const searchForm = ref({
  couponCode: "",
  couponType: undefined,
  couponName: "",
  status: undefined,
});
const couponForm = ref({
  id: null,
  couponName: "",
  couponCode: "",
  couponType: 0,
  couponPrice: null,
  status: 0,
});

const couponTypeMap = { 1: "满减券", 2: "无门槛券", 3: "折扣券" };
const couponStatusMap = { 0: "可用", 1: "不可用" };
const couponStatusTypeMap = { 0: "success", 1: "info" };

const formatDateTime = (value) => {
  if (!value) return "";
  const date = new Date(value);
  if (isNaN(date.getTime())) return value;
  const y = date.getFullYear();
  const M = String(date.getMonth() + 1).padStart(2, "0");
  const d = String(date.getDate()).padStart(2, "0");
  const h = String(date.getHours()).padStart(2, "0");
  const m = String(date.getMinutes()).padStart(2, "0");
  const s = String(date.getSeconds()).padStart(2, "0");
  return `${y}-${M}-${d} ${h}:${m}:${s}`;
};

const getCouponList = () => {
  getCouponListRequest({
    couponCode: searchForm.value.couponCode || undefined,
    couponType: searchForm.value.couponType !== undefined && searchForm.value.couponType !== null ? searchForm.value.couponType : undefined,
    couponName: searchForm.value.couponName || undefined,
    status: searchForm.value.status !== undefined && searchForm.value.status !== null ? searchForm.value.status : undefined,
  })
    .then((res) => {
      if (res.code === 200 && res.data) {
        tableData.value = res.data || [];
      } else {
        tableData.value = [];
      }
    })
    .catch((error) => {
      console.error(error);
    });
};

const handleSearch = () => {
  getCouponList();
};

const resetSearch = () => {
  searchForm.value = {
    couponCode: "",
    couponType: undefined,
    couponName: "",
    status: undefined,
  };
  getCouponList();
};

const handleSelectionChange = (rows) => {
  selectedIds.value = rows.map((r) => r.id);
};

const openAddDialog = () => {
  isEdit.value = false;
  couponForm.value = {
    id: null,
    couponName: "",
    couponCode: "",
    couponType: 0,
    couponPrice: null,
    status: 0,
  };
  dialogVisible.value = true;
};

const openEditDialog = (row) => {
  isEdit.value = true;
  couponForm.value = { ...row };
  dialogVisible.value = true;
};

const submitCoupon = () => {
  if (!couponForm.value.couponName) {
    ElMessage.warning("优惠券名称不能为空");
    return;
  }
  if (!couponForm.value.couponCode) {
    ElMessage.warning("优惠券编码不能为空");
    return;
  }
  if (isEdit.value) {
    updateCouponRequest(couponForm.value)
      .then(() => {
        ElMessage.success("修改成功");
        dialogVisible.value = false;
        getCouponList();
      })
      .catch(() => ElMessage.error("修改失败"));
  } else {
    addCouponRequest(couponForm.value)
      .then(() => {
        ElMessage.success("添加成功");
        dialogVisible.value = false;
        getCouponList();
      })
      .catch(() => ElMessage.error("添加失败"));
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除优惠券「${row.couponName}」吗？`,
    "提示",
    { type: "warning" }
  )
    .then(() => {
      deleteCouponRequest(row.id)
        .then(() => {
          ElMessage.success("删除成功");
          getCouponList();
        })
        .catch(() => ElMessage.error("删除失败"));
    })
    .catch(() => {});
};

const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning("请先选择要删除的优惠券");
    return;
  }
  ElMessageBox.confirm(
    `确定要批量删除选中的 ${selectedIds.value.length} 条优惠券吗？删除后将设为不可用。`,
    "提示",
    { type: "warning" }
  )
    .then(() => {
      deleteCouponRequest(selectedIds.value.join(","))
        .then(() => {
          ElMessage.success("批量删除成功");
          selectedIds.value = [];
          getCouponList();
        })
        .catch(() => ElMessage.error("批量删除失败"));
    })
    .catch(() => {});
};

onMounted(() => {
  getCouponList();
});
</script>

<template>
  <div class="coupon-wrapper">
    <div class="main-content">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="优惠券编码">
            <el-input
              v-model="searchForm.couponCode"
              placeholder="请输入优惠券编码"
              clearable
            />
          </el-form-item>
          <el-form-item label="优惠券类型">
            <el-select
              v-model="searchForm.couponType"
              placeholder="全部"
              clearable
              style="width: 130px"
            >
              <el-option label="满减券" :value="1" />
              <el-option label="无门槛券" :value="2" />
              <el-option label="折扣券" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="优惠券名称">
            <el-input
              v-model="searchForm.couponName"
              placeholder="请输入优惠券名称"
              clearable
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="searchForm.status"
              placeholder="全部"
              clearable
              style="width: 130px"
            >
              <el-option label="可用" :value="0" />
              <el-option label="不可用" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
            <el-button type="success" @click="openAddDialog"
              >新增优惠券</el-button
            >
            <el-button type="danger" @click="handleBatchDelete"
              >批量删除</el-button
            >
          </el-form-item>
        </el-form>
      </div>

      <el-table
        :data="tableData"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="45" />
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="couponName" label="优惠券名称" width="150" />
        <el-table-column prop="couponCode" label="优惠券编码" width="160" />
        <el-table-column label="类型" width="100">
          <template #default="scope">
            <el-tag size="small" type="warning">
              {{ couponTypeMap[scope.row.couponType] || "未知" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="面值/折扣" width="110">
          <template #default="scope">
            <span v-if="scope.row.couponPrice != null" class="price-text"
              >¥{{ scope.row.couponPrice.toFixed(2) }}</span
            >
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="scope">
            <el-tag
              :type="couponStatusTypeMap[scope.row.status] || 'info'"
              size="small"
            >
              {{ couponStatusMap[scope.row.status] || "未知" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="更新时间" min-width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="160">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="openEditDialog(scope.row)"
              >编辑</el-button
            >
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑优惠券' : '新增优惠券'"
      width="500px"
    >
      <el-form :model="couponForm" label-width="100px">
        <el-form-item label="优惠券名称">
          <el-input
            v-model="couponForm.couponName"
            placeholder="请输入优惠券名称"
          />
        </el-form-item>
        <el-form-item label="优惠券编码">
          <el-input
            v-model="couponForm.couponCode"
            placeholder="请输入优惠券编码"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="优惠券类型">
          <el-select v-model="couponForm.couponType" style="width: 100%">
            <el-option label="满减券" :value="1" />
            <el-option label="无门槛券" :value="2" />
            <el-option label="折扣券" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="面值/折扣">
          <el-input-number
            v-model="couponForm.couponPrice"
            :min="0"
            :precision="2"
            :step="10"
            style="width: 100%"
            placeholder="请输入面值"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="couponForm.status" style="width: 100%">
            <el-option label="可用" :value="0" />
            <el-option label="不可用" :value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCoupon">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.coupon-wrapper {
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

.price-text {
  color: #ff6b6b;
  font-weight: 700;
  font-size: 15px;
}
</style>