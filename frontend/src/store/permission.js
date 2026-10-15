import { ref } from 'vue';

const menuList = ref([]);
const permissions = ref(new Set());

export function setMenus(menus) {
    menuList.value = menus || [];
}

export function setPermissions(perms) {
    permissions.value = new Set(perms || []);
}

export function getMenus() {
    return menuList.value;
}

export function getPermissions() {
    return permissions.value;
}

export function hasPermission(permission) {
    return permissions.value.has(permission);
}

export function clearPermissions() {
    menuList.value = [];
    permissions.value = new Set();
}