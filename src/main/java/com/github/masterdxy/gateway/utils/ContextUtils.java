package com.github.masterdxy.gateway.utils;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.common.Endpoint;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;

public class ContextUtils {

    public static Endpoint getEndpoint(RoutingContext context) {
        return Objects.requireNonNull(context.get(Constant.ENDPOINT_CONFIG));
    }

    public static boolean isHttp(RoutingContext context) {
        Endpoint endpoint = getEndpoint(context);
        return Constant.PROTOCOL_HTTP.equalsIgnoreCase(endpoint.getUpstreamType());
    }

    public static boolean isDubbo(RoutingContext context) {
        Endpoint endpoint = getEndpoint(context);
        return Constant.PROTOCOL_DUBBO.equalsIgnoreCase(endpoint.getUpstreamType());
    }

}
