package com.github.masterdxy.gateway.handler;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import org.springframework.stereotype.Component;

@Component
public class HandlerMapping {

    public Handler<HttpServerRequest> getHandler(Vertx vertx) {
        Router router = Router.router(vertx);
        //Load route mapping from nacos config center.
        //Build handler.

        router.route().handler(LoggerHandler.create());
        router.get("/*").handler(RoutingContext::next);
        return router;
    }
}
