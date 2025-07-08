package com.jiawa.train.business.controller.admin;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.jiawa.train.business.resp.DailyTrainTicketQueryResp;
import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.resp.CommonResp;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.business.req.TrainStationQueryReq;
import com.jiawa.train.business.req.TrainStationSaveReq;
import com.jiawa.train.business.resp.TrainStationQueryResp;
import com.jiawa.train.business.service.TrainStationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/admin/train-station")
public class TrainStationAdminController {
    @Autowired
    Cache<String, Object> caffeineCache;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private TrainStationService trainStationService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainStationSaveReq req) {
        trainStationService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainStationQueryResp>> queryList(@Valid TrainStationQueryReq req) {
        //构建缓存的key
        String requestCondition = JSONUtil.toJsonStr(req);
        String hashKey = DigestUtils.md5DigestAsHex(requestCondition.getBytes());
        String cachekey = String.format("spring-cloud-12306:%s", hashKey);

        //查询本地缓存
        Object localCache = caffeineCache.getIfPresent(cachekey);
        if(localCache != null){
            return new CommonResp<>((PageResp<TrainStationQueryResp>)localCache);
        }
        //查询redis缓存
        Object redisCache = redisTemplate.opsForValue().get(cachekey);
        if(redisCache != null){
            //设置本地缓存
            caffeineCache.put(cachekey,redisCache);
            return new CommonResp<>((PageResp<TrainStationQueryResp>)redisCache);
        }
        PageResp<TrainStationQueryResp> list = trainStationService.queryList(req);
        //随机存储时间
        int cacheexpiredTime = 300 + RandomUtil.randomInt(0, 300);
        //设置缓存
        caffeineCache.put(cachekey, list);
        redisTemplate.opsForValue().set(cachekey, list,cacheexpiredTime, TimeUnit.SECONDS);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainStationService.delete(id);
        return new CommonResp<>();
    }

}
