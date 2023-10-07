package com.relengxing.rebase.kafka.config;

import com.relengxing.rebase.kafka.interceptor.KafkaProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class KafkaProducerBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof ProducerFactory) {
            ProducerFactory producerFactory = (ProducerFactory) bean;
            try {
                Field field = ProducerFactory.class.getField("configs");
                field.setAccessible(true);
                Map<String, Object> configs = (Map<String, Object>) ReflectionUtils.getField(field, producerFactory);
                // 添加拦截器
                List<String> interceptorList = (List<String>) configs.get(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG);
                interceptorList.add(KafkaProducerInterceptor.class.getName());
                configs.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptorList);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return bean;
    }
}
