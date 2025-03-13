package com.yupi.yupao.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupao.mapper.UserMapper;
import com.yupi.yupao.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:RedissonTest
 * Package:com.yupi.yupao.service
 * User:HP
 * Date:2025/3/11
 * Time:16:22
 *
 * @Author 周东汉
 * @Version 1.0
 * Description:
 */
@Slf4j
@SpringBootTest
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private UserMapper userMapper;
    private List<Long> mainUserIds = Arrays.asList(1L, 2L, 3L);
    @Test
    public void testRedisson() {
        // List
        RList<String> rList = redissonClient.getList("testList");
        rList.add("zdh");
        System.out.println("rList: " + rList.get(0));
        rList.remove(0);
        // Map
        RMap<String, Object> rMap = redissonClient.getMap("testMap");
        rMap.put("zdh", "231109");
        System.out.println("rMap: " + rMap.get("zdh"));
        // Set
    }

    @Test
    public void testRedisson2() {
        RLock rLock = redissonClient.getLock("yupao:preCacheRecommendUsers:doCache:lock");
        try {
            if(rLock.tryLock(0, -1,TimeUnit.SECONDS)){
                Thread.sleep(1000000);
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
                System.out.println(rLock.isHeldByCurrentThread());
            }
        } catch (InterruptedException e) {
            log.error("锁获取失败", e);
        } finally {
            System.out.println("释放锁" + Thread.currentThread().getName());
            System.out.println(rLock.isHeldByCurrentThread());
            // 只能释放自己持有的锁，不能释放别人的锁
            if(rLock.isHeldByCurrentThread()){
                System.out.println("释放锁成功" + Thread.currentThread().getName());
                rLock.unlock();
            }
        }
    }
}
