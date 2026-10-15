<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getRolePageRequest,
  getRoleInfoRequest,
  saveRoleRequest,
  updateRoleRequest,
  deleteRoleRequest,
  getAllMenuListRequest,
} from "../../api/system.js";

const roleTableData = ref([]);
const roleTotal = ref(0);
const roleDialogVisible = ref(false);
const isEdit = ref(false);
const menuTree = ref([]);
const expandedKeys = ref([]);
const roleSearchForm = ref({
  current: 1,
  size: 10,
  roleName: "",
});
const roleForm = ref({
  roleId: null,
  roleName: "",
  remark: "",
  menuIdList: [],
});
const currentRoleMenuIds = ref([]);
const treeRef = ref(null);

const typeMap = { 0: "目录", 1: "菜单", 2: "按钮" };
const typeColorMap = { 0: "", 1: "warning", 2: "success" };

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

const buildMenuTree = (list) => {
  const map = {};
  const roots = [];
  list.forEach((item) => {
    map[item.menuId] = { ...item, children: [] };
  });
  list.forEach((item) => {
    if (item.parentId === 0) {
      roots.push(map[item.menuId]);
    } else if (map[item.parentId]) {
      map[item.parentId].children.push(map[item.menuId]);
    }
  });
  return roots;
};

const getAllMenuIds = (nodes) => {
  const ids = [];
  const traverse = (list) => {
    list.forEach((node) => {
      ids.push(node.menuId);
      if (node.children && node.children.length > 0) {
        traverse(node.children);
      }
    });
  };
  traverse(nodes);
  return ids;
};

const loadRolePage = () => {
  getRolePageRequest(roleSearchForm.value)
    .then((res) => {
      if (res.code === 200 && res.data) {
        roleTableData.value = res.data.records || [];
        roleTotal.value = res.data.total || 0;
      } else {
        roleTableData.value = [];
        roleTotal.value = 0;
      }
    })
    .catch(() => {
      roleTableData.value = [];
      roleTotal.value = 0;
    });
};

const loadMenuTree = () => {
  getAllMenuListRequest()
    .then((res) => {
      if (res.code === 200 && res.data) {
        menuTree.value = buildMenuTree(res.data);
        expandedKeys.value = getAllMenuIds(menuTree.value);
      }
    })
    .catch(() => {});
};

const handleSearch = () => {
  roleSearchForm.value.current = 1;
  loadRolePage();
};

const resetSearch = () => {
  roleSearchForm.value = { current: 1, size: 10, roleName: "" };
  loadRolePage();
};

const handlePageChange = (page) => {
  roleSearchForm.value.current = page;
  loadRolePage();
};

const handleSizeChange = (size) => {
  roleSearchForm.value.size = size;
  roleSearchForm.value.current = 1;
  loadRolePage();
};

const openAddRoleDialog = () => {
  isEdit.value = false;
  roleForm.value = {
    roleId: null,
    roleName: "",
    remark: "",
    menuIdList: [],
  };
  currentRoleMenuIds.value = [];
  roleDialogVisible.value = true;
};

const openEditRoleDialog = (row) => {
  isEdit.value = true;
  getRoleInfoRequest(row.roleId)
    .then((res) => {
      if (res.code === 200 && res.data) {
        const data = res.data;
        roleForm.value = {
          roleId: data.roleId,
          roleName: data.roleName,
          remark: data.remark || "",
          menuIdList: data.menuIdList || [],
        };
        currentRoleMenuIds.value = data.menuIdList || [];
        roleDialogVisible.value = true;
      }
    })
    .catch(() => ElMessage.error("获取角色详情失败"));
};

const openPermissionDialog = (row) => {
  getRoleInfoRequest(row.roleId)
    .then((res) => {
      if (res.code === 200 && res.data) {
        const data = res.data;
        roleForm.value = {
          roleId: data.roleId,
          roleName: data.roleName,
          remark: data.remark || "",
          menuIdList: data.menuIdList || [],
        };
        currentRoleMenuIds.value = data.menuIdList || [];
        roleDialogVisible.value = true;
        isEdit.value = true;
      }
    })
    .catch(() => ElMessage.error("获取角色权限失败"));
};

