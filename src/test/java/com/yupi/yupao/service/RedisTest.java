package com.yupi.yupao.service;

import com.yupi.yupao.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * ClassName:RedisTest
 * Package:com.yupi.yupao.service
 * User:HP
 * Date:2025/3/8
 * Time:21:53
 *
 * @Author 周东汉
 * @Version 1.0
 * Description:
 */
@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void testRedis() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 增
        valueOperations.set("zdh", "zdh2311109");
        valueOperations.set("zdhFLOAT", 1.5);
        valueOperations.set("zdhINT", 1);
        User user = new User();
        user.setUsername("zdh");
        user.setUserAccount("zdh231109");
        user.setUserPassword("123456");
        user.setGender("男");
        user.setProfile("这是个测试用户");
        user.setEmail("zdh@163.com");
        user.setAvatarUrl("https://www.baidu.com/img/bd_logo1.png");
        user.setPhone("13811111111");
        user.setUserStatus(0);
        user.setPlanetCode("123456");
        user.setTags("[\"Java\",\"Python\",\"C++\"]");
        valueOperations.set("zdhUser", user);
        // 查
        Object zdhString = valueOperations.get("zdh");
        Assertions.assertTrue("zdh2311109".equals(zdhString));
        Object zdhFloat = valueOperations.get("zdhFLOAT");
        Assertions.assertTrue(1.5 == (Double) zdhFloat);
        Object zdhInt = valueOperations.get("zdhINT");
        Assertions.assertTrue(1 == (Integer) zdhInt);
        Object zdhUser = valueOperations.get("zdhUser");
        System.out.println(zdhUser);
    }
}
