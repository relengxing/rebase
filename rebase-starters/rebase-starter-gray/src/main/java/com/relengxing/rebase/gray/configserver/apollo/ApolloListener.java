package com.relengxing.rebase.gray.configserver.apollo;

import com.relengxing.rebase.gray.configserver.AbstractGrayConfigListener;
import com.relengxing.rebase.gray.configserver.GrayConfig;
import com.relengxing.rebase.gray.properties.GrayProperties;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.function.BiConsumer;


@Slf4j
public class ApolloListener extends AbstractGrayConfigListener {

//    String configInfoLocal;
//
    @PostConstruct
    public void init() {
        String namespace = "application";
//        Config config = ConfigService.getConfig(namespace);
//        config.addChangeListener(new ConfigChangeListener() {
//            @Override
//            public void onChange(ConfigChangeEvent changeEvent) {
//                Set<String> strings = changeEvent.changedKeys();
//                strings.forEach(s -> {
//                    log.info(s, changeEvent.getChange(s).getNewValue());
//                });
//            }
//        });
//        String property = config.getProperty("status", "none");
//        log.info("ApolloListener: " + property);
    }


    @Override
    public GrayProperties getGrayConfig() {
        return null;
    }
}
