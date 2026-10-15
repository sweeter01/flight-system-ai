<template>
  <div class="app-wrapper">
    <div class="main-layout">
      <!-- 左侧菜单栏 -->
      <aside class="sidebar" :class="{ 'is-collapsed': isCollapse }">
        <div class="sidebar-header">
          <span class="sidebar-logo">✈</span>
          <span v-show="!isCollapse" class="sidebar-title">吉利航空后台</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          class="sidebar-menu"
          background-color="transparent"
          text-color="rgba(255,255,255,0.65)"
          active-text-color="#00d4ff"
          @select="handleMenuSelect"
        >
          <el-menu-item index="home">
            <el-icon><HomeFilled /></el-icon>
            <template #title><span>首页</span></template>
          </el-menu-item>

          <el-sub-menu index="flight-info-manage">
            <template #title>
              <el-icon><Promotion /></el-icon>
              <span>航班信息</span>
            </template>
            <el-menu-item index="flight">客户航班信息</el-menu-item>
            <el-menu-item v-if="hasPermission('flight:getFlight')" index="flight-detail">具体航班信息</el-menu-item>
            <el-menu-item v-if="hasPermission('plane:getPlaneInfo')" index="plane-list">客机信息</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="order-manage" class="sidebar-menu1">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>客户订单管理</span>
            </template>
            <el-menu-item v-if="hasPermission('passenger:list')" index="passenger-list">乘客信息管理</el-menu-item>
            <el-menu-item v-if="hasPermission('order:list')" index="order-list">订单信息管理</el-menu-item>
            <el-menu-item v-if="hasPermission('coupon:list')" index="coupon-list">优惠券管理</el-menu-item>
          </el-sub-menu>


          <el-sub-menu index="addr-manage" class="sidebar-menu1"> <!--菜单图标样式调整-->
            <template #title>
              <el-icon><Location /></el-icon>
              <span>运营地址信息</span>
            </template>
            <el-menu-item v-if="hasPermission('addr:listCity')" index="addr">开通城市信息</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="aircrew-manage" class="sidebar-menu1">
            <template #title>
              <el-icon><User /></el-icon>
              <span>机组信息管理</span>
            </template>
            <el-menu-item v-if="hasPermission('crew:listCrew')" index="aircrew">机组人员管理</el-menu-item>
            <el-menu-item v-if="hasPermission('crewGroup:list')" index="crew-group">人员分组管理</el-menu-item>
            <el-menu-item v-if="hasPermission('crewSchedule:list')" index="schedule">机组人员日程管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="system-manage" class="sidebar-menu1">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </template>
            <el-menu-item index="user-manage">用户管理</el-menu-item>
            <el-menu-item index="permission-manage">权限管理</el-menu-item>
          </el-sub-menu>
        </el-menu>

        <div class="sidebar-footer">
          <el-button type="danger" size="small" @click="handleLogout">
            <el-icon v-if="isCollapse"><SwitchButton /></el-icon>
            <span v-else>退出登录</span>
          </el-button>
        </div>

        <!-- 折叠/展开按钮 -->
        <div class="collapse-btn" @click="isCollapse = !isCollapse">
          <el-icon :size="14">
            <DArrowLeft v-if="!isCollapse" />
            <DArrowRight v-else />
          </el-icon>
        </div>
      </aside>

      <!-- 中间主题展示区 -->
      <main class="content-area">
        <!-- 顶部管理员信息栏 -->
        <div class="top-bar">
          <div class="top-bar-placeholder"></div>
          <div class="admin-profile" @click="profileDialogVisible = true">
            <el-avatar :size="36" class="admin-avatar">
              <span style="font-size: 16px;">{{ avatarText }}</span>
            </el-avatar>
            <span class="admin-name">{{ userInfo.username || '管理员' }}</span>
            <el-icon class="arrow-icon"><ArrowDown /></el-icon>
          </div>
        </div>

        <Intro v-if="activeMenu === 'home'" />

        <PlaneList v-else-if="activeMenu === 'plane-list'" />

        <FlightDetail v-else-if="activeMenu === 'flight-detail'" />

        <FlightBooking v-else-if="activeMenu === 'flight'" />

        <Addr v-else-if="activeMenu === 'addr'" />

        <Aircrew v-else-if="activeMenu === 'aircrew'" />

        <CrewGroup v-else-if="activeMenu === 'crew-group'" />

        <Schedule v-else-if="activeMenu === 'schedule'" />

        <Passenger v-else-if="activeMenu === 'passenger-list'" />

        <OrderList v-else-if="activeMenu === 'order-list'" />

        <CouponList v-else-if="activeMenu === 'coupon-list'" />

        <UserManage v-else-if="activeMenu === 'user-manage'" />

        <UserPermission v-else-if="activeMenu === 'permission-manage'" />

        <div v-else class="placeholder-panel">
          <el-empty description="功能开发中，敬请期待..." />
        </div>
      </main>

      <!-- 右侧 AI 助手 -->
      <div class="assistant-area" :style="{ width: assistantExpanded ? '380px' : '70px' }">
        <Assistant
          :service-prefix="assistantService"
          @booking-updated="onBookingUpdated"
          @expand-change="val => assistantExpanded = val"
        />
      </div>
    </div>

    <!-- 查看管理员信息弹窗 -->
    <el-dialog v-model="profileDialogVisible" title="个人信息" width="460px" class="profile-dialog">
      <div class="profile-header">
        <el-avatar :size="60" class="profile-avatar">
          <span style="font-size: 22px;">{{ avatarText }}</span>
        </el-avatar>
        <div class="profile-header-info">
          <h3>{{ userInfo.username }}</h3>
          <span class="profile-status" :class="{ active: userInfo.status === 1 }">
            {{ userInfo.status === 1 ? '正常' : '禁用' }}
          </span>
        </div>
      </div>

      <el-divider />

      <el-descriptions :column="1" border class="profile-descriptions">
        <el-descriptions-item label="用户ID">{{ userInfo.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userInfo.email || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ userInfo.mobile || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(userInfo.createTime) }}</el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="profileDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="openEditProfile">编辑信息</el-button>
      </template>
    </el-dialog>

    <!-- 编辑管理员信息弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑个人信息" width="460px" class="profile-dialog">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.mobile" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="editForm.password" type="password" placeholder="留空则不修改密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEditProfile">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { HomeFilled, Promotion, Document, User, DArrowLeft, DArrowRight, SwitchButton, Location, ArrowDown, Setting } from "@element-plus/icons-vue";
