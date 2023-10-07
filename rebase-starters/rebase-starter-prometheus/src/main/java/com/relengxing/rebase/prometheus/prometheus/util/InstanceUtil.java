package com.relengxing.rebase.prometheus.prometheus.util;

import cn.hutool.extra.spring.SpringUtil;
import com.relengxing.rebase.utils.IpUtil;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * @Description
 */
public class InstanceUtil {

    /**
     * ip+port
     *
     * @return
     */
    public static String getInstance() {
        String ip = IpUtil.getLocalIP();
        if (StringUtils.hasText(ip)) {
            return null;
        }
        String port = SpringUtil.getBean(Environment.class).getProperty("server.port");
        return ip + ":" + port;
    }

}
