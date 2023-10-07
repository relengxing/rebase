package com.relengxing.rebase.prometheus.prometheus;

import org.springframework.boot.CommandLineRunner;


/**
 * @Description 监控启动类
 *
 */
public class MonitorBootstrap implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // simple-client-hotspot 自带的指标
//        DefaultExports.register(AkCollectorRegistry.getAkCollectorRegistry());
        // 此处可以添加一些强制依赖的监控项
        // 例如
//        (new GarbageCollectorExports()).register(AkCollectorRegistry.getAkCollectorRegistry());
//        MonitorUtil.addMetrics(new ThreadPoolMetrics());
    }




}
