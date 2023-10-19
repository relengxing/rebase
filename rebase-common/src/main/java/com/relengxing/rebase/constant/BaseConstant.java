package com.relengxing.rebase.constant;

/**
 * @author relengxing
 * @date 2023-10-07 17:05
 * @Description
 **/
public class BaseConstant {

    /**
     * Nacos 注册发现版本信息 meta data Key
     */
    public static final String NACOS_VERSION_KEY = "version";

    /**
     * dubbo provider version key
     */
    public static final String DUBBO_VERSION_KEY = "provider-version";

    /**
     * dubbo provider service name
     */
    public static final String DUBBO_SERVICE_KEY = "provider-name";

    /**
     * dubbo 消费者key
     */
    public static final String DUBBO_CONSUMER_SERVICE_KEY = "consumer-name";


    public static final String GRAY_HEADER = "x-gray-tag";

    public static final String TRACE_HEADER = "x-trace-tag";

}
