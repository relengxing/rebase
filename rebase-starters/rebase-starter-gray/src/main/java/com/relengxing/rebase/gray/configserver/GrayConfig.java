package com.relengxing.rebase.gray.configserver;


import com.relengxing.rebase.gray.properties.GrayProperties;

/**
 * 获取 灰度发布 公共配置
 * @author relengxing
 */
public interface GrayConfig {

    /**
     * 获取灰度配置
     *
     * @return
     */
    GrayProperties getGrayConfig();

}
