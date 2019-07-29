package com.github.masterdxy.gateway.utils;


import com.github.masterdxy.gateway.config.VertxInitialization;
import com.github.masterdxy.gateway.spring.SpringContext;
import com.github.masterdxy.gateway.task.TaskRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GatewayShutdownHook extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(GatewayShutdownHook.class);

    private static final GatewayShutdownHook gatewayShutdownHook = new GatewayShutdownHook("GatewayShutdownHook");

    @Override
    public void run() {
        if (VertxInitialization.getDeploymentIds().size() > 0) {
            if (VertxInitialization.isStarted()) {
                CountDownLatch countDownLatch = new CountDownLatch(VertxInitialization.getDeploymentIds().size());
                VertxInitialization.setStarted(false);
                VertxInitialization.getDeploymentIds().forEach(deploymentId ->
                        VertxInitialization.getVertx().undeploy(deploymentId, async -> {
                            countDownLatch.countDown();
                        }));
                try {
                    countDownLatch.await(1000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ignore) {
                }
                logger.info("Vertx stopped.");
                TaskRegistry.stopAll();
                logger.info("Tasks stopped.");
                SpringContext.stop();
                logger.info("StringContext stopped.");
            }
        }
    }

    private GatewayShutdownHook(String name) {
        super(name);
    }

    public static GatewayShutdownHook getGatewayShutdownHook() {
        return gatewayShutdownHook;
    }
}
