package com.github.masterdxy.gateway.handler;

import com.github.masterdxy.gateway.handler.after.ResponseTimeHandler;
import com.github.masterdxy.gateway.handler.before.AccessLogHandler;
import com.github.masterdxy.gateway.handler.before.RequestParserHandler;
import com.github.masterdxy.gateway.handler.before.TraceHandler;
import com.github.masterdxy.gateway.handler.error.ErrorHandler;
import com.github.masterdxy.gateway.handler.plugin.PluginHandler;
import com.github.masterdxy.gateway.spring.SpringContext;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HandlerMapping {

    private static final Logger logger = LoggerFactory.getLogger(HandlerMapping.class);
    /*

    Global Before Handler (AccessLog,Trace...)  -->  Plugin Handler (Auth,Dubbo,Http,Hystrix...)  --> After Handler
    Event Loop                                  -->  Worker Pool                                  --> Event Loop
     */

    public Handler<HttpServerRequest> getHandler(Vertx vertx) {
        Router router = Router.router(vertx);
        //Load route mapping from nacos config center.
        //UpstreamHandler Endpoint.

        router.post("/api/*").
                produces("application/json").
                consumes("application/json").
                handler(BodyHandler.create(false)).
                handler(SpringContext.instance(RequestParserHandler.class)).
                handler(SpringContext.instance(TraceHandler.class)).          //Before Handler
                handler(SpringContext.instance(AccessLogHandler.class)).
                handler(SpringContext.instance(PluginHandler.class)).         //Plugin Handler
                handler(SpringContext.instance(ResponseTimeHandler.class)).   //AfterHandler
                failureHandler(SpringContext.instance(ErrorHandler.class));   //Error Handler


        router.get("/mgr/*").handler((context -> {
            logger.info("=== MGR /* ");
            context.next();
        }));
        return router;
    }
}
