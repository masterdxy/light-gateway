package com.github.masterdxy.gateway.plugin.impl;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.common.Endpoint;
import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import com.github.masterdxy.gateway.plugin.PluginResult;
import com.github.masterdxy.gateway.plugin.endpoint.EndpointMatcher;
import com.github.masterdxy.gateway.protocol.v1.GatewayRequest;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component @Lazy(value = false) public class EndpointSelectorPlugin implements Plugin {

    private static Logger logger = LoggerFactory.getLogger(EndpointSelectorPlugin.class);

    @Autowired private EndpointMatcher endpointMatcher;

    @Override public int order() {
        return -100;
    }

    @Override public boolean match(RoutingContext context) {
        //All request is match for EndpointSelector
        return true;
    }

    @Override public PluginResult execute(RoutingContext context, PluginChain chain) {
        //request is never null here.
        GatewayRequest request = Objects.requireNonNull(context.get(Constant.GATEWAY_REQUEST_KEY));
        //resolve upstream type
        Optional<Endpoint> endpoint = endpointMatcher.match(request);

        if (!endpoint.isPresent()) {
            logger.warn("EndpointSelector :{}, requestUri :{}", "Endpoint not found", context.request().absoluteURI());
            return PluginResult.fail("Endpoint not found");
        }
        //set endpoint config into context
        context.put(Constant.ENDPOINT_CONFIG, endpoint.get());

        return chain.execute();
    }
}
