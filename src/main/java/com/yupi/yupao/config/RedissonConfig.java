package com.yupi.yupao.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName:RedissonConfig
 * Package:com.yupi.yupao.config
 * User:HP
 * Date:2025/3/11
 * Time:16:15
 *
 * @Author 周东汉
 * @Version 1.0
 * Description:
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    private String host;
    private int port;
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String Redis_address = String.format("redis://%s:%d", host, port);
        config.useSingleServer().setAddress(Redis_address)
                .setDatabase(3);
        return Redisson.create(config);
    }
}
