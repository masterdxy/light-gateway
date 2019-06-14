package com.github.masterdxy.gateway.handler.plugin;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

/*
Execute plugin chain
 */
@Component
public class PluginHandler implements Handler<RoutingContext> {


    @Override
    public void handle(RoutingContext context) {
        //Execute plugin chain here
        context.next();
    }
}
