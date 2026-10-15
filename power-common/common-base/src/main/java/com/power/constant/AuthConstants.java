package com.power.constant;

/**
 * 认证授权常量类
 */
public interface AuthConstants {

    /**
     * 在请求头中存放token值的KEY
     */
    String AUTHORIZATION = "Authorization";

    /**
     * token值的前缀
     */
    String BEARER = "bearer ";

    /**
     * token值存放在redis中的前缀
     */
    String LOGIN_TOKEN_PREFIX = "login_token:";


    /**
     * 登录URL
     */
    String LOGIN_URL = "/doLogin";

    /**
     * 登出URL
     */
    String LOGOUT_URL = "/doLogout";

    /**
     * 登录类型
     */
    String LOGIN_TYPE = "loginType";

    /**
     * 登录类型值：商城后台管理系统用户
     */
    String SYS_USER_LOGIN = "sysUserLogin";

//    /**
//     * 登录类型值：预定航班用户
//     */
//    String MEMBER_LOGIN = "memberLogin";


    /**
     * TOKEN有效时长（单位：秒，4个小时）
     */
    Long TOKEN_TIME = 14400L;

    /**
     * TOKEN的阈值：3600秒（1个小时）
     */
    Long TOKEN_EXPIRE_THRESHOLD_TIME = 60*60L;
}
