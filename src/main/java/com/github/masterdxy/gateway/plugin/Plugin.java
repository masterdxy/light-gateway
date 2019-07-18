package com.github.masterdxy.gateway.plugin;

import io.vertx.ext.web.RoutingContext;

public interface Plugin {

    int order();

    boolean match(RoutingContext context);

    PluginResult execute(RoutingContext context, PluginChain chain);
}
