package com.github.masterdxy.gateway.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration @PropertySource(value = "application.properties") @ComponentScan(basePackages = "com.github.masterdxy.gateway")
public class GatewaySpringConfiguration {

    /*
     * Import Nacos
     * Import Dubbo
     * Import Redis/MySQL
     * Config here
     */

}
