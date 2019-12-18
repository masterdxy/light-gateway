package com.github.masterdxy.gateway.plugin.impl.ratelimit;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.common.Endpoint;
import com.github.masterdxy.gateway.common.RateLimit;
import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import com.github.masterdxy.gateway.plugin.PluginResult;
import com.github.masterdxy.gateway.plugin.impl.auth.AuthResult;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import io.github.bucket4j.grid.GridBucketState;
import io.github.bucket4j.grid.ProxyManager;
import io.github.bucket4j.grid.hazelcast.Hazelcast;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.function.Supplier;

import static com.github.masterdxy.gateway.common.Constant.HAZELCAST_RATE_LIMIT_BUCKET_MAP_KEY;

/**
 * @author tomoyo
 */
@Component
@Lazy(value = false)
public class RateLimitPlugin implements Plugin {
	
	private static Logger logger = LoggerFactory.getLogger(RateLimitPlugin.class);
	
	private ProxyManager<String> proxyManager;
	
	@Autowired
	private RateLimitManager rateLimitManager;
	
	public RateLimitPlugin (@Autowired HazelcastInstance hazelcastInstance) {
		IMap<String,GridBucketState> imap = hazelcastInstance.getMap(HAZELCAST_RATE_LIMIT_BUCKET_MAP_KEY);
		proxyManager = Bucket4j.extension(Hazelcast.class).proxyManagerForMap(imap);
	}
	
	@Override
	public int order () {
		return -80;
	}
	
	@Override
	public boolean match (RoutingContext context) {
		Endpoint endpoint = context.get(Constant.ENDPOINT_CONFIG);
		RateLimit rateLimit = rateLimitManager.getRateLimit(endpoint.getId());
		if (rateLimit == null) {
			return false;
		}
		context.put(Constant.RATE_LIMIT_KEY, rateLimit);
		return true;
	}
	
	/**
	 * Limit by remote ip/auth user/endpoint url. Always limit the incoming api call not the outgoing proxy forward
	 * call.
	 *
	 * @param context c
	 * @param chain   c
	 *
	 * @return p
	 */
	@Override
	public PluginResult execute (RoutingContext context, PluginChain chain) {
		logger.info("RateLimit...");
		Bucket bucket = getBucket(context);
		ConsumptionProbe consumptionProbe = bucket.tryConsumeAndReturnRemaining(1);
		logger.info("Consumed : {}, Remain token :{}",
		            consumptionProbe.isConsumed(),
		            consumptionProbe.getRemainingTokens()
		           );
		if (consumptionProbe.isConsumed()) {
			return chain.execute();
		}
		else {
			logger.warn("RateLimit is fired.", bucket.getAvailableTokens());
			return PluginResult.fail("RateLimit Open");
		}
	}
	
	private Bucket getBucket (RoutingContext context) {
		String bucketKey = getLimiterBucketKey(context);
		return proxyManager.getProxy(bucketKey, getBucketConfiguration(context));
	}
	
	private String getLimiterBucketKey (RoutingContext context) {
		RateLimit rateLimit = context.get(Constant.RATE_LIMIT_KEY);
		AuthResult authResult = context.get(Constant.AUTH_RESULT_KEY);
		switch (rateLimit.getLimitPolicy()) {
			case URL: {
				return "bkt_" + rateLimit.getLimitPolicy() + "_" + rateLimit.getEndpointId();
			}
			case USER: {
				if (authResult.getAuthCode() != Constant.AUTH_SUCCESS || StringUtils.isBlank(authResult.getUserId())) {
					throw new IllegalStateException("rate limit need authorized user id");
				}
				return "bkt_" + rateLimit.getLimitPolicy() + "_" + rateLimit.getEndpointId() + "_" + authResult
					                                                                                     .getUserId();
			}
			case REMOTE_IP: {
				return "bkt_" + rateLimit.getLimitPolicy() + "_" + rateLimit.getEndpointId() + "_" + context.request()
				                                                                                            .remoteAddress()
				                                                                                            .host();
			}
			default: {
				throw new IllegalStateException("rate limit policy not match");
			}
		}
	}
	
	private Supplier<BucketConfiguration> getBucketConfiguration (RoutingContext context) {
		RateLimit rateLimit = context.get(Constant.RATE_LIMIT_KEY);
		Refill refill = Refill.greedy(rateLimit.getGreedTokenSize(), Duration.ofSeconds(rateLimit.getRefillSecond()));
		Bandwidth limit = Bandwidth.classic(rateLimit.getOverdraft(), refill);
		return () -> new BucketConfiguration(Collections.singletonList(limit));
	}
}
