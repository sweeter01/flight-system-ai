<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getPassengerListRequest,
  getPassengerOrderVoRequest,
  addPassengerRequest,
  updatePassengerRequest,
  deletePassengerRequest,
} from "../../api/passenger.js";

const tableData = ref([]);
const dialogVisible = ref(false);
const orderDialogVisible = ref(false);
const isEdit = ref(false);
const pagination = ref({ current: 1, size: 10, total: 0 });
const searchForm = ref({
  name: "",
  ageMin: undefined,
  ageMax: undefined,
  sex: "",
  certificate: "",
  username: "",
});
const passengerForm = ref({
  id: null,
  name: "",
  age: null,
  sex: "男",
  phone: "",
  email: "",
  certificate: "",
  username: "",
  password: "",
});
const orderList = ref([]);
const currentPassengerName = ref("");
const orderLoading = ref(false);

const sexOptions = ["男", "女"];

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

const bookingStatusMap = {
  CONFIRMED: "已确认",
  CANCELLED: "已取消",
  COMPLETED: "已完成",
};
const bookingStatusTypeMap = {
  CONFIRMED: "success",
  CANCELLED: "danger",
  COMPLETED: "info",
};
const bookingClassMap = {
  ECONOMY: "经济舱",
  PREMIUM_ECONOMY: "高级经济舱",
  BUSINESS: "商务舱",
  FIRST: "头等舱",
};
const orderStatusMap = { 0: "待支付", 1: "已支付", 2: "已取消", 3: "已完成" };
const orderStatusTypeMap = { 0: "warning", 1: "success", 2: "danger", 3: "info" };
const paymentMethodMap = { ALIPAY: "支付宝", WECHAT: "微信", CREDIT: "信用卡", DEBIT: "借记卡" };
const couponTypeMap = { 0: "折扣券", 1: "满减券", 2: "无门槛券" };

