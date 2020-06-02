package com.example.redisCommand.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class HashService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public void testHash(){
        //hmset hash-key k1 v1 k2 v2 k3 v3
        Map<String,String> map1 = new HashMap<>();
        map1.put("k1","v1");
        map1.put("k2","v2");
        map1.put("k3","v3");
        stringRedisTemplate.opsForHash().putAll("hash-key",map1);
        //hmget hash-key k2 k3
        stringRedisTemplate.opsForHash().multiGet("hash-key", Arrays.asList("k2","k3"));
        //hlen hash-key
        stringRedisTemplate.opsForHash().size("hash-key");
        //hdel hash-key k1 k3
        stringRedisTemplate.opsForHash().delete("hash-key","k1","k3");
        //hmset hash-key2 short hello long '11111111111111111' //书里用python的1000*'1'
        stringRedisTemplate.opsForHash().putAll("hash-key2",map1);
        //hkeys hash-key2
        Set<Object> keys = stringRedisTemplate.opsForHash().keys("hash-key2");
        //hexists hash-key2 num
        stringRedisTemplate.opsForHash().hasKey("hash-key2","num");
        //hincrby hash-key2 num 1
        stringRedisTemplate.opsForHash().increment("hash-key2","num",1L);
    }
}
