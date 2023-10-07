package com.relengxing.rebase.prometheus.prometheus.metrics;

import com.relengxing.rebase.prometheus.prometheus.collector.ReCollector;
import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description DemoMetrics 增加自定义指标项的时候请参考此结构，注意返回值不要为空
 */
public class DemoMetrics extends ReCollector {


    @Override
    public List<Collector.MetricFamilySamples> collect() {
        List<Collector.MetricFamilySamples> mfs = new ArrayList<>();
        // With no labels.
        mfs.add(new GaugeMetricFamily("my_gauge_2", "help", 42));
        // With labels
        GaugeMetricFamily labeledGauge = new GaugeMetricFamily("my_other_gauge", "help", Arrays.asList("labelname"));
        labeledGauge.addMetric(Arrays.asList("foo"), 4);
        labeledGauge.addMetric(Arrays.asList("bar"), 5);
        mfs.add(labeledGauge);

        return mfs;
    }
}
