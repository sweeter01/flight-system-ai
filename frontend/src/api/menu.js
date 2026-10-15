import request from '../utils/request.js';

export const getUserMenuAndPerms = () => {
    return request.get('/manager-server/sys/menu/nav');
};