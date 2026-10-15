package com.power.constant;

/**
 * 业务响应状态码枚举类
 */
public enum BusinessEnum {


    OPERATION_FAIL(-1,"操作失败"),
    SERVER_INNER_ERROR(9999,"服务内部异常"),
    UN_AUTHORIZATION(401,"未授权"),
    ACCESS_DENY_FAIL(403,"没有该操作权限，请联系管理员");

    BusinessEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;

    private String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


}