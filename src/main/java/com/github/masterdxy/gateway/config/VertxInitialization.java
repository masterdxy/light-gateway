package com.github.masterdxy.gateway.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.spring.SpringContext;
import com.github.masterdxy.gateway.spring.SpringVerticleFactory;
import com.github.masterdxy.gateway.verticle.GatewayVerticle;
import com.github.masterdxy.gateway.verticle.ManagerVerticle;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.cpu.CpuCoreSensor;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import io.vertx.micrometer.backends.PrometheusBackendRegistry;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@Component
public class VertxInitialization {

    private static Logger log = LoggerFactory.getLogger(VertxInitialization.class);

    @NacosValue("${gateway.cluster.enable:true}")
    private boolean enableCluster;

    public static volatile boolean started = false;

    @Autowired
    private SpringVerticleFactory springVerticleFactory;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private DataSource dataSource;

    private Vertx vertx;


    public void initialization(CountDownLatch countDownLatch) {
        //Make Dubbo generic client cache
        MicrometerMetricsOptions metricsOptions = new MicrometerMetricsOptions()
                .setJvmMetricsEnabled(false)
                .setEnabled(true);

        MeterRegistry meterRegistry =
                new PrometheusBackendRegistry(new VertxPrometheusOptions().setEnabled(true).setPublishQuantiles(true)).getMeterRegistry();


        metricsOptions.setMicrometerRegistry(meterRegistry);

        initMeter(meterRegistry);

        VertxOptions options = new VertxOptions()
                .setMetricsOptions(metricsOptions);

        if (enableCluster) {
            //Make vertx cluster.
            Config hazelcastConfig = new Config();
            HazelcastClusterManager mgr = new HazelcastClusterManager(hazelcastInstance);
            options.setClusterManager(mgr);
            Vertx.clusteredVertx(options, res -> {
                if (res.succeeded()) {
                    log.info("Build clustered vertx success");
                    vertx = res.result();
                    deploy(countDownLatch);
                } else {
                    log.error("Error to build clustered vertx", res.cause());
                    System.exit(-1);
                }
            });
        } else {
            //Create standalone vertx
            vertx = Vertx.vertx(options);
            deploy(countDownLatch);
        }
    }

    private void deploy(CountDownLatch countDownLatch) {
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

        vertx.deployVerticle(springVerticleFactory.prefix() + ":" + GatewayVerticle.class.getName(),
                deploymentOptions, stringAsyncResult -> {
                    if (stringAsyncResult.succeeded()) {
                        log.info("Deploy GatewayVerticle Success, deploymentId:{}", stringAsyncResult.result());
                        countDownLatch.countDown();
                    } else
                        log.error("Deploy GatewayVerticle Failed, deploymentId:{}, cause:{}",
                                stringAsyncResult.result(), stringAsyncResult.cause().getMessage());
                });
        vertx.deployVerticle(SpringContext.instance(ManagerVerticle.class), stringAsyncResult -> {
            if (stringAsyncResult.succeeded()) {
                log.info("Deploy ManagerVerticle Success, deploymentId:{}", stringAsyncResult.result());
                countDownLatch.countDown();
            } else {
                log.error("Deploy ManagerVerticle Failed, deploymentId:{}, cause:{}",
                        stringAsyncResult.result(), stringAsyncResult.cause().getMessage());
            }
        });


    }

    private void initMeter(MeterRegistry registry) {
        registry.config().commonTags(Tags.of("application", "gw"));
        new ClassLoaderMetrics().bindTo(registry);
        new JvmMemoryMetrics().bindTo(registry);
        new JvmGcMetrics().bindTo(registry);
        new JvmThreadMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new LogbackMetrics().bindTo(registry);
        new FileDescriptorMetrics().bindTo(registry);
        new UptimeMetrics().bindTo(registry);
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        Gauge.builder("active_connections", () -> hikariDataSource.getHikariPoolMXBean().getActiveConnections())
                .description("The number of active connections").register(registry);
        Gauge.builder("idle_connections", () -> hikariDataSource.getHikariPoolMXBean().getIdleConnections())
                .description("The number of idle connections").register(registry);
    }

}
