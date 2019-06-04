package com.github.masterdxy.gateway;

import com.github.masterdxy.gateway.config.GatewaySpringConfiguration;
import com.github.masterdxy.gateway.spring.SpringContext;
import com.github.masterdxy.gateway.verticle.GatewayVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {

    private static Logger log = LoggerFactory.getLogger(Bootstrap.class);

    /*
     For local test only.
      */
    public static void main(String[] args) {
        //Build ApplicationContext before vertx startup.
        SpringContext.initContext(GatewaySpringConfiguration.class);

        //Load nacos config

        //Make Dubbo generic client cache

        //Create vertx
        Vertx v = Vertx.vertx();

        //Register vertx event bus and consumer


        //Deploy gate's verticle
        v.deployVerticle(new GatewayVerticle());


        log.info("Started.");
    }
}
