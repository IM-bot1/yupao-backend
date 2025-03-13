package com.yupi.yupao.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupao.mapper.UserMapper;
import com.yupi.yupao.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:PreCacheJob
 * Package:com.yupi.yupao.job
 * User:HP
 * Date:2025/3/10
 * Time:20:41

 * Author 周东汉
 * Version  1.0
 * Description:缓存预热
 */
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedissonClient redissonClient;

    private List<Long> mainUserIds = Arrays.asList(1L, 2L, 3L);
    // 定时任务：缓存推荐用户
    @Scheduled(cron = "0 0 5 * * *")
    public void preCacheRecommendUsers() {
        RLock rLock = redissonClient.getLock("yupao:preCacheRecommendUsers:doCache:lock");
        try {
            if(rLock.tryLock(0, -1,TimeUnit.SECONDS)){
                System.out.println("获取锁成功" +Thread.currentThread().getName());
                for (Long mainUserId : mainUserIds) {
                    String redisKey = String.format("yupao:user:recommend:%s", mainUserId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userMapper.selectPage(new Page<>(1, 20), queryWrapper);
                    try {
                        valueOperations.set(redisKey, userPage, 20, TimeUnit.HOURS);
                        log.info("定时任务：缓存推荐用户成功");
                    } catch (Exception e) {
                        log.error("定时任务：缓存用户失败", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("锁获取失败", e);
        } finally {
            // 只能释放自己持有的锁，不能释放别人的锁
            if(rLock.isHeldByCurrentThread()){
                System.out.println("释放锁成功" + Thread.currentThread().getName());
                rLock.unlock();
            }
        }
    }
}
