import request from '../utils/request.js';

export const getCouponListRequest = (params) => {
    return request.get('/order-server/coupon/list', { params });
}

export const addCouponRequest = (data) => {
    return request.post('/order-server/coupon/add', data);
}

export const updateCouponRequest = (data) => {
    return request.put('/order-server/coupon/update', data);
}

export const deleteCouponRequest = (ids) => {
    return request.delete(`/order-server/coupon/delete/${ids}`);
}