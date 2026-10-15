package com.power.enums;

public enum PayEnums {
    ALIPAY("支付宝"),
    WECHAT("微信"),
    CREDIT("信用卡"),
    DEBIT("借记卡");

    private final String name;

    PayEnums(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PayEnums fromValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (PayEnums e : values()) {
            if (e.name().equalsIgnoreCase(value) || e.name.equals(value)) {
                return e;
            }
        }
        return null;
    }
}