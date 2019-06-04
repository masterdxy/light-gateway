package com.github.masterdxy.gateway.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
todo use verticle factory to create this verticle
 */
public class GatewayVerticle extends AbstractVerticle {

    private static Logger log = LoggerFactory.getLogger(GatewayVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        //Build router and handlers
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/*").handler((context) -> {
            log.info("Handle GET : {}", context.request().absoluteURI());
            context.next();
        });

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080, (httpServerAsyncResult -> {
                    if (httpServerAsyncResult.succeeded())
                        startFuture.complete();
                    else
                        startFuture.fail(httpServerAsyncResult.cause());
                }));
    }

}
