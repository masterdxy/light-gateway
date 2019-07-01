package com.github.masterdxy.gateway;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.config.GatewaySpringConfiguration;
import com.github.masterdxy.gateway.config.VertxInitialization;
import com.github.masterdxy.gateway.spring.SpringContext;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {

    private static Logger log = LoggerFactory.getLogger(Bootstrap.class);

    private static Vertx vertx;

    /*
     For local test only.
      */
    public static void main(String[] args) {
        //switch vertx's logging delegate to slf4j.
        System.setProperty("vertx.logger-delegate-factory-class-name", Constant.LOGGER_FACTORY);

        //Build ApplicationContext before vertx startup.
        SpringContext.initContext(GatewaySpringConfiguration.class);

        //Init vertx.
        VertxInitialization vertxInitialization = SpringContext.instance(VertxInitialization.class);
        vertxInitialization.initialization();

        //Register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(vertxInitialization::stop,"GatewayShutdownHook"));
        log.info("Started.");
    }
}
