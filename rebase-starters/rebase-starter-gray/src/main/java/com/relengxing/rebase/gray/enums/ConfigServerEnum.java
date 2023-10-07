package com.relengxing.rebase.gray.enums;


/**
 * 注册中心类型枚举
 */
public enum ConfigServerEnum {

    NACOS("nacos"),

    APOLLO("apollo");

    ConfigServerEnum(String name) {
        this.name = name;
    }

    private final String name;

    public String getName() {
        return name;
    }
}
