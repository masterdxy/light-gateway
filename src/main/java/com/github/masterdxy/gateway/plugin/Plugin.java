package com.github.masterdxy.gateway.plugin;

import io.vertx.ext.web.RoutingContext;

public interface Plugin {

    int order();

    boolean match(RoutingContext context);

    boolean execute(RoutingContext context,PluginChain chain);
}
