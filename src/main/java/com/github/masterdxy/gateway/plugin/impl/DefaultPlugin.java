package com.github.masterdxy.gateway.plugin.impl;

import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import com.github.masterdxy.gateway.plugin.PluginResult;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(value = false)
public class DefaultPlugin implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPlugin.class);

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean match(RoutingContext context) {
        return true;
    }

    @Override
    public PluginResult execute(RoutingContext context, PluginChain chain) {
        logger.warn("plugin chain execute finish but no result returned.");
        return PluginResult.success(StringUtils.EMPTY);
    }
}
