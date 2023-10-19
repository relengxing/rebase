package com.relengxing.rebase.context;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Map;

/**
 * 需要透传的上下文
 * 一般源头是 Http 请求的 header。
 * 设置进这个map，跨服务调用时会传递后后面的服务
 */
public class PassThroughHolder {

    private static final ThreadLocal<Map<String, String>> transTh = new TransmittableThreadLocal<>();


    public static void clear() {
        transTh.get().clear();
    }


    public static void put(String key, String value) {
        transTh.get().put(key, value);
    }

    public static String get(String key) {
        return transTh.get().get(key);
    }

    public static Map<String, String> getAll() {
        return transTh.get();
    }


}
