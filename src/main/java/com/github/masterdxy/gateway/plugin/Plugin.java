package com.github.masterdxy.gateway.plugin;

import io.vertx.ext.web.RoutingContext;

public interface Plugin {


    boolean match(RoutingContext context);
}
