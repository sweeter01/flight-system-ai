<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getOrderListRequest,
  getOrderDetailRequest,
  updateOrderRequest,
  deleteOrderRequest,
  cancelOrderRequest,
} from "../../api/order.js";

const tableData = ref([]);
const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const isEdit = ref(false);
const selectedIds = ref([]);
const pagination = ref({ current: 1, size: 10, total: 0 });
const searchForm = ref({
  orderCode: "",
  passengerName: "",
  status: "",
  paymentMethod: "",
});
const orderForm = ref({
  id: null,
  orderCode: "",
  bookingNumber: null,
  passengerId: null,
  passengerName: "",
  discount: null,
  couponCode: "",
  paymentMethod: "",
  comment: "",
  status: 0,
});
const currentDetail = ref(null);

const orderStatusMap = { 0: "待支付", 1: "已支付", 2: "已取消", 3: "已完成" };
const orderStatusTypeMap = { 0: "warning", 1: "success", 2: "danger", 3: "info" };
const paymentMethodMap = { ALIPAY: "支付宝", WECHAT: "微信", CREDIT: "信用卡", DEBIT: "借记卡" };
const bookingStatusMap = { CONFIRMED: "已确认", CANCELLED: "已取消", COMPLETED: "已完成" };
const bookingStatusTypeMap = { CONFIRMED: "success", CANCELLED: "danger", COMPLETED: "info" };
const bookingClassMap = { ECONOMY: "经济舱", PREMIUM_ECONOMY: "高级经济舱", BUSINESS: "商务舱", FIRST: "头等舱" };
const couponTypeMap = { 0: "折扣券", 1: "满减券", 2: "无门槛券" };

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

const getOrderList = () => {
  getOrderListRequest({
    orderCode: searchForm.value.orderCode || undefined,
    passengerName: searchForm.value.passengerName || undefined,
    status: searchForm.value.status !== "" ? searchForm.value.status : undefined,
    paymentMethod: searchForm.value.paymentMethod || undefined,
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
  getOrderList();
};

const resetSearch = () => {
  searchForm.value = {
    orderCode: "",
    passengerName: "",
    status: "",
    paymentMethod: "",
  };
  pagination.value.current = 1;
  getOrderList();
};

const handleSizeChange = () => {
  pagination.value.current = 1;
  getOrderList();
};

const handleCurrentChange = () => {
  getOrderList();
};

const handleSelectionChange = (rows) => {
  selectedIds.value = rows.map((r) => r.id);
};

const openEditDialog = (row) => {
  isEdit.value = true;
  orderForm.value = { ...row };
  dialogVisible.value = true;
};

const submitOrder = () => {
  if (!orderForm.value.orderCode) {
    ElMessage.warning("订单编码不能为空");
    return;
  }
  updateOrderRequest(orderForm.value)
    .then(() => {
      ElMessage.success("修改成功");
      dialogVisible.value = false;
      getOrderList();
    })
    .catch(() => ElMessage.error("修改失败"));
};

const handleDelete = (row) => {
  if (row.status !== 2 && row.status !== 3) {
    ElMessage.warning("只能删除已取消或已完成的订单");
    return;
  }
  ElMessageBox.confirm(`确定要删除订单「${row.orderCode}」吗？`, "提示", {
    type: "warning",
  })
    .then(() => {
      deleteOrderRequest(row.id)
        .then(() => {
          ElMessage.success("删除成功");
          getOrderList();
        })
        .catch(() => ElMessage.error("删除失败"));
    })
    .catch(() => {});
};

const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning("请先选择要删除的订单");
    return;
  }
  ElMessageBox.confirm(
    `确定要批量删除选中的 ${selectedIds.value.length} 条订单吗？`,
    "提示",
    { type: "warning" }
  )
    .then(() => {
      deleteOrderRequest(selectedIds.value.join(","))
        .then(() => {
          ElMessage.success("批量删除成功");
          selectedIds.value = [];
          getOrderList();
        })
        .catch(() => ElMessage.error("批量删除失败"));
    })
    .catch(() => {});
};

