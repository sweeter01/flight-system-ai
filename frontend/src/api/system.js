import request from '../utils/request.js';

export const getUserPageRequest = (params) => {
    return request.get('/manager-server/sys/user/page', { params });
};

export const getUserInfoByIdRequest = (id) => {
    return request.get(`/manager-server/sys/user/info/${id}`);
};

export const saveUserRequest = (data) => {
    return request.post('/manager-server/sys/user/save', data);
};

export const updateUserRequest = (data) => {
    return request.put('/manager-server/sys/user/update', data);
};

export const deleteUserRequest = (ids) => {
    return request.delete(`/manager-server/sys/user/${ids}`);
};

export const getRoleListRequest = () => {
    return request.get('/manager-server/sys/role/list');
};

export const getRolePageRequest = (params) => {
    return request.get('/manager-server/sys/role/page', { params });
};

export const getRoleInfoRequest = (id) => {
    return request.get(`/manager-server/sys/role/info/${id}`);
};

export const saveRoleRequest = (data) => {
    return request.post('/manager-server/sys/role', data);
};

export const updateRoleRequest = (data) => {
    return request.put('/manager-server/sys/role', data);
};

export const deleteRoleRequest = (ids) => {
    return request.delete('/manager-server/sys/role', { data: ids });
};

export const getAllMenuListRequest = () => {
    return request.get('/manager-server/sys/menu/table');
};