package com.github.masterdxy.gateway.common;

import com.github.masterdxy.gateway.plugin.impl.ratelimit.RateLimitPolicy;
import org.jfaster.mango.annotation.Getter;
import org.jfaster.mango.annotation.ID;
import org.jfaster.mango.annotation.Setter;
import org.jfaster.mango.invoker.function.enums.EnumToStringFunction;
import org.jfaster.mango.invoker.function.enums.StringToEnumFunction;

import java.io.Serializable;

/**
 * config rate limiter params , RateLimitPlugin use.
 *
 * @author tomoyo
 */ //
public class RateLimit implements Serializable {
	
	@ID
	private Long id;
	
	private Long endpointId;
	
	/**
	 * URL
	 */
	private RateLimitPolicy limitPolicy;
	//capacity of init token size
	private int             overdraft;
	
	//size of greed-manner refill token
	private int greedTokenSize;
	
	//period of refill in second
	private int refillSecond;
	
	public Long getId () {
		return id;
	}
	
	public void setId (Long id) {
		this.id = id;
	}
	
	public int getOverdraft () {
		return overdraft;
	}
	
	public void setOverdraft (int overdraft) {
		this.overdraft = overdraft;
	}
	
	public int getGreedTokenSize () {
		return greedTokenSize;
	}
	
	public void setGreedTokenSize (int greedTokenSize) {
		this.greedTokenSize = greedTokenSize;
	}
	
	public int getRefillSecond () {
		return refillSecond;
	}
	
	public void setRefillSecond (int refillSecond) {
		this.refillSecond = refillSecond;
	}
	
	@Getter(EnumToStringFunction.class)
	public RateLimitPolicy getLimitPolicy () {
		return limitPolicy;
	}
	
	@Setter(StringToEnumFunction.class)
	public void setLimitPolicy (RateLimitPolicy limitPolicy) {
		this.limitPolicy = limitPolicy;
	}
	
	public Long getEndpointId () {
		return endpointId;
	}
	
	public void setEndpointId (Long endpointId) {
		this.endpointId = endpointId;
	}
}
