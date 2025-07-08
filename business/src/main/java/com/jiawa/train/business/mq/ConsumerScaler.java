package com.jiawa.train.business.mq;


import com.jiawa.train.business.config.RocketMQAdminTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



@Slf4j
@Component
@EnableScheduling
public class ConsumerScaler {
    @Autowired
    private DefaultMQPushConsumer consumer;

    @Autowired
    private RocketMQAdminTemplate adminTemplate; // RocketMQ 管理客户端

    @Scheduled(fixedRate = 30000)
    public void adjustThreads() {
        // 1. 获取当前积压量（需业务消费者组名）
        long backlog = adminTemplate.examinePendingMessages(
                "ORDER_TOPIC",
                consumer.getConsumerGroup()
        );

        // 2. 计算所需线程数
        int targetThreads = Math.min(64, Math.max(8, (int) backlog / 1000));

        // 3. 动态调整
        if (targetThreads != consumer.getConsumeThreadMin()) {
            consumer.setConsumeThreadMin(targetThreads);
            consumer.setConsumeThreadMax(targetThreads);
            log.info("线程数调整为: {}", targetThreads);
        }
    }
}
