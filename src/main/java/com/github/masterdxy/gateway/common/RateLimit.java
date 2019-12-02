package com.github.masterdxy.gateway.common;

import org.jfaster.mango.annotation.ID;

import java.io.Serializable;

//config rate limiter params , RateLimitPlugin use.
public class RateLimit implements Serializable {

    @ID private Long endpointId;
    //capacity of init token size
    private int overdraft;

    //size of greed-manner refill token
    private int greedTokenSize;

    //period of refill in second
    private int refillSecond;

    public Long getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(Long endpointId) {
        this.endpointId = endpointId;
    }

    public int getOverdraft() {
        return overdraft;
    }

    public void setOverdraft(int overdraft) {
        this.overdraft = overdraft;
    }

    public int getGreedTokenSize() {
        return greedTokenSize;
    }

    public void setGreedTokenSize(int greedTokenSize) {
        this.greedTokenSize = greedTokenSize;
    }

    public int getRefillSecond() {
        return refillSecond;
    }

    public void setRefillSecond(int refillSecond) {
        this.refillSecond = refillSecond;
    }
}
