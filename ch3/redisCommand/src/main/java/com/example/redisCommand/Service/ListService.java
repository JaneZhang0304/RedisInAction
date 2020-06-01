package com.example.redisCommand.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ListService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public void testList(){
        //>rpush list-key last
        stringRedisTemplate.opsForList().rightPush("list-key","last");
        //>lpush list-key first
        stringRedisTemplate.opsForList().leftPush("list-key","first");
        //>rpush list-key 'new last' //需要加括号，不然会以空格区分多个value
        stringRedisTemplate.opsForList().rightPush("list-key","new last");
        //>lrange list-key 0 -1
        stringRedisTemplate.opsForList().range("list-key",0,-1);
        //lpop list-key
        stringRedisTemplate.opsForList().leftPop("list-key");
        //rpush list-key a b c
        stringRedisTemplate.opsForList().rightPushAll("list-key","a","b","c");
        //ltrim list-key 2 -1
        stringRedisTemplate.opsForList().trim("list-key",2,-1);
    }

    public void testBlock(){
        //rpush list item1 item2
        stringRedisTemplate.opsForList().rightPushAll("list","item1","item2");
        //rpush list2 item3
        stringRedisTemplate.opsForList().rightPush("list2","item3");
        //brpoplpush list2 list 1
        stringRedisTemplate.opsForList().rightPopAndLeftPush("list2","list",1L, TimeUnit.SECONDS);
        //brpoplpush list2 list 1
        stringRedisTemplate.opsForList().rightPopAndLeftPush("list2","list",1L,TimeUnit.SECONDS);
        //lrange list 0 -1
        stringRedisTemplate.opsForList().range("list",0,-1);
        //brpoplpush list list2 1
        stringRedisTemplate.opsForList().rightPopAndLeftPush("list","list2",1L,TimeUnit.SECONDS);
        //blpop list list2 1 //spring-data-redis好像没有实现多个key
        stringRedisTemplate.opsForList().leftPop("list",1L,TimeUnit.SECONDS);
    }
}
