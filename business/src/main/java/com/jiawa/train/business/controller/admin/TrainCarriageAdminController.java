package com.jiawa.train.business.controller.admin;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.jiawa.train.business.resp.DailyTrainTicketQueryResp;
import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.resp.CommonResp;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.business.req.TrainCarriageQueryReq;
import com.jiawa.train.business.req.TrainCarriageSaveReq;
import com.jiawa.train.business.resp.TrainCarriageQueryResp;
import com.jiawa.train.business.service.TrainCarriageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/admin/train-carriage")
public class TrainCarriageAdminController {
    @Autowired
    Cache<String, Object> caffeineCache;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private TrainCarriageService trainCarriageService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainCarriageSaveReq req) {
        trainCarriageService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainCarriageQueryResp>> queryList(@Valid TrainCarriageQueryReq req) {
        //构建缓存的key
        String requestCondition = JSONUtil.toJsonStr(req);
        String hashKey = DigestUtils.md5DigestAsHex(requestCondition.getBytes());
        String cachekey = String.format("spring-cloud-12306:%s", hashKey);

        //查询本地缓存
        Object localCache = caffeineCache.getIfPresent(cachekey);
        if(localCache != null){
            return new CommonResp<>((PageResp<TrainCarriageQueryResp>)localCache);
        }
        //查询redis缓存
        Object redisCache = redisTemplate.opsForValue().get(cachekey);
        if(redisCache != null){
            //设置本地缓存
            caffeineCache.put(cachekey,redisCache);
            return new CommonResp<>((PageResp<TrainCarriageQueryResp>)redisCache);
        }
        PageResp<TrainCarriageQueryResp> list = trainCarriageService.queryList(req);
        //随机存储时间
        int cacheexpiredTime = 300 + RandomUtil.randomInt(0, 300);
        //设置缓存
        caffeineCache.put(cachekey, list);
        redisTemplate.opsForValue().set(cachekey, list,cacheexpiredTime, TimeUnit.SECONDS);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainCarriageService.delete(id);
        return new CommonResp<>();
    }

}
