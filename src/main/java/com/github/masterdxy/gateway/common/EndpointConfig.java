package com.github.masterdxy.gateway.common;

//(version in DTO)
//Parse config file e.g.  /api/crud --> dubbo --> com.xxx.CRUDService --> auth   --> LB_DEF  --> Rate --> Sign
//                        /api/echo --> http  --> http://127.0.0.1:8001 -> no auth --> LB_DEF -->  Rate --> Sign
public class EndpointConfig {
    private String uri;
    private String upstreamType;                    //http or dubbo
    private String upstreamUrl;                     //http support , split urls.
    private boolean isNeedAuth;
    private boolean isNeedSign;
    private boolean isMock;
    private LoadBalanceConfig loadBalanceConfig;
    private RateLimitConfig rateLimiterConfig;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUpstreamType() {
        return upstreamType;
    }

    public void setUpstreamType(String upstreamType) {
        this.upstreamType = upstreamType;
    }

    public String getUpstreamUrl() {
        return upstreamUrl;
    }

    public void setUpstreamUrl(String upstreamUrl) {
        this.upstreamUrl = upstreamUrl;
    }

    public boolean isNeedAuth() {
        return isNeedAuth;
    }

    public void setNeedAuth(boolean needAuth) {
        isNeedAuth = needAuth;
    }

    public boolean isNeedSign() {
        return isNeedSign;
    }

    public void setNeedSign(boolean needSign) {
        isNeedSign = needSign;
    }

    public LoadBalanceConfig getLoadBalanceConfig() {
        return loadBalanceConfig;
    }

    public void setLoadBalanceConfig(LoadBalanceConfig loadBalanceConfig) {
        this.loadBalanceConfig = loadBalanceConfig;
    }

    public RateLimitConfig getRateLimiterConfig() {
        return rateLimiterConfig;
    }

    public void setRateLimiterConfig(RateLimitConfig rateLimiterConfig) {
        this.rateLimiterConfig = rateLimiterConfig;
    }

    public boolean isMock() {
        return isMock;
    }

    public void setMock(boolean mock) {
        isMock = mock;
    }
}
