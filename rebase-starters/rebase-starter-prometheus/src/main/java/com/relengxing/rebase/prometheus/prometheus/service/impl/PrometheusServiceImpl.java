//package com.relengxing.rebase.prometheus.prometheus.service.impl;
//
//import com.relengxing.rebase.prometheus.prometheus.collector.ReCollectorRegistry;
//import com.relengxing.rebase.prometheus.prometheus.service.PrometheusService;
//import io.prometheus.client.Collector;
//import io.prometheus.client.exporter.common.TextFormat;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.Enumeration;
//
///**
// * @Description
// *
// */
//@Service
//@Slf4j
//public class PrometheusServiceImpl implements PrometheusService {
//
//
//    /**
//     * 这是接口调用的方法
//     *
//     * @param response
//     * @throws IOException
//     */
//    @Override
//    public void metrics(HttpServletResponse response) throws IOException {
//        Enumeration<Collector.MetricFamilySamples> metricFamilySamplesEnumeration = ReCollectorRegistry.getReCollectorRegistry().metricFamilySamples();
//        TextFormat.write004(response.getWriter(), metricFamilySamplesEnumeration);
//    }
//
//
//}
