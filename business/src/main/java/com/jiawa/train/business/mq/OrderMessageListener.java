package com.jiawa.train.business.mq;

import com.jiawa.train.business.service.DegradeProcessor;
import com.jiawa.train.business.service.OrderService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMessageListener implements MessageListenerConcurrently {

    @Autowired
    private DegradeProcessor degradeProcessor;

    @Autowired
    private OrderService orderService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(
            List<MessageExt> msgs,
            ConsumeConcurrentlyContext context
    ) {
        for (MessageExt msg : msgs) {
            // 降级判断（业务逻辑耦合点）
            if (degradeProcessor.shouldDegrade(msg)) {
                degradeProcessor.handle(msg);
                continue;
            }

//            // 正常业务处理
//            OrderDTO order = parseOrder(msg.getBody());
//            orderService.process(order);
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
