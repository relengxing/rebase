package com.relengxing.rebase.prometheus.prometheus.util;

import com.relengxing.rebase.prometheus.prometheus.collector.ReCollector;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Description 监控工具类
 */
@Slf4j
public class MonitorUtil {

    /**
     * 增加指标
     *
     * @param metrics
     */
    public static void addMetrics(ReCollector metrics) {
        metrics.register();
    }

    /**
     * 增加指标
     *
     * @param metrics
     */
    public static void addMetrics(List<ReCollector> metrics) {
        for (ReCollector metric : metrics) {
            metric.register();
        }
    }

}
