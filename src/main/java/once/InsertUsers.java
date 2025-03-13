package once;

import com.yupi.yupao.mapper.UserMapper;
import com.yupi.yupao.model.domain.User;
import org.springframework.util.StopWatch;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * ClassName:InsertUsers
 * Package:once
 * User:HP
 * Date:2025/3/8
 * Time:13:11
 *
 * @Author 周东汉
 * @Version 1.0
 * Description:
 */
@Component
public class InsertUsers {
    @Resource
    private UserMapper userMapper;
    /**
    * MethodName:doInsertUsers
    * Description: 插入用户数据
    * @return: void
    */
//    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int count = 1000;
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
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println("插入" + count + "条用户数据，耗时：" + stopWatch.getTotalTimeMillis() + "毫秒");
    }
}
