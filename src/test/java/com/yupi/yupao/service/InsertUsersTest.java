package com.yupi.yupao.service;

import com.yupi.yupao.mapper.UserMapper;import com.yupi.yupao.model.domain.User;

import org.ehcache.xml.model.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 插入用户数据
 * @author yupi
 * @date 2021/01/08
 */
@SpringBootTest
public class InsertUsersTest {
    @Resource
//    private UserMapper userMapper;
    private UserService userService;
    /**
     * MethodName:doInsertUsers
     * Description: 插入用户数据
     * @return: void
     */
    @Test
    public void doInsertUsers() {
        List<User> users = new ArrayList<>();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int count = 100000;
        for (int i = 1; i <= count; i++) {
            User user = new User();
            user.setUsername("假用户");
            user.setUserAccount("fakeZDH");
            user.setAvatarUrl("https://s2.loli.net/2025/02/27/WtbSkx5C34cADow.jpg");
            user.setGender("男");
            user.setUserPassword("123456");
            user.setTags("['假用户']");
            user.setProfile("这个人很懒，什么也没留下。");
            user.setEmail("fakeZDH@gmail.com");
            user.setPhone("13812345678");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode(String.valueOf(i +100));
            users.add(user);
        }
        userService.saveBatch(users,10000);
        stopWatch.stop();
        System.out.println("插入" + count + "条用户数据，耗时：" + stopWatch.getTotalTimeMillis() + "毫秒");
    }
    @Test
    public void doInsertUsers2() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int count = 100000;
        int j = 0;
        int batchSize = 5000;
        List<CompletableFuture> futures = new ArrayList<>();
        for(int i = 1;i <= count / batchSize; i++){
            List<User> users = new ArrayList<>();
            while(true){
                j++;
                User user = new User();
                user.setUsername("假用户");
                user.setUserAccount("fakeZDH");
                user.setAvatarUrl("https://s2.loli.net/2025/02/27/WtbSkx5C34cADow.jpg");
                user.setGender("男");
                user.setUserPassword("123456");
                user.setTags("['假用户']");
                user.setProfile("这个人很懒，什么也没留下。");
                user.setEmail("fakeZDH@gmail.com");
                user.setPhone("13812345678");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setPlanetCode(String.valueOf(i + 100));
                users.add(user);
                if(j % batchSize == 0){
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                userService.saveBatch(users, batchSize);
            });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println("插入" + count + "条用户数据，耗时：" + stopWatch.getTotalTimeMillis() + "毫秒");
    }
}
