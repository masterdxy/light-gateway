package com.github.masterdxy.gateway.handler.after;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

@Component
public class ResponseTimeHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext context) {
        //Log response time in header and logback.
        context.next();
    }
}
