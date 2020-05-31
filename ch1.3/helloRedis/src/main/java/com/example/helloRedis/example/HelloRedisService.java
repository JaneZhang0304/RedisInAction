package com.example.helloRedis.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class HelloRedisService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    //region string
    public void testSet() {
         stringRedisTemplate.opsForValue().set("test-string-value", "Hello Redis");
    }

    public void testGet() {
        String value = stringRedisTemplate.opsForValue().get("test-string-value");
        System.out.println(value);
    }

    public void TestSetTimeOut(){
        stringRedisTemplate.opsForValue().set("test-string-key-time-out","Hello Redis",3, TimeUnit.HOURS);
    }

    public void testDeleted(){
        stringRedisTemplate.delete("test-string-value");
    }
    //endregion

    // region  list
    public void testLeftPush(){
        redisTemplate.opsForList().leftPush("TestList", "TestLeftPush");
    }

    public void testRightPush(){
        redisTemplate.opsForList().rightPush("TestList","TestRightPush");
    }
    public void testLeftPop(){
        Object leftFirstElement = redisTemplate.opsForList().leftPop("TestList");
        System.out.println(leftFirstElement);
    }
    public void testRightPop(){
        Object rightFirstElement = redisTemplate.opsForList().rightPop("TestList");
        System.out.println(rightFirstElement);
    }
    //endregion
    //region hash
    public void testHashPut(){
        redisTemplate.opsForHash().put("TestHash","FirstElement","Hello,Redis hash.");
        boolean result = redisTemplate.opsForHash().hasKey("TestHash","FirstElement");
        System.out.println(result);
    }

    public void testHashGet(){
        Object element = redisTemplate.opsForHash().get("TestHash","FirstElement");
        System.out.println(element);
    }

    public void testHashDel(){
        redisTemplate.opsForHash().delete("TestHash","FirstElement");
    }
    //endregion
    //region set
    public void testSetAdd(){
        stringRedisTemplate.opsForSet().add("TestSet","e1","e2","e3");
        Long num = redisTemplate.opsForSet().size("TestSet");
        System.out.println("3=="+num);
    }
    public void testSetGet(){
        Set<Object> testSet = redisTemplate.opsForSet().members("TestSet");
        Set<String> set = stringRedisTemplate.opsForSet().members("TestSet");
        System.out.println(testSet);
        System.out.println(set);
    }
    public void testSetRemove(){
        redisTemplate.opsForSet().remove("TestSet","e1","e2");
    }
    //endregion
    //region zSet

    //endregion

    //region shared lock
    /**
     * 在实现分布式锁的时候其实就是使用了 RedisTemplate 的 execute 方法来执行 lua 脚本来获取和释放锁的
     * */
    public void sharedLock(){
        String key="";
        String value ="";
        Long timeout = 1L;
        TimeUnit timeUnit=TimeUnit.HOURS;
        //获取锁
        Boolean lockStat = stringRedisTemplate.execute((RedisCallback<Boolean>) connection->connection.set(key.getBytes(Charset.forName("UTF-8")),
                value.getBytes(Charset.forName("UTF-8")),
                Expiration.from(timeout,timeUnit),
                RedisStringCommands.SetOption.SET_IF_ABSENT));
        //释放锁
        String script = "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        boolean unLockStat = stringRedisTemplate.execute((RedisCallback<Boolean>)connection->
                connection.eval(script.getBytes(), ReturnType.BOOLEAN,1,
                key.getBytes(Charset.forName("UTF-8")),value.getBytes(Charset.forName("UTF-8"))));
    }
    //endregion

}
