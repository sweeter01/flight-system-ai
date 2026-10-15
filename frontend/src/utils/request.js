import axios from 'axios';
import { ElMessage } from "element-plus";
import router from "../router/index.js";

const instance = axios.create();

instance.interceptors.request.use(
    config => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers.Authorization = 'bearer ' + token;
        }
        return config;
    },
    err => {
        ElMessage.error(err.message || "服务异常了！");
        return Promise.reject(err);
    }
);

const handleUnauthorized = () => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        ElMessage.error("登录已过期，请重新登录！");
    }
    localStorage.removeItem('accessToken');
    localStorage.removeItem('expireIn');
    if (window.location.pathname !== '/login') {
        router.push('/login');
    }
};

const isLoginRequest = (config) => {
    return config && config.url && config.url.includes('/auth-server/doLogin');
};

instance.interceptors.response.use(
    result => {
        if (result.data.code === 200) {
            return result.data;
        } else if (result.data.code === 401) {
            if (isLoginRequest(result.config)) {
                return Promise.reject(result.data);
            }
            handleUnauthorized();
            return Promise.reject(result.data);
        } else {
            const msg = result.data.message || '操作失败';
            ElMessage.error(msg);
            return Promise.reject(result.data);
        }
    },
    err => {
        if (err.response && err.response.status === 401) {
            if (isLoginRequest(err.config)) {
                return Promise.reject(err);
            }
            handleUnauthorized();
            return Promise.reject(err);
        } else if (err.response && err.response.status === 403) {
            const msg = err.response.data?.message || '没有该操作权限，请联系管理员';
            ElMessage.error(msg);
            return Promise.reject(err.response.data || err);
        } else {
            const msg = err.response?.data?.message || '服务器异常，请联系管理员！';
            ElMessage.error(msg);
            return Promise.reject(err);
        }
    }
);

export default instance;