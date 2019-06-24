package com.github.masterdxy.gateway.plugin.impl;

import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import io.vertx.ext.web.RoutingContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(value = false)
public class RateLimitPlugin implements Plugin {

    @Override
    public int order() {
        return -80;
    }

    @Override
    public boolean match(RoutingContext context) {
        return false;
    }

    @Override
    public boolean execute(RoutingContext context, PluginChain chain) {
        return false;
    }
}
