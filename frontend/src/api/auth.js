import request from '../utils/request.js';

export const loginRequest = (data) => {
    const params = new URLSearchParams()
    for (const key in data) {
        params.append(key, data[key])
    }
    return request.post('/auth-server/doLogin', params, {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'loginType': 'sysUserLogin'
        }
    });
}

export const logoutRequest = () => {
    const token = localStorage.getItem('accessToken');
    return request.post('/auth-server/doLogout', null, {
        headers: {
            'Authorization': 'bearer ' + token
        }
    });
}
