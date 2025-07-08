package com.jiawa.train.business.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.jiawa.train.business.req.DailyTrainTicketQueryReq;
import com.jiawa.train.business.resp.ConfirmOrderQueryResp;
import com.jiawa.train.business.resp.DailyTrainTicketQueryResp;
import com.jiawa.train.business.service.DailyTrainTicketService;
import com.jiawa.train.common.resp.CommonResp;
import com.jiawa.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/daily-train-ticket")
public class DailyTrainTicketController {
    @Autowired
    Cache<String, Object> caffeineCache;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq req) {
        //构建缓存的key
        String requestCondition = JSONUtil.toJsonStr(req);
        String hashKey = DigestUtils.md5DigestAsHex(requestCondition.getBytes());
        String cachekey = String.format("spring-cloud-12306:%s", hashKey);

        //查询本地缓存
        Object localCache = caffeineCache.getIfPresent(cachekey);
        if(localCache != null){
            return new CommonResp<>((PageResp<DailyTrainTicketQueryResp>)localCache);
        }
        //查询redis缓存
        Object redisCache = redisTemplate.opsForValue().get(cachekey);
        if(redisCache != null){
            //设置本地缓存
            caffeineCache.put(cachekey,redisCache);
            return new CommonResp<>((PageResp<DailyTrainTicketQueryResp>)redisCache);
        }
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList(req);
        //随机存储时间
        int cacheexpiredTime = 300 + RandomUtil.randomInt(0, 300);
        //设置缓存
        caffeineCache.put(cachekey, list);
        redisTemplate.opsForValue().set(cachekey, list,cacheexpiredTime, TimeUnit.SECONDS);
        return new CommonResp<>(list);
    }

}
