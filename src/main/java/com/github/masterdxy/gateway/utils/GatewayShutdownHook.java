package com.github.masterdxy.gateway.utils;


import com.github.masterdxy.gateway.config.VertxInitialization;
import com.github.masterdxy.gateway.spring.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class GatewayShutdownHook extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(GatewayShutdownHook.class);

    private static final GatewayShutdownHook gatewayShutdownHook = new GatewayShutdownHook("GatewayShutdownHook");

    @Override
    public void run() {
        VertxInitialization.setStarted(false);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        if (VertxInitialization.getDeploymentIds().size() != 0 && VertxInitialization.isStarted()) {
            VertxInitialization.getDeploymentIds().forEach(deploymentId ->
                    VertxInitialization.getVertx().undeploy(deploymentId, async -> {
                        countDownLatch.countDown();
                    }));
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException ignore) {
        }
        logger.info("Vertx stopped.");
        SpringContext.stop();
        logger.info("StringContext stopped.");
    }

    private GatewayShutdownHook(String name) {
        super(name);
    }

    public static GatewayShutdownHook getGatewayShutdownHook() {
        return gatewayShutdownHook;
    }
}
