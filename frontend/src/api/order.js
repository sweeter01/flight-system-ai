import request from '../utils/request.js';

export const getOrderListRequest = (params) => {
    return request.get('/order-server/order/list', { params });
}

export const getOrderDetailRequest = (id) => {
    return request.get('/order-server/order/order/detail', { params: { id } });
}

export const updateOrderRequest = (data) => {
    return request.put('/order-server/order/update', data);
}

export const deleteOrderRequest = (ids) => {
    return request.delete(`/order-server/order/delete/${ids}`);
}

export const cancelOrderRequest = (bookingNumbers) => {
    return request.delete('/order-server/order/cancelOrder', { params: { bookingNumbers } });
}