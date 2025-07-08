package com.jiawa.train.business.service;

import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Service;

// 自定义降级处理器 (需自己实现)
@Service
public class DegradeProcessor {
    // 降级判断逻辑
    public boolean shouldDegrade(MessageExt msg) {
        // 实现业务特定的降级规则
        return true;
    }

    // 降级处理逻辑
    public void handle(MessageExt msg) {
        // 实现降级处理（如存DB/日志）
    }
}


