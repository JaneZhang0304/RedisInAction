package com.example.redisCommand.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ValueService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public void testValue(){
        //>append new-string-key "hello "
        stringRedisTemplate.opsForValue().append("new-string-key","hello ");
        //>append new-string-key world!
        stringRedisTemplate.opsForValue().append("new-string-key","world!");
        //>getrange new-string-key 3,7
        stringRedisTemplate.opsForValue().get("new-string-key",3,7);
        //>setrange new-string-key 0 H
        stringRedisTemplate.opsForValue().set("new-string-key","H",0L);
        //>setrange new-string-key 6 W
        stringRedisTemplate.opsForValue().set("new-string-key","W",6);
        //>get new-string-key
        stringRedisTemplate.opsForValue().get("new-string-key");
        //>setrange new-string-key 11 ', how are you?'
        stringRedisTemplate.opsForValue().set("new-string-key",", how are you?",11);
        //>get new-string-key
        stringRedisTemplate.opsForValue().get("new-string-key");
        //>setbit another-key 2 1
        stringRedisTemplate.opsForValue().setBit("another-key",2,true);
        //>setbit another-key 7 1
        stringRedisTemplate.opsForValue().setBit("another-key",7,true);
        //>get another-key
        stringRedisTemplate.opsForValue().get("another-key");

    }
}
