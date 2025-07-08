package com.jiawa.train.business.config;


// package com.jiawa.train.business.config;
//
// import org.apache.rocketmq.client.producer.DefaultMQProducer;
// import org.apache.rocketmq.spring.core.RocketMQTemplate;
// import org.springframework.context.annotation.Bean;
// import org.springframework.stereotype.Component;
//
// @Component
// public class RocketMQConfig {
//

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// }
@Configuration
public class RocketMQAdminTemplate {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    /**
     * 新版本需要声明RocketMQTemplate，否则会注入失败
     *
     * @return
     */
//    @Bean
//    public RocketMQTemplate rocketMQTemplate() {
//        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
//        DefaultMQProducer producer = new DefaultMQProducer();
//        producer.setProducerGroup("default");
//        producer.setNamesrvAddr("http://localhost:9876");
//        producer.setSendMsgTimeout(3000);
//        rocketMQTemplate.setProducer(producer);
//        return rocketMQTemplate;
//    }

    public long examinePendingMessages(String orderTopic, String consumerGroup) {



        return 0;
    }
}
