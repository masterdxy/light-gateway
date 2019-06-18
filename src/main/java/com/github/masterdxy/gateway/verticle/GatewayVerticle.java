package com.github.masterdxy.gateway.verticle;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.masterdxy.gateway.handler.HandlerMapping;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(value = SCOPE_PROTOTYPE)
public class GatewayVerticle extends AbstractVerticle {

    private static Logger log = LoggerFactory.getLogger(GatewayVerticle.class);

    @Autowired
    private HandlerMapping handlerMapping;

    @NacosValue("${gateway.http.port}")
    private int listenPort;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        //Build router and handlers
        Handler<HttpServerRequest> handler = handlerMapping.getHandler(vertx);
        vertx.createHttpServer()
                .requestHandler(handler)
                .listen(listenPort, (httpServerAsyncResult -> {
                    if (httpServerAsyncResult.succeeded())
                        startFuture.complete();
                    else
                        startFuture.fail(httpServerAsyncResult.cause());
                }));
    }

}
