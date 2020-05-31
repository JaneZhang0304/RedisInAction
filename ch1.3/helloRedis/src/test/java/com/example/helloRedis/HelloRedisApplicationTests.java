package com.example.helloRedis;

import com.example.helloRedis.example.HelloRedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//Spring boot 升级使用了JUnit5，
@SpringBootTest
class HelloRedisApplicationTests {

	@Test
	void contextLoads() {
	}
	@Autowired
	HelloRedisService helloRedisService;

	@Test
	public void testSet(){
		helloRedisService.testSet();
	}

	@Test
	public void testGet(){
		helloRedisService.testGet();
//		Assertions.assertTrue();
	}

}
