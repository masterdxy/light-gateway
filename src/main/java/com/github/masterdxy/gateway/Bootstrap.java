package com.github.masterdxy.gateway;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.config.GatewaySpringConfiguration;
import com.github.masterdxy.gateway.spring.SpringContext;
import com.github.masterdxy.gateway.verticle.GatewayVerticle;
import com.github.masterdxy.gateway.spring.SpringVerticleFactory;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.impl.cpu.CpuCoreSensor;
import io.vertx.core.spi.VerticleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class Bootstrap {

    private static Logger log = LoggerFactory.getLogger(Bootstrap.class);

    /*
     For local test only.
      */
    public static void main(String[] args) {
        //switch vertx's logging delegate to slf4j.
        System.setProperty("vertx.logger-delegate-factory-class-name", Constant.LOGGER_FACTORY);

        //Build ApplicationContext before vertx startup.
        ApplicationContext context = SpringContext.initContext(GatewaySpringConfiguration.class);

        //Load nacos config

        //Make Dubbo generic client cache

        //Create vertx
        Vertx v = Vertx.vertx();

        //Register vertx event bus and consumer


        //Deploy gate's verticle
        VerticleFactory verticleFactory = context.getBean(SpringVerticleFactory.class);

        // The verticle factory is registered manually because it is created by the Spring container
        v.registerVerticleFactory(verticleFactory);

        // Scale the verticles on cores: create 4 instances during the deployment
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(CpuCoreSensor.availableProcessors());
        deploymentOptions.setWorkerPoolSize(CpuCoreSensor.availableProcessors() * 2);
        deploymentOptions.setWorkerPoolName(Constant.WORKER_POOL_NAME);

        v.deployVerticle(verticleFactory.prefix() + ":" + GatewayVerticle.class.getName(), deploymentOptions);

        //Register shutdown hook

        log.info("Started.");
    }
}
