package com.example.helloRedis.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloRedisController {
    @Autowired
    HelloRedisService helloRedisService;

    @RequestMapping("/")
    public void getTest(){
        helloRedisService.testSet();
        helloRedisService.testGet();
        helloRedisService.testLeftPush();
        helloRedisService.testRightPush();
        helloRedisService.testLeftPop();
        helloRedisService.testSetAdd();
        helloRedisService.testSetGet();
        helloRedisService.testHashPut();
        helloRedisService.testHashGet();
    }
}
