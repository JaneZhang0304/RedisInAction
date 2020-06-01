package com.example.redisCommand.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SetService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public void testSet(){
        //sadd set-key a b c
        stringRedisTemplate.opsForSet().add("set-key","a","b","c");
        //srem set-key c d
        stringRedisTemplate.opsForSet().remove("set-key","c","d");
        stringRedisTemplate.opsForSet().remove("set-key","c","d");
        //scard set-key
        stringRedisTemplate.opsForSet().size("set-key");
        //smembers set-key
        stringRedisTemplate.opsForSet().members("set-key");
        //smove set-key set-key2 a
        stringRedisTemplate.opsForSet().move("set-key","a","set-key2");
        stringRedisTemplate.opsForSet().move("set-key","c","set-key2");

        //sadd skey1 a b c d
        stringRedisTemplate.opsForSet().add("skey1","a","b","c","d");
        //sadd skey2 c d e f
        stringRedisTemplate.opsForSet().add("skey2","c","d","e","f");
        //sdiff skey1 skey2 //存在与skey1中不存在与skey2中, a b
        stringRedisTemplate.opsForSet().difference("skey1","skey2");
        //sinter skey1 skey2 // c d
        stringRedisTemplate.opsForSet().intersect("skey1","skey2");
        //sunion skey1 skey2 // a b c d e f
        stringRedisTemplate.opsForSet().union("skey1","skey2");
    }


}
