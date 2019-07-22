package com.github.masterdxy.gateway.verticle;

import org.apache.dubbo.common.utils.NetUtils;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.common.Endpoint;
import com.github.masterdxy.gateway.common.dao.EndpointConfigDao;
import com.github.masterdxy.gateway.config.VertxInitialization;
import com.github.masterdxy.gateway.handler.HandlerMapping;
import com.github.masterdxy.gateway.plugin.endpoint.EndpointManager;
import com.github.masterdxy.gateway.plugin.impl.dubbo.DubboServiceProvider;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.SocketAddress;
import org.apache.commons.lang3.StringUtils;
import org.jfaster.mango.operator.Mango;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class ManagerVerticle extends AbstractVerticle {

    //ManagerVerticle  manage cluster of gateway.
    //Init hazelcast for response cache and ratelimiter,sub redis some channel, and push manage event,like endpoint changes.
    //Handle ThreadDump and HeadDump req

    //1.✅Init cluster with hazelcast. Or redis.
    //2.✅Load endpoint config and others with distributed lock from remote db. (jdbc)
    //3.Register manager endpoint e.g. /thread_dump and listen. (web)
    //4.Watch config changes by period retrieve remote db, send msg through event bus.(timer)


    private static Logger logger = LoggerFactory.getLogger(ManagerVerticle.class);
    @Autowired
    private Mango mango;
    @Autowired
    private EndpointManager endpointManager;
    @Autowired
    private VertxInitialization vertxInitialization;
    @Autowired
    private HazelcastInstance hazelcastInstance;
    @Autowired
    private DubboServiceProvider dubboServiceProvider;
    @Autowired
    private HandlerMapping handlerMapping;

    @NacosValue("${gateway.bind.port:8080}")
    private int bindPort;
    @NacosValue("${gateway.bind.host:}")
    private String bindHost;

    private EndpointConfigDao endpointConfigDao;

    private EPCLoaderService epcLoaderService;

    //TODO when lock release ? shutdown or node leave
    private volatile boolean hasLock;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        logger.info("Starting manager verticle....");
        logger.info("1.Get hazelcast instance ....");
        Objects.requireNonNull(hazelcastInstance);

        logger.info("2.Init mango .... ");
        endpointConfigDao = mango.create(EndpointConfigDao.class);

        ILock iLock = hazelcastInstance.getLock(Constant.HAZELCAST_LOCK_KEY);
        Objects.requireNonNull(iLock);
        epcLoaderService = new EPCLoaderService(iLock);
        epcLoaderService.startAsync().awaitRunning();
        //execute once before scheduler started.
        epcLoaderService.awaitFirstTimeRun();
        //Build router and handlers
        Handler<HttpServerRequest> handler = handlerMapping.getManagerHandler(vertx);
        if (StringUtils.isEmpty(bindHost)) {
            logger.warn("bind host is null, trying fetch an ip.");
            bindHost = NetUtils.getLocalHost();
        }
        SocketAddress bindAddress = SocketAddress.inetSocketAddress(bindPort, bindHost);
        logger.info("Manager is bind to {}", bindAddress.toString());
        vertx.createHttpServer().requestHandler(handler)
                .listen(bindAddress, (httpServerAsyncResult -> {
                    if (httpServerAsyncResult.succeeded())
                        startFuture.complete();
                    else
                        startFuture.fail(httpServerAsyncResult.cause());
                }));

//        startFuture.complete();
    }

    private class EPCLoaderService extends AbstractScheduledService {

        private ILock lock;
        private CountDownLatch countDownLatch;

        EPCLoaderService(ILock lock) {
            this.lock = lock;
            countDownLatch = new CountDownLatch(1);
        }

        @Override
        protected void runOneIteration() throws Exception {
            logger.info("3.Try Lock .... ");
            lock(this.lock);
            logger.info("4.Loading Epc .... ");
            loadEpc();
            logger.info("5.Load Dubbo Service ...");
            int serviceCount = dubboServiceProvider.initDubboGenericService();
            logger.info("5.Loaded {} Dubbo Service.", serviceCount);
            VertxInitialization.started = true;
            countDownLatch.countDown();
        }

        @Override
        protected Scheduler scheduler() {
            return Scheduler.newFixedDelaySchedule(0, Constant.DELAY_LOAD_EPC, TimeUnit.MILLISECONDS);
        }

        void awaitFirstTimeRun() {
            try {
                countDownLatch.await(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignore) {
            }
        }
    }

    private void lock(ILock iLock) {
        try {
            if (iLock.isLocked()) {
                if (iLock.isLockedByCurrentThread()) {
                    logger.info("current lock is locked by current thread");
                } else {
                    if (iLock.tryLock(Constant.LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                        logger.info("current lock is locked, try lock success.");
                        hasLock = true;
                    } else {
                        logger.info("current lock is locked, try lock timeout, wait next period");
                    }
                }
            } else {
                logger.info("current lock is NOT locked, try lock now.");
                if (iLock.tryLock(Constant.LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                    logger.info("current lock is not locked, try lock success.");
                    hasLock = true;
                } else {
                    logger.info("current lock is not locked, try lock timeout, wait next period");
                }
            }
        } catch (Exception e) {
            logger.warn("Periodic lock task error", e);
        }
    }

    private void loadEpc() {
        if (hasLock) {
            logger.info("load epc from MySQL store with lock ...");
            Map<String, Endpoint> endpointConfigMap = Maps.newConcurrentMap();
            List<Endpoint> allEpc = endpointConfigDao.findAll();
            allEpc.forEach(epc -> endpointConfigMap.put(epc.getUri(), epc));
            hazelcastInstance.getMap(Constant.HAZELCAST_EPC_MAP_KEY).putAll(endpointConfigMap);
            endpointManager.updateEpcMap(endpointConfigMap);
        } else {
            logger.info("load epc from K/V store without lock ...");
            IMap<String, Endpoint> iMap = hazelcastInstance.getMap(Constant.HAZELCAST_EPC_MAP_KEY);
            Set<String> keySet = iMap.keySet();
            Map<String, Endpoint> endpointConfigMap = Maps.newConcurrentMap();
            keySet.forEach(key -> endpointConfigMap.put(key, iMap.get(key)));
            endpointManager.updateEpcMap(endpointConfigMap);
        }

    }


    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);
        epcLoaderService.stopAsync().awaitTerminated();
    }
}
