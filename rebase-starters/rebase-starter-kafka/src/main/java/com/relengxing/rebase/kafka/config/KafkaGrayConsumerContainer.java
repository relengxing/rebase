package com.relengxing.rebase.kafka.config;

import cn.hutool.extra.spring.SpringUtil;
import com.relengxing.rebase.gray.configserver.GrayConfig;
import com.relengxing.rebase.kafka.constant.KafkaConstant;
import com.relengxing.rebase.kafka.interceptor.KafkaConsumerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


@Configuration(proxyBeanMethods = false)
@Slf4j
public class KafkaGrayConsumerContainer implements CommandLineRunner {

    private final KafkaListenerEndpointRegistrar grayRegistrar;

    private final KafkaListenerEndpointRegistry internalKafkaListenerEndpointRegistry;


//    public static final String GRAY_KAFKA_LISTENER_ENDPOINT_REGISTRY =
//            "grayKafkaListenerEndpointRegistry";

    private final ReConcurrentKafkaListenerContainerFactory<Object, Object> grayReConcurrentKafkaListenerContainerFactory;

    private final GrayConfig grayConfig;

    private static final AtomicBoolean grayEndPointInitialized = new AtomicBoolean(false);
    private static final AtomicBoolean grayContainerStarted = new AtomicBoolean(false);
    private static final AtomicBoolean internalContainerStarted = new AtomicBoolean(false);


    public KafkaGrayConsumerContainer(KafkaProperties properties,
                                      ConcurrentKafkaListenerContainerFactoryConfigurer configurer) {
        grayConfig = SpringUtil.getBean(GrayConfig.class);
        // 内部容器
        internalKafkaListenerEndpointRegistry = SpringUtil.getBean(KafkaListenerConfigUtils.KAFKA_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME, KafkaListenerEndpointRegistry.class);
        this.grayRegistrar = new KafkaListenerEndpointRegistrar();
        KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry = new KafkaListenerEndpointRegistry();
        kafkaListenerEndpointRegistry.setApplicationContext(SpringUtil.getApplicationContext());
        this.grayRegistrar.setEndpointRegistry(kafkaListenerEndpointRegistry);
        this.grayReConcurrentKafkaListenerContainerFactory = new ReConcurrentKafkaListenerContainerFactory<>();
        grayReConcurrentKafkaListenerContainerFactory.setRecordFilterStrategy(new GrayRecordFilterStrategy(KafkaConstant.GRAY));
        // 设置拦截器
        properties.getProperties().put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, KafkaConsumerInterceptor.class.getName());
        // 从最新的消息开始消费
        properties.getConsumer().setAutoOffsetReset("latest");
        // 设置启动方式
        configurer.configure(grayReConcurrentKafkaListenerContainerFactory, new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties()));
    }

    public void initialize() {
        // 注册灰度endpoint
        registerGrayEndPoint();
        // 内部容器
        // 判断本机是否是默认版本，是则启动容器
        if (grayConfig.getGrayConfig().getStatus().equals(Boolean.FALSE)) {
            // 未开启灰度, 内部容器启动
            startInternal();
        } else {
            if (Boolean.TRUE.equals(grayConfig.inGrayList())) {
                // 在灰度列表
                if (grayConfig.getGrayConfig().getDefaultValue().equals(grayConfig.currentColor())) {
                    // 开启灰度，且是默认集群
                    startInternal();
                }
                startGray();
            } else {
                // 不在灰度列表
                startInternal();
            }
        }
    }


    @Override
    public void run(String... args) throws Exception {
        initialize();
        // 注册一个灰度事件监听器
        grayConfig.registerListener((oldGrayProperties, newGrayProperties) -> {
            if (oldGrayProperties == null) {
                // 首次启动，直接返回
                return;
            } else {
                if (newGrayProperties.getStatus().equals(Boolean.TRUE)) {
                    if (newGrayProperties.getDefaultValue().equals(grayConfig.currentColor())) {
                        startInternal();
                    } else {
                        stopInternal();
                    }
                    if (Boolean.TRUE.equals(grayConfig.inGrayList())) {
                        startGray();
                    }else {
                        stopGray();
                    }
                } else {
                    stopGray();
                }
            }
        });
    }

    public void registerGrayEndPoint() {
        ReConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory = SpringUtil.getBean("kafkaListenerContainerFactory", ReConcurrentKafkaListenerContainerFactory.class);
        List<KafkaListenerEndpoint> endpointList = kafkaListenerContainerFactory.getEndpointList();
        endpointList.forEach(endpoint -> {
            AbstractKafkaListenerEndpoint<?, ?> absEndPoint = (AbstractKafkaListenerEndpoint<?, ?>) endpoint;
            // 修改 group.id 和 id
            absEndPoint.setId(KafkaConstant.GRAY_PREFIX + grayConfig.currentColor() + "_" + absEndPoint.getId());
            absEndPoint.setGroupId(KafkaConstant.GRAY_PREFIX + grayConfig.currentColor() + "_" + absEndPoint.getGroupId());
            // 重新注册
            this.grayRegistrar.registerEndpoint(absEndPoint, grayReConcurrentKafkaListenerContainerFactory);
        });
    }


    /**
     * 启动内部容器
     */
    public void startInternal() {
        if (internalContainerStarted.compareAndSet(false, true)) {
            this.internalKafkaListenerEndpointRegistry.getListenerContainers().forEach(Lifecycle::start);
            log.info("KafkaGrayConsumerContainer startInternal");
        }
    }

    /**
     * 关闭内部容器
     */
    public void stopInternal() {
        if (internalContainerStarted.compareAndSet(true, false)) {
            log.info("KafkaGrayConsumerContainer stopInternal");
            this.internalKafkaListenerEndpointRegistry.getListenerContainers().forEach(Lifecycle::stop);
        }
    }

    /**
     * 启动灰度容器
     */
    public void startGray() {
        if (grayContainerStarted.compareAndSet(false, true)) {

            log.info("KafkaGrayConsumerContainer startGray");
            if (grayEndPointInitialized.compareAndSet(false, true)) {
                this.grayRegistrar.afterPropertiesSet();
            }
            this.grayRegistrar.getEndpointRegistry().getListenerContainers().forEach(Lifecycle::start);
        }
    }


    /**
     * 关闭灰度容器
     */
    public void stopGray() {
        if (grayContainerStarted.compareAndSet(true, false)) {
            log.info("KafkaGrayConsumerContainer stopGray");
            this.grayRegistrar.getEndpointRegistry().getListenerContainers().forEach(Lifecycle::stop);
        }
    }


}
