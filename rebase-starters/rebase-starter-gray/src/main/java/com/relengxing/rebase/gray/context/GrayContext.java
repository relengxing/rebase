package com.relengxing.rebase.gray.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;

/**
 * @author relengxing
 */
@Slf4j
public class GrayContext {

    /**
     * 是否开启灰度
     */
    private static final ThreadLocal<String> grayTh = new TransmittableThreadLocal<>();

    /**
     * 获取灰度标识
     *
     * @return
     */
    public static String get() {
//        log.debug("GrayContext: 获取灰度标识 {}", grayTh.get());
        return grayTh.get();
    }


    /**
     * 清理灰度标识
     */
    public static void clear() {
//        log.debug("GrayContext: 清理灰度标识 {}", grayTh.get());
        grayTh.remove();
    }


    /**
     * 设置灰度标识
     *
     * @param gray
     */
    public static void set(String gray) {
//        log.debug("GrayContext: 设置灰度标识 {}", gray);
        grayTh.set(gray);
    }


}
