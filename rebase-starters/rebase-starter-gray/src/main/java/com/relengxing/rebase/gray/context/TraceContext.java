package com.relengxing.rebase.gray.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;

/**
 * @author relengxing
 */
@Slf4j
public class TraceContext {

    /**
     * trace标识
     */
    private static final ThreadLocal<String> traceTh = new TransmittableThreadLocal<>();

    /**
     * 获取Trace标识
     *
     * @return
     */
    public static String get() {
//        log.debug("TraceContext: 获取链路标识 {}", traceTh.get());
        return traceTh.get();
    }


    /**
     * 清理Trace标识
     */
    public static void clear() {
//        log.info("TraceContext: 清理链路标识 {}", traceTh.get());
        traceTh.remove();
    }


    /**
     * 设置trace标识
     *
     * @param traceId
     */
    public static void set(String traceId) {
//        log.info("TraceContext: 设置链路标识 {}", traceId);
        traceTh.set(traceId);
    }


}
