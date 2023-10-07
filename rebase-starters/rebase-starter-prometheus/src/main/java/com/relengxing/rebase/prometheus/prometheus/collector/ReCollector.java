package com.relengxing.rebase.prometheus.prometheus.collector;

import cn.hutool.extra.spring.SpringUtil;
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;

/**
 */
public abstract class ReCollector extends Collector {

    /**
     * 注册到自己的注册器
     *
     * @param <T>
     * @return
     */
    @Override
    public <T extends Collector> T register() {
        try {
            // 注册到内部注册器
            register(SpringUtil.getBean(CollectorRegistry.class));
        } catch (Exception e) {

        }
        return register(ReCollectorRegistry.getReCollectorRegistry());
    }

}