import { logoutRequest } from "../api/auth.js";
import { getUserMenuAndPerms } from "../api/menu.js";
import { getUserInfoRequest, updatePersonInfoRequest } from "../api/user.js";
import { getMenus, getPermissions, hasPermission, clearPermissions, setMenus, setPermissions } from "../store/permission.js";
import Intro from './intro.vue';
import PlaneList from './flight/planeList.vue';
import FlightDetail from './flight/flightDetail.vue';
import FlightBooking from './flight/flight.vue';
import Addr from './addr/addr.vue';
import Aircrew from './aircrew/aircrew.vue';
import CrewGroup from './aircrew/group.vue';
import Schedule from './aircrew/schedule.vue';
import Assistant from './Assistant.vue';
import Passenger from "./order/passenger.vue";
import OrderList from "./order/order.vue";
import CouponList from "./order/coupon.vue";
import UserManage from "./system/userManage.vue";
import UserPermission from "./system/userPermission.vue";

export default {
  name: 'Home',
  components: {
    Passenger,
    OrderList,
    CouponList,
    UserManage,
    UserPermission,
    Intro,
    PlaneList,
    FlightDetail,
    FlightBooking: FlightBooking,
    Addr,
    Aircrew,
    CrewGroup,
    Schedule,
    Assistant,
    HomeFilled,
    Promotion,
    Document,
    User,
    DArrowLeft,
    DArrowRight,
    SwitchButton,
    Location,
    ArrowDown,
    Setting,
  },
  setup() {
    const router = useRouter();
    const assistantExpanded = ref(false);
    const activeMenu = ref('home');
    const isCollapse = ref(false);
    const profileDialogVisible = ref(false);
    const editDialogVisible = ref(false);
    const userInfo = ref({});
    const editForm = ref({});

    const avatarText = computed(() => {
      const name = userInfo.value.username || '管';
      return name.charAt(0).toUpperCase();
    });

    const permissions = computed(() => getPermissions());

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

    const loadUserInfo = () => {
      getUserInfoRequest()
        .then((res) => {
          if (res.code === 200 && res.data) {
            userInfo.value = res.data;
          }
        })
        .catch((err) => {
          console.warn('获取管理员信息失败:', err);
        });
    };

    const openEditProfile = () => {
      editForm.value = {
        username: userInfo.value.username,
        email: userInfo.value.email,
        mobile: userInfo.value.mobile,
        password: '',
      };
      profileDialogVisible.value = false;
      editDialogVisible.value = true;
    };

    const submitEditProfile = () => {
      if (!editForm.value.username) {
        ElMessage.warning('用户名不能为空');
        return;
      }
      const params = { ...editForm.value };
      if (!params.password) {
        delete params.password;
      }
      updatePersonInfoRequest(params)
        .then((res) => {
          if (res.code === 200) {
            ElMessage.success('个人信息修改成功');
            editDialogVisible.value = false;
            loadUserInfo();
          } else {
            ElMessage.error(res.message || '个人信息修改失败');
          }
        })
        .catch((err) => {
          if (err && err.code === 401) return;
          ElMessage.error('修改失败');
        });
    };

    const assistantService = computed(() => {
      const menu = activeMenu.value;
      if (menu === 'addr') return 'addr';
      if (['passenger-list', 'order-list', 'coupon-list'].includes(menu)) return 'order';
      if (['flight', 'flight-detail', 'plane-list'].includes(menu)) return 'flight';
      return 'manager';
    });

    const handleMenuSelect = (index) => {
      activeMenu.value = index;
    };

    const onBookingUpdated = () => {
    };

    const restorePermissions = () => {
      const token = localStorage.getItem('accessToken');
      const perms = getPermissions();
      if (token && perms.size === 0) {
        getUserMenuAndPerms()
          .then((menuRes) => {
            if (menuRes.code === 200 && menuRes.data) {
              setMenus(menuRes.data.menuList || []);
              setPermissions(menuRes.data.authorities || []);
            }
          })
          .catch((err) => {
            console.warn('刷新后恢复权限失败:', err);
          });
      }
    };

    onMounted(() => {
      restorePermissions();
      loadUserInfo();
    });

    const handleLogout = () => {
      clearPermissions();
      logoutRequest().finally(() => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('expireIn');
        router.push('/login');
      });
    };

    return {
      assistantExpanded,
      assistantService,
      activeMenu,
      isCollapse,
      permissions,
      hasPermission,
      handleMenuSelect,
      onBookingUpdated,
      handleLogout,
      profileDialogVisible,
      editDialogVisible,
      userInfo,
      editForm,
      avatarText,
      formatDateTime,
      openEditProfile,
      submitEditProfile,
    };
  },
};
</script>

