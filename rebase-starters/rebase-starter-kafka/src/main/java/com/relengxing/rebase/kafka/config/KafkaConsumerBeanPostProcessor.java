package com.relengxing.rebase.kafka.config;//package com.vv.finance.base.kafka.config;
//
//import com.vv.finance.base.kafka.interceptor.KafkaConsumerInterceptor;
//import com.vv.finance.base.kafka.interceptor.KafkaProducerInterceptor;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.kafka.config.KafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.ProducerFactory;
//import org.springframework.util.ReflectionUtils;
//
//import java.lang.reflect.Field;
//import java.util.List;
//import java.util.Map;
//
//public class KafkaConsumerBeanPostProcessor implements BeanPostProcessor {
//
//    @Override
//    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//
//        if (bean instanceof KafkaListenerContainerFactory) {
//            ConsumerFactory consumerFactory = (ConsumerFactory) bean;
//            try {
//                Field field = ConsumerFactory.class.getField("configs");
//                field.setAccessible(true);
//                Map<String, Object> configs = (Map<String, Object>) ReflectionUtils.getField(field, consumerFactory);
//                // 添加拦截器
//                List<String> interceptorList = (List<String>) configs.get(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG);
//                interceptorList.add(KafkaConsumerInterceptor.class.getName());
//                configs.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptorList);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return bean;
//    }
//}
