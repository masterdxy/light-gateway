package com.github.masterdxy.gateway.plugin.impl.ratelimit;

import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import com.github.masterdxy.gateway.plugin.PluginResult;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component @Lazy(value = false) public class RateLimitPlugin implements Plugin {

    private static Logger logger = LoggerFactory.getLogger(RateLimitPlugin.class);
    private volatile Bucket bucket;

    @Override public int order() {
        return -80;
    }

    @Override public boolean match(RoutingContext context) {
        //todo use RateLimit config find by epcId here.
        return false;
    }

    //limit the incoming api call not the outgoing proxy forward call.
    @Override public PluginResult execute(RoutingContext context, PluginChain chain) {
        logger.info("RateLimit...");
        ConsumptionProbe consumptionProbe = getOrCreateBucket().tryConsumeAndReturnRemaining(1);
        logger.info("Consumed : {}, Remain token :{}", consumptionProbe.isConsumed(),
            consumptionProbe.getRemainingTokens());
        if (consumptionProbe.isConsumed()) {
            return chain.execute();
        } else {
            logger.warn("RateLimit is fired.", bucket.getAvailableTokens());
            return PluginResult.fail("RateLimit Open");
        }
    }

    //TODO switch to hazelcast bucket impl.
    //TODO url relation.
    private Bucket getOrCreateBucket() {
        if (bucket != null) {
            return bucket;
        }
        long overdraft = 2;
        Refill refill = Refill.greedy(1, Duration.ofSeconds(1));
        Bandwidth limit = Bandwidth.classic(overdraft, refill);
        bucket = Bucket4j.builder().addLimit(limit).build();
        return bucket;
    }

}