const submitRole = () => {
  if (!roleForm.value.roleName) {
    ElMessage.warning("角色名称不能为空");
    return;
  }
  const checkedKeys = treeRef.value?.getCheckedKeys() || [];
  const halfChecked = treeRef.value?.getHalfCheckedKeys() || [];
  roleForm.value.menuIdList = [...checkedKeys, ...halfChecked];

  if (isEdit.value) {
    updateRoleRequest(roleForm.value)
      .then(() => {
        ElMessage.success("修改成功");
        roleDialogVisible.value = false;
        loadRolePage();
      })
      .catch(() => ElMessage.error("修改失败"));
  } else {
    saveRoleRequest(roleForm.value)
      .then(() => {
        ElMessage.success("新增成功");
        roleDialogVisible.value = false;
        loadRolePage();
      })
      .catch(() => ElMessage.error("新增失败"));
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除角色「${row.roleName}」吗？`, "提示", {
    type: "warning",
  })
    .then(() => {
      deleteRoleRequest([row.roleId])
        .then(() => {
          ElMessage.success("删除成功");
          loadRolePage();
        })
        .catch(() => ElMessage.error("删除失败"));
    })
    .catch(() => {});
};

onMounted(() => {
  loadRolePage();
  loadMenuTree();
});
</script>

<template>
  <div class="permission-wrapper">
    <div class="main-content">
      <div class="search-bar">
        <el-form :inline="true" :model="roleSearchForm" class="search-form">
          <el-form-item label="角色名称">
            <el-input
              v-model="roleSearchForm.roleName"
              placeholder="请输入角色名称"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
            <el-button type="success" @click="openAddRoleDialog"
              >新增角色</el-button
            >
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="roleTableData" stripe style="width: 100%">
        <el-table-column prop="roleId" label="ID" width="70" />
        <el-table-column prop="roleName" label="角色名称" width="160" />
        <el-table-column prop="remark" label="备注" min-width="200" />
        <el-table-column label="创建时间" min-width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="openEditRoleDialog(scope.row)"
              >编辑</el-button
            >
            <el-button
              size="small"
              type="warning"
              @click="openPermissionDialog(scope.row)"
              >权限</el-button
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
          :total="roleTotal"
          :current-page="roleSearchForm.current"
          :page-size="roleSearchForm.size"
          :page-sizes="[10, 20, 50]"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <el-dialog
      v-model="roleDialogVisible"
      :title="isEdit ? '编辑角色 & 权限设置' : '新增角色 & 权限设置'"
      width="600px"
      @opened="() => {
        if (treeRef && currentRoleMenuIds.length > 0) {
          treeRef.setCheckedKeys(currentRoleMenuIds);
        }
      }"
    >
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="角色名称">
          <el-input
            v-model="roleForm.roleName"
            placeholder="请输入角色名称"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="roleForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
        <el-form-item label="权限设置">
          <div class="tree-container">
            <el-tree
              ref="treeRef"
              :data="menuTree"
              show-checkbox
              node-key="menuId"
              :default-expanded-keys="expandedKeys"
              :props="{
                label: 'name',
                children: 'children',
              }"
            >
              <template #default="{ node, data }">
                <span class="custom-tree-node">
                  <el-tag
                    :type="typeColorMap[data.type] || 'info'"
                    size="small"
                    style="margin-right: 6px"
                  >
                    {{ typeMap[data.type] || "未知" }}
                  </el-tag>
                  <span>{{ data.name }}</span>
                  <span
                    v-if="data.perms"
                    class="perms-text"
                    :title="data.perms"
                  >
                    ({{ data.perms }})
                  </span>
                </span>
              </template>
            </el-tree>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRole">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.permission-wrapper {
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

.tree-container {
  width: 100%;
  max-height: 360px;
  overflow-y: auto;
  padding: 8px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 6px;
}

.custom-tree-node {
  display: flex;
  align-items: center;
  font-size: 13px;
}

.perms-text {
  margin-left: 8px;
  color: rgba(0, 212, 255, 0.5);
  font-size: 11px;
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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

:deep(.el-dialog .el-textarea__inner) {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(0, 212, 255, 0.2);
  color: #fff;
}

:deep(.el-tree) {
  background: transparent;
  --el-tree-text-color: rgba(255, 255, 255, 0.85);
  --el-tree-node-hover-bg-color: rgba(0, 212, 255, 0.08);
}

:deep(.el-tree-node__content:hover) {
  background: rgba(0, 212, 255, 0.08);
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