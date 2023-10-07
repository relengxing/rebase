package com.relengxing.rebase.gray.properties;


import com.relengxing.rebase.gray.context.GrayContext;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class GrayProperties {

    /**
     * 装填
     */
    private Boolean status = false;

    /**
     * 默认流量
     */
    private String defaultValue = "blue";

    /**
     * 服务灰度标识
     * key: serviceId
     * value: key: code, value: version
     */
    private Map<String, Map<String, String>> list = new HashMap<>();


    /**
     * 选择服务对应的版本
     *
     * @param serviceId
     * @return
     */
    public String getVersion(String serviceId) {
        String code = GrayContext.get();
        String version;
        if (code == null) {
            // 流量未染色，选择默认版本
            code = this.getDefaultValue();
        }
        version = this.getList().get(serviceId).get(code);
        return version;
    }


}
