package com.relengxing.rebase.kafka.config;

import com.relengxing.rebase.gray.EnableGray;
import com.relengxing.rebase.gray.configserver.GrayConfig;
import com.relengxing.rebase.kafka.constant.KafkaConstant;
import com.relengxing.rebase.kafka.interceptor.KafkaConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(annotation = EnableGray.class)
public class ReKafkaConfig {


//    @Bean
//    public KafkaGrayConsumerContainer kafkaGrayConsumerContainer() {
//        return new KafkaGrayConsumerContainer();
//    }


    @Bean
    @Qualifier(value = "kafkaListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            KafkaProperties properties,
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ReConcurrentKafkaListenerContainerFactory<>();
        factory.setRecordFilterStrategy(new GrayRecordFilterStrategy(KafkaConstant.INTERNAL));

        // 设置拦截器
        properties.getProperties().put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, KafkaConsumerInterceptor.class.getName());

        configurer.configure(factory, kafkaConsumerFactory
                .getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties())));
        return factory;
    }
}
