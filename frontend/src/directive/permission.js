import { hasPermission } from '../store/permission.js';

export default {
    mounted(el, binding) {
        const { value } = binding;
        if (value) {
            const requiredPerms = Array.isArray(value) ? value : [value];
            const hasAuth = requiredPerms.some(perm => hasPermission(perm));
            if (!hasAuth) {
                el.parentNode && el.parentNode.removeChild(el);
            }
        }
    }
};