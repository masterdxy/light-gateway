package com.github.masterdxy.gateway.task.loader;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.common.RateLimit;
import com.github.masterdxy.gateway.common.dao.RateLimitDao;
import com.github.masterdxy.gateway.plugin.impl.ratelimit.RateLimitManager;
import com.github.masterdxy.gateway.task.TaskRegistry;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.jfaster.mango.operator.Mango;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.masterdxy.gateway.common.Constant.HAZELCAST_RATE_LIMIT_MAP_KEY;

/**
 * @author tomoyo
 */
@Component
public class RateLimitLoader extends AbstractScheduledService implements TaskRegistry.Task {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitLoader.class);

    @Autowired
    private HazelcastInstance hazelcastInstance;
    @Autowired
    private Mango mango;
    @Autowired
    private MasterWriteLocker masterWriteLocker;
    @Autowired
    private RateLimitManager rateLimitManager;

    private RateLimitDao rateLimitDao;

    @Override
    public String name() {
        return "rate-limit-data-loader";
    }

    @Override
    public void stop() {
        super.stopAsync();
    }

    @Override
    public int order() {
        return 5;
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(Constant.DELAY_LOAD_EPC, Constant.DELAY_LOAD_EPC, TimeUnit.MILLISECONDS);
    }

    @Override
    public void startAfterRunOnce() throws Exception {
        startAsync().awaitRunning();
    }

    @Override
    protected void startUp() throws Exception {
        runOneIteration();
    }

    @Override
    protected void runOneIteration() throws Exception {
        fetchRateLimitData();
    }

    /**
     * retrieve data from database and cache into cache.
     */
    private void fetchRateLimitData() {
        if (masterWriteLocker.isHasMasterLock()) {
            logger.info("[RateLimit] Fetch RateLimit Config from [MySQL], with MasterWriteLock=[{}]",
                masterWriteLocker.isHasMasterLock());
            if (rateLimitDao == null) {
                rateLimitDao = mango.create(RateLimitDao.class);
            }
            List<RateLimit> allRateLimit = rateLimitDao.getAll();
            Map<Long, RateLimit> rateLimitHashMap = Maps.newHashMap();
            if (allRateLimit != null) {
                allRateLimit.forEach(rateLimit -> {
                    rateLimitHashMap.put(rateLimit.getEndpointId(), rateLimit);
                });
            }
            IMap<Long, RateLimit> hazelMap = hazelcastInstance.getMap(HAZELCAST_RATE_LIMIT_MAP_KEY);
            hazelMap.clear();
            hazelMap.putAll(rateLimitHashMap);
        } else {
            logger.info("[RateLimit] Fetch RateLimit Config from [HAZELCAST], with MasterWriteLock=[{}]",
                masterWriteLocker.isHasMasterLock());
            IMap<Long, RateLimit> hazelcastInstanceMap = hazelcastInstance.getMap(HAZELCAST_RATE_LIMIT_MAP_KEY);
            Map<Long, RateLimit> rateLimitConcurrentMap = Maps.newConcurrentMap();
            hazelcastInstanceMap.keySet()
                .forEach(key -> rateLimitConcurrentMap.put(key, hazelcastInstanceMap.get(key)));
            rateLimitManager.updateRateLimit(rateLimitConcurrentMap);
        }
    }

}
