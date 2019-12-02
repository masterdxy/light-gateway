package com.github.masterdxy.gateway.handler.before;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component @Lazy(value = false) public class TraceHandler implements Handler<RoutingContext> {

    @Override public void handle(RoutingContext context) {
        //add trace id or span trace id from client.  Protocol.TRACE_HEADER
        context.next();
    }
}
