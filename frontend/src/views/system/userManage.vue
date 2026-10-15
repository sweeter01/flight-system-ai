<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getUserPageRequest,
  saveUserRequest,
  updateUserRequest,
  deleteUserRequest,
  getRoleListRequest,
} from "../../api/system.js";

const tableData = ref([]);
const total = ref(0);
const dialogVisible = ref(false);
const isEdit = ref(false);
const roleOptions = ref([]);
const searchForm = ref({
  current: 1,
  size: 10,
  username: "",
});
const userForm = ref({
  userId: null,
  username: "",
  password: "",
  email: "",
  mobile: "",
  status: 1,
  roleIdList: [],
});

const statusMap = { 0: "禁用", 1: "正常" };
const statusTypeMap = { 0: "danger", 1: "success" };

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

const loadUserPage = () => {
  getUserPageRequest(searchForm.value)
    .then((res) => {
      if (res.code === 200 && res.data) {
        tableData.value = res.data.records || [];
        total.value = res.data.total || 0;
      } else {
        tableData.value = [];
        total.value = 0;
      }
    })
    .catch(() => {
      tableData.value = [];
      total.value = 0;
    });
};

const loadRoleOptions = () => {
  getRoleListRequest()
    .then((res) => {
      if (res.code === 200 && res.data) {
        roleOptions.value = res.data || [];
      }
    })
    .catch(() => {});
};

const handleSearch = () => {
  searchForm.value.current = 1;
  loadUserPage();
};

const resetSearch = () => {
  searchForm.value = { current: 1, size: 10, username: "" };
  loadUserPage();
};

const handlePageChange = (page) => {
  searchForm.value.current = page;
  loadUserPage();
};

const handleSizeChange = (size) => {
  searchForm.value.size = size;
  searchForm.value.current = 1;
  loadUserPage();
};

const openAddDialog = () => {
  isEdit.value = false;
  userForm.value = {
    userId: null,
    username: "",
    password: "",
    email: "",
    mobile: "",
    status: 1,
    roleIdList: [],
  };
  dialogVisible.value = true;
};

const openEditDialog = (row) => {
  isEdit.value = true;
  userForm.value = {
    userId: row.userId,
    username: row.username,
    password: "",
    email: row.email || "",
    mobile: row.mobile || "",
    status: row.status,
    roleIdList: row.roleIdList || [],
  };
  dialogVisible.value = true;
};

const submitUser = () => {
  if (!userForm.value.username) {
    ElMessage.warning("用户名不能为空");
    return;
  }
  if (!isEdit.value && !userForm.value.password) {
    ElMessage.warning("密码不能为空");
    return;
  }
  if (isEdit.value) {
    updateUserRequest(userForm.value)
      .then(() => {
        ElMessage.success("修改成功");
        dialogVisible.value = false;
        loadUserPage();
      })
      .catch(() => ElMessage.error("修改失败"));
  } else {
    saveUserRequest(userForm.value)
      .then(() => {
        ElMessage.success("新增成功");
        dialogVisible.value = false;
        loadUserPage();
      })
      .catch(() => ElMessage.error("新增失败"));
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除用户「${row.username}」吗？`, "提示", {
    type: "warning",
  })
    .then(() => {
      deleteUserRequest(row.userId)
        .then(() => {
          ElMessage.success("删除成功");
          loadUserPage();
        })
        .catch(() => ElMessage.error("删除失败"));
    })
    .catch(() => {});
};

onMounted(() => {
  loadUserPage();
  loadRoleOptions();
});
</script>

<template>
  <div class="user-wrapper">
    <div class="main-content">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
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
              >新增管理员</el-button
            >
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="userId" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="mobile" label="手机号" width="140" />
        <el-table-column label="状态" width="90">
          <template #default="scope">
            <el-tag
              :type="statusTypeMap[scope.row.status] || 'info'"
              size="small"
            >
              {{ statusMap[scope.row.status] || "未知" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
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

      <div class="pagination-bar">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :current-page="searchForm.current"
          :page-size="searchForm.size"
          :page-sizes="[10, 20, 50]"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑管理员' : '新增管理员'"
      width="500px"
    >
      <el-form :model="userForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input
            v-model="userForm.username"
            placeholder="请输入用户名"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="userForm.password"
            type="password"
            :placeholder="isEdit ? '留空则不修改' : '请输入密码'"
            show-password
          />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="userForm.mobile" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="userForm.status" style="width: 100%">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色">
          <el-select
            v-model="userForm.roleIdList"
            multiple
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option
              v-for="role in roleOptions"
              :key="role.roleId"
              :label="role.remark"
              :value="role.roleId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUser">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.user-wrapper {
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

.pagination-bar {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
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
</style>