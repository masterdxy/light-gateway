package com.github.masterdxy.gateway;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayVerticle extends AbstractVerticle {

    private static Logger log = LoggerFactory.getLogger(GatewayVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);
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


    /*
    For local test only.
     */
    public static void main(String[] args) {
        //Build ApplicationContext before vertx startup.
        //ApplicationContext context = xx;
        Vertx v = Vertx.vertx();
        //Use Nacos config vertx.
        v.deployVerticle(new GatewayVerticle());
        log.info("Started.");
    }
}
