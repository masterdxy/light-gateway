package com.github.masterdxy.gateway.plugin.impl.ratelimit;

/**
 * @author tomoyo
 */

public enum RateLimitPolicy {

    /**
     * URL Policy
     */
    URL("URL"),

    /**
     * USER Policy
     */
    USER("USER"),

    /**
     * IP Policy
     */
    REMOTE_IP("IP");

    private String value;

    RateLimitPolicy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