<style scoped>
.app-wrapper {
  height: 100vh;
  background: linear-gradient(135deg, #0a0e27 0%, #1a1f3a 50%, #0d1230 100%);
  overflow: hidden;
}

.main-layout {
  display: flex;
  height: 100%;
}

/* ========== 左侧菜单栏 ========== */
.sidebar {
  width: 220px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  background: rgba(10, 14, 39, 0.8);
  border-right: 1px solid rgba(0, 212, 255, 0.12);
  position: relative;
  transition: width 0.3s ease;
}

.sidebar.is-collapsed {
  width: 64px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 20px 16px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.1);
  white-space: nowrap;
  overflow: hidden;
}

.sidebar-logo {
  font-size: 24px;
  filter: drop-shadow(0 0 6px rgba(0, 212, 255, 0.8));
}

.sidebar-title {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 2px;
  background: linear-gradient(180deg, #ffffff 0%, #60d5ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sidebar-menu {
  flex: 1;
  border-right: none !important;
  overflow-y: auto;
}

.sidebar-menu::-webkit-scrollbar {
  width: 3px;
}

.sidebar-menu::-webkit-scrollbar-thumb {
  background: rgba(0, 212, 255, 0.3);
  border-radius: 2px;
}

.sidebar-menu :deep(.el-sub-menu__title) {
  border-radius: 6px;
  margin: 4px 8px;
  width: calc(100% - 16px);
  display: flex;
  align-items: center;
  position: relative;
  padding-right: 30px !important;
}

.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(0, 212, 255, 0.08) !important;
}

/*菜单箭头*/
.sidebar-menu :deep(.el-sub-menu__icon-arrow),
.sidebar-menu1 :deep(.el-sub-menu__icon-arrow) {
  position: absolute;
  right: -12px;
  top: 40%;
  transform: translateY(-50%);
  margin-top: 0 !important;
}
.sidebar-menu1 :deep(.el-sub-menu__icon-arrow) {
  right: -36px;
}

