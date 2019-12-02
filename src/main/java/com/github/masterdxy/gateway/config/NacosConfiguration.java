package com.github.masterdxy.gateway.config;

import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.context.annotation.Configuration;

@Configuration @EnableNacosConfig @NacosPropertySource(dataId = "gateway", autoRefreshed = true)
public class NacosConfiguration {

}
