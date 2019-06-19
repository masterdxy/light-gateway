package com.github.masterdxy.gateway.common;

//(version in DTO)
//Parse config file e.g.  /api/crud --> dubbo --> com.xxx.CRUDService --> auth   --> LB_DEF  --> Rate --> Sign
//                        /api/echo --> http  --> http://127.0.0.1:8001 -> no auth --> LB_DEF -->  Rate --> Sign
public class EndpointConfig {
    private String uri;
    private String upstreamType;
    private String upstreamUrl;
    private String loadBalanceConfig;
    private String rateLimiterConfig;
    private boolean isNeedAuth;
    private boolean isNeedSign;
}
