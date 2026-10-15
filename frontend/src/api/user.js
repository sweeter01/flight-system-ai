// D:\FromGItHub\Flight-demo\bussiness-server\flight-booking\frontend\src\api\user.js
import request from '../utils/request.js';

export const getUserInfoRequest = () => {
    return request.get('/manager-server/sys/user/info');
};

export const updatePersonInfoRequest = (data) => {
    return request.put('/manager-server/sys/user/person', data);
};
