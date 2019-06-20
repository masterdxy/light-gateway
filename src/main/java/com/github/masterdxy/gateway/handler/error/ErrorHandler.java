package com.github.masterdxy.gateway.handler.error;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.masterdxy.gateway.protocol.v1.ResponseBuilder;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(value = false)
public class ErrorHandler implements Handler<RoutingContext> {

    @NacosValue("${gateway.error.detail:true}")
    private boolean showDetailMsg;

    @NacosValue("${gateway.error.msg:Server Error}")
    private String errorMsg;

    private static Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    @Override
    public void handle(RoutingContext context) {
        Throwable error = context.failure();
        //http status code is always 200.
        int statusCode = context.statusCode();
        logger.info("Error : {}", error != null ? error.getMessage() : "Unknown");
        if (error != null) {
            if (showDetailMsg) {
                errorMsg = error.getMessage();
            }
        }
        context.response().end(ResponseBuilder.buildJson(statusCode, errorMsg, null));
    }
}
