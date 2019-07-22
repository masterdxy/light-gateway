package com.github.masterdxy.gateway;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.config.GatewaySpringConfiguration;
import com.github.masterdxy.gateway.config.VertxInitialization;
import com.github.masterdxy.gateway.spring.SpringContext;
import com.github.masterdxy.gateway.utils.GatewayShutdownHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;


public class Bootstrap {

    private static Logger log = LoggerFactory.getLogger(Bootstrap.class);

    /*
     For local test only.
      */
    public static void main(String[] args) {

        //switch vertx's logging delegate to slf4j.
        System.setProperty("vertx.logger-delegate-factory-class-name", Constant.LOGGER_FACTORY);
        System.setProperty("hazelcast.logging.type", "slf4j");
        //Build ApplicationContext before vertx startup.
        SpringContext.initContext(GatewaySpringConfiguration.class);

        CountDownLatch countDownLatch = new CountDownLatch(2);//deploy 2 verticle
        //Init vertx.
        VertxInitialization vertxInitialization = SpringContext.instance(VertxInitialization.class);
        vertxInitialization.initialization(countDownLatch);

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("Start failed.", e);
        }
        Runtime.getRuntime().addShutdownHook(GatewayShutdownHook.getGatewayShutdownHook());
        log.info("Started.");

    }

}