const handleCancelOrder = (row) => {
  if (!row.bookingNumber) {
    ElMessage.warning("该订单无预订号，无法取消");
    return;
  }
  ElMessageBox.confirm(`确定要取消订单「${row.orderCode}」吗？`, "提示", {
    type: "warning",
  })
    .then(() => {
      cancelOrderRequest(row.bookingNumber)
        .then(() => {
          ElMessage.success("取消成功");
          getOrderList();
        })
        .catch(() => ElMessage.error("取消失败"));
    })
    .catch(() => {});
};

const openDetailDialog = (row) => {
  getOrderDetailRequest(row.id)
    .then((res) => {
      if (res.code === 200 && res.data) {
        currentDetail.value = res.data;
        detailDialogVisible.value = true;
      } else {
        ElMessage.error("获取订单详情失败");
      }
    })
    .catch(() => ElMessage.error("获取订单详情失败"));
};

onMounted(() => {
  getOrderList();
});
</script>

<template>
  <div class="order-wrapper">
    <div class="main-content">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="订单编码">
            <el-input
              v-model="searchForm.orderCode"
              placeholder="请输入订单编码"
              clearable
            />
          </el-form-item>
          <el-form-item label="用户姓名">
            <el-input
              v-model="searchForm.passengerName"
              placeholder="请输入用户姓名"
              clearable
            />
          </el-form-item>
          <el-form-item label="订单状态">
            <el-select
              v-model="searchForm.status"
              placeholder="全部"
              clearable
              style="width: 130px"
            >
              <el-option label="待支付" :value="0" />
              <el-option label="已支付" :value="1" />
              <el-option label="已取消" :value="2" />
              <el-option label="已完成" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="支付方式">
            <el-select
              v-model="searchForm.paymentMethod"
              placeholder="全部"
              clearable
              style="width: 130px"
            >
              <el-option label="支付宝" value="ALIPAY" />
              <el-option label="微信" value="WECHAT" />
              <el-option label="信用卡" value="CREDIT" />
              <el-option label="借记卡" value="DEBIT" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
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
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="orderCode" label="订单编码" width="160" />
        <el-table-column prop="passengerName" label="乘客姓名" width="100" />
        <el-table-column label="订单状态" width="90">
          <template #default="scope">
            <el-tag
              :type="orderStatusTypeMap[scope.row.status] || 'info'"
              size="small"
            >
              {{ orderStatusMap[scope.row.status] || "未知" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="支付方式" width="90">
          <template #default="scope">
            {{ paymentMethodMap[scope.row.paymentMethod] ||
            scope.row.paymentMethod || "-" }}
          </template>
        </el-table-column>
        <el-table-column label="折扣" width="70">
          <template #default="scope">
            {{ scope.row.discount != null ? scope.row.discount + "折" : "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="couponCode" label="优惠券码" width="130" />
        <el-table-column prop="comment" label="备注" min-width="100" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="scope">
            <el-button
              size="small"
              type="warning"
              @click="openDetailDialog(scope.row)"
              >详情</el-button
            >
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

    <el-dialog
      v-model="dialogVisible"
      title="编辑订单"
      width="600px"
    >
      <el-form :model="orderForm" label-width="80px">
        <el-form-item label="订单编码">
          <el-input v-model="orderForm.orderCode" disabled />
        </el-form-item>
        <el-form-item label="乘客姓名">
          <el-input v-model="orderForm.passengerName" disabled />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="orderForm.status" style="width: 100%">
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已取消" :value="2" />
            <el-option label="已完成" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="orderForm.paymentMethod" style="width: 100%">
            <el-option label="支付宝" value="ALIPAY" />
            <el-option label="微信" value="WECHAT" />
            <el-option label="信用卡" value="CREDIT" />
            <el-option label="借记卡" value="DEBIT" />
          </el-select>
        </el-form-item>
        <el-form-item label="折扣">
          <el-input-number
            v-model="orderForm.discount"
            :min="0"
            :max="10"
            :precision="1"
            :step="0.5"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="优惠券码">
          <el-input v-model="orderForm.couponCode" placeholder="请输入优惠券码" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="orderForm.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitOrder">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="1000px"
      top="5vh"
    >
      <div v-if="currentDetail" class="order-detail">
        <div class="detail-section">
          <div class="section-title">📋 订单基本信息</div>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="订单编码">{{
              currentDetail.orderCode || "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag
                :type="orderStatusTypeMap[currentDetail.status] || 'info'"
                size="small"
              >
                {{ orderStatusMap[currentDetail.status] || "未知" }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="乘客姓名">{{
              currentDetail.passengerName || "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="支付方式">{{
              paymentMethodMap[currentDetail.paymentMethod] ||
              currentDetail.paymentMethod ||
              "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="折扣">{{
              currentDetail.discount != null
                ? currentDetail.discount + "折"
                : "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="优惠券码">{{
              currentDetail.couponCode || "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="原价">
              <span
                v-if="currentDetail.primePrice != null"
                class="price-text"
                >¥{{ currentDetail.primePrice.toFixed(2) }}</span
              >
              <span v-else>-</span>
            </el-descriptions-item>
            <el-descriptions-item label="实付金额">
              <span
                v-if="currentDetail.totalPrice != null"
                class="price-text"
                >¥{{ currentDetail.totalPrice.toFixed(2) }}</span
              >
              <span v-else>-</span>
            </el-descriptions-item>
            <el-descriptions-item label="备注">{{
              currentDetail.comment || "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{
              formatDateTime(currentDetail.createTime)
            }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{
              formatDateTime(currentDetail.updateTime)
            }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div
          v-if="currentDetail.bookingRecord"
          class="detail-section"
        >
          <div class="section-title">✈ 航班信息</div>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="预订号">{{
              currentDetail.bookingRecord.bookingNumber || "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="航班批次">{{
              currentDetail.bookingRecord.flightId || "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="乘客姓名">{{
              currentDetail.bookingRecord.name || "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="出发地">{{
              currentDetail.bookingRecord.from || "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="目的地">{{
              currentDetail.bookingRecord.to || "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="出发时间">{{
              formatDateTime(currentDetail.bookingRecord.date)
            }}</el-descriptions-item>
            <el-descriptions-item label="舱位等级">
              <el-tag size="small" type="primary">
                {{
                  bookingClassMap[currentDetail.bookingRecord.bookingClass] ||
                  currentDetail.bookingRecord.bookingClass ||
                  "-"
                }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="预订状态">
              <el-tag
                :type="
                  bookingStatusTypeMap[
                    currentDetail.bookingRecord.bookingStatus
                  ] || 'info'
                "
                size="small"
              >
                {{
                  bookingStatusMap[currentDetail.bookingRecord.bookingStatus] ||
                  currentDetail.bookingRecord.bookingStatus ||
                  "-"
                }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="证件后6位">{{
              currentDetail.bookingRecord.certificateSubstring || "-"
            }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div
          v-if="currentDetail.coupon"
          class="detail-section"
        >
          <div class="section-title">🎫 优惠券信息</div>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="优惠券名称">{{
              currentDetail.coupon.couponName || "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="优惠券码">{{
              currentDetail.coupon.couponCode || "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="类型">
              <el-tag size="small" type="warning">
                {{
                  couponTypeMap[currentDetail.coupon.couponType] ||
                  currentDetail.coupon.couponType ||
                  "-"
                }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="面值/折扣">
              {{
                currentDetail.coupon.couponPrice != null
                  ? "¥" + currentDetail.coupon.couponPrice.toFixed(2)
                  : "-"
              }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag
                :type="
                  currentDetail.coupon.status === 1 ? 'success' : 'info'
                "
                size="small"
              >
                {{ currentDetail.coupon.status === 1 ? "可用" : "不可用" }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{
              formatDateTime(currentDetail.coupon.createTime)
            }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.order-wrapper {
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

.detail-section {
  margin-bottom: 16px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #00ffcc;
  margin-bottom: 10px;
  padding-left: 4px;
  border-left: 3px solid #00d4ff;
}

.price-text {
  color: #ff6b6b;
  font-weight: 700;
  font-size: 15px;
}

:deep(.el-descriptions) {
  --el-descriptions-table-border: 1px solid rgba(0, 212, 255, 0.1);
}

:deep(.el-descriptions__body) {
  background: transparent;
}

:deep(.el-descriptions .el-descriptions__cell) {
  background: rgba(10, 14, 39, 0.4);
  color: rgba(255, 255, 255, 0.85);
}

:deep(.el-descriptions .el-descriptions__label) {
  color: rgba(0, 212, 255, 0.8);
  background: rgba(0, 212, 255, 0.05);
}

:deep(.el-empty__description p) {
  color: rgba(255, 255, 255, 0.5);
}
</style>