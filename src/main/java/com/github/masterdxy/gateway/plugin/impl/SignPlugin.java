package com.github.masterdxy.gateway.plugin.impl;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.common.Endpoint;
import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import com.github.masterdxy.gateway.plugin.PluginResult;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component @Lazy(value = false) public class SignPlugin implements Plugin {

    private static Logger logger = LoggerFactory.getLogger(SignPlugin.class);

    @Override public int order() {
        return -95;
    }

    @Override public boolean match(RoutingContext context) {
        Endpoint endpoint = Objects.requireNonNull(context.get(Constant.ENDPOINT_CONFIG));
        return endpoint.isNeedSign();
    }

    @Override public PluginResult execute(RoutingContext context, PluginChain chain) {
        logger.info("SignPlugin...");
        return chain.execute();
    }
}
