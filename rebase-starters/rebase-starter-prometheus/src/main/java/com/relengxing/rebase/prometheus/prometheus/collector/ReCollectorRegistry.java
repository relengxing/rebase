package com.relengxing.rebase.prometheus.prometheus.collector;

import io.prometheus.client.CollectorRegistry;

/**
 * @Description
 */
public class ReCollectorRegistry extends CollectorRegistry {

    /**
     * 自定义监控使用的注册器，和 prometheus 默认的注册器分开
     */
    private static final CollectorRegistry reCollectorRegistry = new CollectorRegistry(true);

    public static CollectorRegistry getReCollectorRegistry(){
        return reCollectorRegistry;
    }


}
