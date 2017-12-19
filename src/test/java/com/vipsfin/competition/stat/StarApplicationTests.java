package com.vipsfin.competition.stat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StarApplicationTests {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void redis() {
        redisTemplate.opsForValue().set("x", "1");
        System.out.println(redisTemplate.opsForValue().get("x"));
    }
}