.sidebar-menu :deep(.el-sub-menu.is-opened > .el-sub-menu__title .el-sub-menu__icon-arrow) {
  transform: translateY(-50%) rotate(180deg);
}

.sidebar-menu :deep(.el-menu-item) {
  border-radius: 6px;
  margin: 5px 8px;
  width: calc(100% - 16px);
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(0, 212, 255, 0.08) !important;
}

.sidebar-menu :deep(.el-menu-item.is-active),
.sidebar-menu1 :deep(.el-menu-item.is-active) {
  background: rgba(0, 212, 255, 0.12) !important;
  border-left: 3px solid #00d4ff;
}

.sidebar-menu:not(.el-menu--collapse) :deep(.el-sub-menu__title) {
  margin: 4px 8px;
  width: calc(100% - 16px);
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(0, 212, 255, 0.1);
  text-align: center;
}

/* ========== 折叠按钮 ========== */
.collapse-btn {
  position: absolute;
  top: 50%;
  right: -12px;
  transform: translateY(-50%);
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(10, 14, 39, 0.95);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 50%;
  cursor: pointer;
  color: #00d4ff;
  z-index: 20;
  transition: all 0.3s;
}

.collapse-btn:hover {
  background: rgba(0, 212, 255, 0.15);
  border-color: #00d4ff;
  box-shadow: 0 0 10px rgba(0, 212, 255, 0.4);
}

/* ========== 顶部管理员栏 ========== */
.top-bar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 0 12px 0;
}

.top-bar-placeholder {
  flex: 1;
}

.admin-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 16px;
  border-radius: 8px;
  background: rgba(0, 212, 255, 0.06);
  border: 1px solid rgba(0, 212, 255, 0.15);
  transition: all 0.3s;
}

.admin-profile:hover {
  background: rgba(0, 212, 255, 0.12);
  border-color: rgba(0, 212, 255, 0.3);
  box-shadow: 0 0 12px rgba(0, 212, 255, 0.15);
}

.admin-avatar {
  background: linear-gradient(135deg, #0066cc, #00d4ff);
  color: #fff;
  font-weight: 600;
}

.admin-name {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  letter-spacing: 1px;
}

.arrow-icon {
  color: rgba(0, 212, 255, 0.6);
  font-size: 12px;
}

/* ========== 管理员弹窗 ========== */
.profile-header {
  display: flex;
  align-items: center;
  gap: 16px;
}

.profile-avatar {
  background: linear-gradient(135deg, #0066cc, #00d4ff);
  color: #fff;
  font-weight: 600;
}

.profile-header-info h3 {
  margin: 0 0 6px;
  font-size: 18px;
  color: #fff;
}

.profile-status {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 12px;
  background: rgba(255, 80, 80, 0.2);
  color: #ff5050;
}

.profile-status.active {
  background: rgba(0, 212, 100, 0.2);
  color: #00d464;
}

.profile-dialog :deep(.el-dialog) {
  background: #1a1f3a;
  border: 1px solid rgba(0, 212, 255, 0.2);
}

.profile-dialog :deep(.el-dialog__title) {
  color: #00d4ff;
}

.profile-dialog :deep(.el-divider) {
  border-color: rgba(0, 212, 255, 0.15);
}

.profile-dialog :deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.8);
}

.profile-dialog :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(0, 212, 255, 0.2);
  box-shadow: none;
}

.profile-dialog :deep(.el-input__inner) {
  color: #fff;
}

.profile-dialog :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4);
}

.profile-dialog :deep(.el-descriptions__body) {
  background: transparent;
}

.profile-dialog :deep(.el-descriptions__label) {
  color: rgba(255, 255, 255, 0.6);
  background: rgba(0, 212, 255, 0.05);
}

.profile-dialog :deep(.el-descriptions__content) {
  color: #fff;
  background: rgba(255, 255, 255, 0.03);
}

/* ========== 中间内容区 ========== */
.content-area {
  flex: 1;
  min-width: 0;
  overflow-y: auto;
  padding: 20px;
}

.content-area::-webkit-scrollbar {
  width: 5px;
}

.content-area::-webkit-scrollbar-thumb {
  background: rgba(0, 212, 255, 0.25);
  border-radius: 3px;
}

/* ========== 占位面板 ========== */
.placeholder-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

/* ========== 右侧 AI 助手 ========== */
.assistant-area {
  flex-shrink: 0;
  height: 100%;
  transition: width 0.3s ease;
  z-index: 10;
}
</style>