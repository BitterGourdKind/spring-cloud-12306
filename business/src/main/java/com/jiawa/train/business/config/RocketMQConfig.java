package com.jiawa.train.business.config;

import com.jiawa.train.business.mq.OrderMessageListener;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RocketMQConfig {
    @Value("${rocketmq.consumer.group}")
    private String consumerGroup;

    @Value("${rocketmq.namesrv-addr}")
    private String namesrvAddr;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DefaultMQPushConsumer orderConsumer(
            OrderMessageListener messageListener // 包含降级逻辑的监听器
    ) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.subscribe("CONFIRM_ORDER", "*");

        // 关键：注入包含降级逻辑的监听器
        consumer.registerMessageListener(messageListener);

        // 初始线程设置
        consumer.setConsumeThreadMin(8);
        consumer.setConsumeThreadMax(32);
        return consumer;
    }
}