const getPassengerList = () => {
  getPassengerListRequest({
    name: searchForm.value.name || undefined,
    ageMin: searchForm.value.ageMin || undefined,
    ageMax: searchForm.value.ageMax || undefined,
    sex: searchForm.value.sex || undefined,
    certificate: searchForm.value.certificate || undefined,
    username: searchForm.value.username || undefined,
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
  getPassengerList();
};

const resetSearch = () => {
  searchForm.value = {
    name: "",
    ageMin: undefined,
    ageMax: undefined,
    sex: "",
    certificate: "",
    username: "",
  };
  pagination.value.current = 1;
  getPassengerList();
};

const handleSizeChange = () => {
  pagination.value.current = 1;
  getPassengerList();
};

const handleCurrentChange = () => {
  getPassengerList();
};

const openAddDialog = () => {
  isEdit.value = false;
  passengerForm.value = {
    id: null,
    name: "",
    age: null,
    sex: "男",
    phone: "",
    email: "",
    certificate: "",
    username: "",
    password: "",
  };
  dialogVisible.value = true;
};

const openEditDialog = (row) => {
  isEdit.value = true;
  passengerForm.value = { ...row, password: "" };
  dialogVisible.value = true;
};

const submitPassenger = () => {
  if (!passengerForm.value.name) {
    ElMessage.warning("姓名不能为空");
    return;
  }
  if (!passengerForm.value.username) {
    ElMessage.warning("用户名不能为空");
    return;
  }
  if (!isEdit.value && !passengerForm.value.password) {
    ElMessage.warning("密码不能为空");
    return;
  }
  if (isEdit.value) {
    updatePassengerRequest(passengerForm.value)
      .then(() => {
        ElMessage.success("修改成功");
        dialogVisible.value = false;
        getPassengerList();
      })
      .catch(() => ElMessage.error("修改失败"));
  } else {
    addPassengerRequest(passengerForm.value)
      .then(() => {
        ElMessage.success("添加成功");
        dialogVisible.value = false;
        getPassengerList();
      })
      .catch(() => ElMessage.error("添加失败"));
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除乘客「${row.name}」吗？`,
    "提示",
    { type: "warning" }
  )
    .then(() => {
      deletePassengerRequest(row.id)
        .then(() => {
          ElMessage.success("删除成功");
          getPassengerList();
        })
        .catch(() => ElMessage.error("删除失败"));
    })
    .catch(() => {});
};

const openOrderDialog = (row) => {
  currentPassengerName.value = row.name;
  orderList.value = [];
  orderDialogVisible.value = true;
  orderLoading.value = true;
  getPassengerOrderVoRequest(row.id)
    .then((res) => {
      if (res.code === 200 && res.data) {
        orderList.value = res.data || [];
      } else {
        orderList.value = [];
      }
      orderLoading.value = false;
    })
    .catch((error) => {
      console.error(error);
      orderLoading.value = false;
      ElMessage.error("查询订单详情失败");
    });
};

onMounted(() => {
  getPassengerList();
});
</script>

<template>
  <div class="passenger-wrapper">
    <div class="main-content">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="姓名">
            <el-input
              v-model="searchForm.name"
              placeholder="请输入姓名"
              clearable
            />
          </el-form-item>
          <el-form-item label="年龄下限">
            <el-input-number
              v-model="searchForm.ageMin"
              :min="1"
              :max="150"
              placeholder="最小年龄"
              controls-position="right"
              style="width: 120px"
            />
          </el-form-item>
          <el-form-item label="年龄上限">
            <el-input-number
              v-model="searchForm.ageMax"
              :min="1"
              :max="150"
              placeholder="最大年龄"
              controls-position="right"
              style="width: 120px"
            />
          </el-form-item>
          <el-form-item label="性别">
            <el-select
              v-model="searchForm.sex"
              placeholder="全部"
              clearable
              style="width: 120px"
            >
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
          <el-form-item label="身份证号">
            <el-input
              v-model="searchForm.certificate"
              placeholder="请输入身份证号"
              clearable
            />
          </el-form-item>
          <el-form-item label="用户名">
            <el-input
              v-model="searchForm.username"
              placeholder="请输入用户名"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
            <el-button type="success" @click="openAddDialog"
              >新增乘客</el-button
            >
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="age" label="年龄" width="70" />
        <el-table-column prop="sex" label="性别" width="70" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="certificate" label="身份证号" width="180" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="createTime" label="创建时间" min-width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="260">
          <template #default="scope">
            <el-button
              size="small"
              type="warning"
              @click="openOrderDialog(scope.row)"
              >订单详情</el-button
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
      :title="isEdit ? '编辑乘客' : '新增乘客'"
      width="600px"
    >
      <el-form :model="passengerForm" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="passengerForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number
            v-model="passengerForm.age"
            :min="1"
            :max="150"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="passengerForm.sex" style="width: 100%">
            <el-option
              v-for="item in sexOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="passengerForm.phone"
            placeholder="请输入手机号"
          />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input
            v-model="passengerForm.email"
            placeholder="请输入邮箱"
          />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input
            v-model="passengerForm.certificate"
            placeholder="请输入身份证号"
          />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input
            v-model="passengerForm.username"
            placeholder="请输入用户名"
          />
        </el-form-item>
        <el-form-item label="密码" v-if="!isEdit">
          <el-input
            v-model="passengerForm.password"
            type="password"
            show-password
            placeholder="请输入密码"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPassenger">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="orderDialogVisible"
      :title="`${currentPassengerName} 的订单详情`"
      width="1100px"
      top="5vh"
    >
      <div v-loading="orderLoading">
        <el-empty
          v-if="!orderLoading && orderList.length === 0"
          description="暂无订单数据"
        />
        <div v-else class="order-list">
          <div
            v-for="(order, index) in orderList"
            :key="order.id"
            class="order-card"
          >
            <div class="order-card-header">
              <span class="order-code"
                >订单编号：{{ order.orderCode || "-" }}</span
              >
              <el-tag
                :type="orderStatusTypeMap[order.status] || 'info'"
                size="small"
              >
                {{ orderStatusMap[order.status] || "未知" }}
              </el-tag>
            </div>
            <div class="order-card-body">
              <el-descriptions :column="3" border size="small">
                <el-descriptions-item label="乘客姓名">{{
                  order.passengerName || "-"
                }}</el-descriptions-item>
                <el-descriptions-item label="支付方式">{{
                  paymentMethodMap[order.paymentMethod] || order.paymentMethod || "-"
                }}</el-descriptions-item>
                <el-descriptions-item label="折扣">{{
                  order.discount != null ? order.discount + "折" : "-"
                }}</el-descriptions-item>
                <el-descriptions-item label="优惠券码">{{
                  order.couponCode || "-"
                }}</el-descriptions-item>
                <el-descriptions-item label="原价"
                  >{{
                    order.primePrice != null
                      ? "¥" + order.primePrice.toFixed(2)
                      : "-"
                  }}
                </el-descriptions-item>
                <el-descriptions-item label="实付金额">
                  <span class="price-text"
                    >{{
                      order.totalPrice != null
                        ? "¥" + order.totalPrice.toFixed(2)
                        : "-"
                    }}
                  </span>
                </el-descriptions-item>
                <el-descriptions-item label="创建时间">{{
                  formatDateTime(order.createTime)
                }}</el-descriptions-item>
                <el-descriptions-item label="更新时间">{{
                  formatDateTime(order.updateTime)
                }}</el-descriptions-item>
                <el-descriptions-item label="备注">{{
                  order.comment || "-"
                }}</el-descriptions-item>
              </el-descriptions>
            </div>
            <div
              v-if="order.bookingRecord"
              class="order-section booking-section"
            >
              <div class="section-title">✈ 航班信息</div>
              <el-descriptions :column="3" border size="small">
                <el-descriptions-item label="预订号">{{
                  order.bookingRecord.bookingNumber || "-"
                }}</el-descriptions-item>
                <el-descriptions-item label="航班批次">{{
                  order.bookingRecord.flightId || "-"
                }}</el-descriptions-item>
                <el-descriptions-item label="乘客姓名">{{
                  order.bookingRecord.name || "-"
                }}</el-descriptions-item>
                <el-descriptions-item label="出发地">{{
                  order.bookingRecord.from || "-"
                }}</el-descriptions-item>
                <el-descriptions-item label="目的地">{{
                  order.bookingRecord.to || "-"
                }}</el-descriptions-item>
                <el-descriptions-item label="出发时间">{{
                  formatDateTime(order.bookingRecord.date)
                }}</el-descriptions-item>
                <el-descriptions-item label="舱位等级">
                  <el-tag size="small" type="primary">
                    {{
                      bookingClassMap[order.bookingRecord.bookingClass] ||
                      order.bookingRecord.bookingClass ||
                      "-"
                    }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="预订状态">
                  <el-tag
                    :type="
                      bookingStatusTypeMap[
                        order.bookingRecord.bookingStatus
                      ] || 'info'
                    "
                    size="small"
                  >
                    {{
                      bookingStatusMap[order.bookingRecord.bookingStatus] ||
                      order.bookingRecord.bookingStatus ||
                      "-"
                    }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="证件后6位">{{
                  order.bookingRecord.certificateSubstring || "-"
                }}</el-descriptions-item>
              </el-descriptions>
            </div>
            <div v-if="order.coupon" class="order-section coupon-section">
              <div class="section-title">🎫 优惠券信息</div>
              <el-descriptions :column="3" border size="small">
                <el-descriptions-item label="优惠券名称">{{
                  order.coupon.couponName || "-"
                }}</el-descriptions-item>
                <el-descriptions-item label="优惠券码">{{
                  order.coupon.couponCode || "-"
                }}</el-descriptions-item>
                <el-descriptions-item label="类型">
                  <el-tag size="small" type="warning">
                    {{
                      couponTypeMap[order.coupon.couponType] ||
                      order.coupon.couponType ||
                      "-"
                    }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="面值/折扣"
                  >{{
                    order.coupon.couponPrice != null
                      ? "¥" + order.coupon.couponPrice.toFixed(2)
                      : "-"
                  }}
                </el-descriptions-item>
                <el-descriptions-item label="状态">
                  <el-tag
                    :type="order.coupon.status === 1 ? 'success' : 'info'"
                    size="small"
                  >
                    {{ order.coupon.status === 1 ? "可用" : "不可用" }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="创建时间">{{
                  formatDateTime(order.coupon.createTime)
                }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.passenger-wrapper {
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

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: rgba(10, 14, 39, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 8px;
  overflow: hidden;
}

.order-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: rgba(0, 212, 255, 0.08);
  border-bottom: 1px solid rgba(0, 212, 255, 0.1);
}

.order-code {
  font-size: 15px;
  font-weight: 600;
  color: #00d4ff;
  letter-spacing: 1px;
}

.order-card-body {
  padding: 16px 20px 8px;
}

.order-section {
  padding: 8px 20px 16px;
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