package com.github.masterdxy.gateway.plugin.impl;

import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

@Component
public class LoadBalancePlugin implements Plugin {

    @Override
    public int order() {
        return -60;
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
