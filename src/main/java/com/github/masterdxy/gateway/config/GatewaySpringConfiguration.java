package com.github.masterdxy.gateway.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "application-dev.properties")
@EnableAutoConfiguration
@ComponentScan(basePackages = "${scanPackage}")
public class GatewaySpringConfiguration {

    /*
     * Import Nacos
     * Import Dubbo
     * Import Redis/MySQL
     * Config here
     */

}
