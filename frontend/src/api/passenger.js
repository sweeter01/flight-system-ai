import request from '../utils/request.js';

export const getPassengerListRequest = (params) => {
    return request.get('/order-server/passenger/list', { params });
}

export const getPassengerOrderVoRequest = (passengerId) => {
    return request.get('/order-server/passenger/orderVoList', { params: { passengerId } });
}

export const addPassengerRequest = (data) => {
    return request.post('/order-server/passenger/add', data);
}

export const updatePassengerRequest = (data) => {
    return request.put('/order-server/passenger/update', data);
}

export const deletePassengerRequest = (passengerId) => {
    return request.delete('/order-server/passenger/delete', { params: { passengerId } });
}