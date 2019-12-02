package com.github.masterdxy.gateway.plugin.impl;

import com.github.masterdxy.gateway.common.Endpoint;
import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import com.github.masterdxy.gateway.plugin.PluginResult;
import com.github.masterdxy.gateway.utils.ContextUtils;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component @Lazy(value = false) public class AuthPlugin implements Plugin {
    private static Logger logger = LoggerFactory.getLogger(AuthPlugin.class);

    @Override public int order() {
        return -90;
    }

    @Override public boolean match(RoutingContext context) {
        Endpoint endpoint = ContextUtils.getEndpoint(context);
        return endpoint.isNeedAuth();
    }

    @Override public PluginResult execute(RoutingContext context, PluginChain chain) {
        logger.info("AuthPlugin...");
        return chain.execute();
    }
}
