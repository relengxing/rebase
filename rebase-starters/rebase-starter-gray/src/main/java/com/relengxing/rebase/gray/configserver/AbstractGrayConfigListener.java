package com.relengxing.rebase.gray.configserver;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.setting.yaml.YamlUtil;
import com.relengxing.rebase.gray.properties.GrayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;


@Slf4j
public abstract class AbstractGrayConfigListener implements GrayConfig {

    /**
     * 原始字符串
     */
    protected String configInfoLocal;

    /**
     * 转换后的对象
     */
    protected GrayProperties oldGrayProperties;

    protected GrayProperties grayProperties;

    @Autowired
    public BuildProperties buildProperties;

    final List<BiConsumer<GrayProperties, GrayProperties>> consumerList = new ArrayList<>();

    @Override
    public GrayProperties getGrayConfig() {
        return grayProperties;
    }


    @Override
    public String currentColor() {
        String name = SpringUtil.getProperty("spring.application.name").trim();
        String version = buildProperties.getVersion();
        Map<String, String> colorVersionMap = this.grayProperties.getList().get(name);
        if (colorVersionMap != null) {
            for (Map.Entry<String, String> entry : colorVersionMap.entrySet()) {
                if (entry.getValue().equals(version)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    @Override
    public Boolean inGrayList() {
        return this.grayProperties.inGrayList();
    }

    @Override
    public void registerListener(BiConsumer<GrayProperties, GrayProperties> consumer) {
        synchronized (consumerList) {
            consumerList.add(consumer);
        }
    }


    protected void load() {
        if (configInfoLocal == null) {
            // 没有配置则认为没有打开蓝绿
            grayProperties = new GrayProperties();
            grayProperties.setStatus(false);
            return;
        }
        try {
            oldGrayProperties = grayProperties;
            grayProperties = YamlUtil.load(new ByteArrayInputStream(configInfoLocal.getBytes()), GrayProperties.class);
            eventPublisher(oldGrayProperties, grayProperties);
        } catch (Exception e) {
            log.error("蓝绿配置文件解析错误，无法开启，请联系运维");
        }
    }

    /**
     * 发布事件
     *
     * @param oldGrayProperties
     * @param grayProperties
     */
    private void eventPublisher(GrayProperties oldGrayProperties, GrayProperties grayProperties) {
        this.consumerList.forEach(consumer -> consumer.accept(oldGrayProperties, grayProperties));
    }
}
