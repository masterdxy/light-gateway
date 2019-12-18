package com.github.masterdxy.gateway.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import io.lettuce.core.RedisClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfiguration {
	
	@NacosValue("${gateway.dubbo.metadata}")
	private String redisUrl;
	
	@Bean
	public RedisClient redisClient () {
		return RedisClient.create(redisUrl);
	}
}
