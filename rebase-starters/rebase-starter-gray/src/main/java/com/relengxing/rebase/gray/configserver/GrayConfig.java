package com.relengxing.rebase.gray.configserver;


import com.relengxing.rebase.gray.properties.GrayProperties;

import java.util.function.BiConsumer;

/**
 * 获取 灰度发布 公共配置
 *
 * @author lichao
 */
public interface GrayConfig {

    /**
     * 获取灰度配置
     *
     * @return
     */
    GrayProperties getGrayConfig();

    /**
     * 获取当前服务器的颜色
     *
     * @return
     */
    String currentColor();

    /**
     * 当前服务器是否在灰度名单
     * @return
     */
    Boolean inGrayList();

    /**
     * 注册一个事件监听器
     *
     * @param consumer <GrayProperties, GrayProperties> old, new
     */
    void registerListener(BiConsumer<GrayProperties, GrayProperties> consumer);


}
