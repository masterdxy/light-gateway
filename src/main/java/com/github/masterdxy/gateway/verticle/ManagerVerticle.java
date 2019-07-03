package com.github.masterdxy.gateway.verticle;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.common.EndpointConfig;
import com.github.masterdxy.gateway.common.dao.EndpointConfigDao;
import com.github.masterdxy.gateway.config.VertxInitialization;
import com.github.masterdxy.gateway.plugin.endpoint.EndpointManager;
import com.google.common.collect.Maps;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import org.jfaster.mango.operator.Mango;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class ManagerVerticle extends AbstractVerticle {

    //ManagerVerticle  manage cluster of gateway.
    //Init hazelcast for response cache and ratelimiter,sub redis some channel, and push manage event,like endpoint changes.
    //Handle ThreadDump and HeadDump req

    //1.Init cluster with hazelcast. Or redis.
    //2.Load endpoint config and others with distributed lock from remote db. (jdbc)
    //3.Register manager endpoint e.g. /thread_dump and listen. (web)
    //4.Watch config changes by period retrieve remote db, send msg through event bus.(timer)


    private static Logger logger = LoggerFactory.getLogger(ManagerVerticle.class);

    @Autowired
    private Mango mango;
    @Autowired
    private EndpointManager endpointManager;
    @Autowired
    private VertxInitialization vertxInitialization;

    private HazelcastInstance hazelcastInstance;
    private EndpointConfigDao endpointConfigDao;


    //TODO when lock release ? shutdown or node leave
    private boolean hasLock;
    private Long periodicLoadEpcTimerId = -1L;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        logger.info("Starting manager verticle....");
        logger.info("1.Get hazelcast instance ....");
        hazelcastInstance = vertxInitialization.getHazelcastInstance();
        Objects.requireNonNull(hazelcastInstance);

        logger.info("2.Init mango .... ");
        endpointConfigDao = mango.create(EndpointConfigDao.class);

        ILock iLock = hazelcastInstance.getLock("config_keeper_lock");
        Objects.requireNonNull(iLock);

        logger.info("3.Try Lock .... ");
        lock(iLock);
        logger.info("4.Load EPC .... ");
        loadEpc();

        startFuture.complete();
    }

    private void lock(ILock iLock) {
        //TODO this method may block the event-loop thread.
        try {
            if (iLock.isLocked()) {
                if (iLock.isLockedByCurrentThread()) {
                    logger.info("current lock is locked by current thread");
                } else {
                    if (iLock.tryLock(500, TimeUnit.MILLISECONDS)) {
                        logger.info("current lock is locked, try lock success.");
                        hasLock = true;
                    } else {
                        logger.info("current lock is locked, try lock timeout, wait next period");
                    }
                }
            } else {
                logger.info("current lock is NOT locked, try lock now.");
                if (iLock.tryLock(500, TimeUnit.MILLISECONDS)) {
                    logger.info("current lock is not locked, try lock success.");
                    hasLock = true;
                } else {
                    logger.info("current lock is not locked, try lock timeout, wait next period");
                }
            }
            vertx.setTimer(Constant.DELAY_TRY_LOCK, timerId -> lock(iLock));

        } catch (Exception e) {
            logger.warn("Periodic lock task error", e);
        }
    }

    private void loadEpc() {
        //TODO this method may block the event-loop thread.
        if (hasLock) {
            logger.info("load epc from MySQL store with lock ...");
            Map<String, EndpointConfig> endpointConfigMap = Maps.newConcurrentMap();
            List<EndpointConfig> allEpc = endpointConfigDao.findAll();
            allEpc.forEach(epc -> endpointConfigMap.put(epc.getUri(), epc));
            hazelcastInstance.getMap(Constant.HAZELCAST_EPC_MAP_KEY).putAll(endpointConfigMap);
            endpointManager.updateEpcMap(endpointConfigMap);
        } else {
            logger.info("load epc from K/V store without lock ...");
            IMap<String, EndpointConfig> iMap = hazelcastInstance.getMap(Constant.HAZELCAST_EPC_MAP_KEY);
            Set<String> keySet = iMap.keySet();
            Map<String, EndpointConfig> endpointConfigMap = Maps.newConcurrentMap();
            keySet.forEach(key -> endpointConfigMap.put(key, iMap.get(key)));
            endpointManager.updateEpcMap(endpointConfigMap);
        }

        vertx.setTimer(Constant.DELAY_LOAD_EPC, timerId -> {
            periodicLoadEpcTimerId = timerId;
            loadEpc();
        });
    }


    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);
    }
}
