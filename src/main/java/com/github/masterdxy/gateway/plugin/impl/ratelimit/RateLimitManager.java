package com.github.masterdxy.gateway.plugin.impl.ratelimit;

import com.github.masterdxy.gateway.common.RateLimit;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author tomoyo
 */
@Component
public class RateLimitManager {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitManager.class);
    private AtomicReference<Map<Long, RateLimit>> mapAtomicReference = new AtomicReference<>(Maps.newConcurrentMap());

    public RateLimitManager() {}

    public void updateRateLimit(Map<Long, RateLimit> dataMap) {
        Objects.requireNonNull(dataMap);
        logger.info("update rate limit data map size :{}", dataMap.size());
        mapAtomicReference.compareAndSet(mapAtomicReference.get(), dataMap);
    }

    RateLimit getRateLimit(Long epcId) {
        Objects.requireNonNull(epcId);
        return mapAtomicReference.get().get(epcId);
    }
}
