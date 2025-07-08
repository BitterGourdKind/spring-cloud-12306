package com.jiawa.train.business.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static java.util.concurrent.TimeUnit.HOURS;

@Service
public class IdempotencyService {
    private RedisTemplate<String, String> redisTemplate;

    // 业务幂等校验（与订单系统耦合）
    public boolean checkAndMarkProcessed(String orderId) {
        String key = "ORDER_IDEMPOTENCY:" + orderId;
        return Boolean.FALSE.equals(
                redisTemplate.opsForValue().setIfAbsent(key, "1", 24, HOURS)
        );
    }



//    // 在业务服务中使用
//    @Transactional
//    public void processOrder(OrderDTO order) {
//        if (idempotencyService.checkAndMarkProcessed(order.getId())) {
//            log.warn("订单重复: {}", order.getId());
//            return;
//        }
//        // 正常处理...
//    }
}

