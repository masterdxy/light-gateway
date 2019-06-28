package com.github.masterdxy.gateway.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.spring.SpringContext;
import com.github.masterdxy.gateway.spring.SpringVerticleFactory;
import com.github.masterdxy.gateway.verticle.GatewayVerticle;
import com.github.masterdxy.gateway.verticle.ManagerVerticle;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.cpu.CpuCoreSensor;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class VertxInitialization {

    private static Logger log = LoggerFactory.getLogger(VertxInitialization.class);

    @NacosValue("${vertx.enable.cluster:true}")
    private boolean enableCluster;

    @Autowired
    private SpringVerticleFactory springVerticleFactory;

    private Vertx vertx;
    private HazelcastInstance hazelcastInstance;

    public void initialization() {
        //Make Dubbo generic client cache
        VertxOptions options = new VertxOptions()
                .setMetricsOptions(
                        new MicrometerMetricsOptions()
                                .setJvmMetricsEnabled(true)
                                .setPrometheusOptions(new VertxPrometheusOptions().setEnabled(true))
                                .setEnabled(true));

        if (enableCluster) {
            //Make vertx cluster.
            Config hazelcastConfig = new Config();
//            hazelcastConfig.getSerializationConfig().addSerializerConfig(new SerializerConfig().setImplementation(new JsonSerializable()).setTypeClass(ExampleMeeting.class)))
            HazelcastClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);
            options.setClusterManager(mgr);
            Vertx.clusteredVertx(options, res -> {
                if (res.succeeded()) {
                    log.info("Build clustered vertx success");
                    vertx = res.result();
                    hazelcastInstance = mgr.getHazelcastInstance();
                    deploy();
                } else {
                    log.error("Error to build clustered vertx", res.cause());
                    System.exit(-1);
                }
            });
        } else {
            //Create standalone vertx
            vertx = Vertx.vertx(options);
            deploy();
        }
    }

    private void deploy() {
        //Register vertx event bus and consumer

        //Deploy gate's verticle
        Objects.requireNonNull(vertx);
        Objects.requireNonNull(springVerticleFactory);
        // The verticle factory is registered manually because it is created by the Spring container
        vertx.registerVerticleFactory(springVerticleFactory);

        // Scale the verticles on cores: create 4 instances during the deployment
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(CpuCoreSensor.availableProcessors());
        deploymentOptions.setWorkerPoolSize(CpuCoreSensor.availableProcessors() * 2);
        deploymentOptions.setWorkerPoolName(Constant.WORKER_POOL_NAME);

        vertx.deployVerticle(springVerticleFactory.prefix() + ":" + GatewayVerticle.class.getName(), deploymentOptions);
        vertx.deployVerticle(SpringContext.instance(ManagerVerticle.class));
    }

    public void stop() {
        vertx.close();
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }
}
