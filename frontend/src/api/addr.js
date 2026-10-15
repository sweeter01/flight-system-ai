// D:\FromGItHub\Flight-demo\bussiness-server\flight-booking\frontend\src\api\addr.js
import request from '../utils/request.js';

export const getCityListRequest = (params) => {
    return request.get('/addr-server/addr/listCity', { params });
}

export const addCityRequest = (params) => {
    return request.post('/addr-server/addr/addCity', null, { params });
}

export const updateCityRequest = (params) => {
    return request.put('/addr-server/addr/updateCity', null, { params });
}

export const deleteCityRequest = (id) => {
    return request.delete('/addr-server/addr/deleteCity', { params: { id } });
}
