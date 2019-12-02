package com.github.masterdxy.gateway.handler.after;

import com.github.masterdxy.gateway.common.Constant;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author tomoyo
 */
@Component @Lazy(value = false) public class ResponseTimeHandler implements Handler<RoutingContext> {

    private static Logger logger = LoggerFactory.getLogger(ResponseTimeHandler.class);

    @Override public void handle(RoutingContext context) {
        //Log response time in header and logback.
        long start = System.nanoTime();
        context.addHeadersEndHandler((v) -> {
            long duration = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
            logger.info("Response time :{}ms", duration);
            context.response().putHeader(Constant.RESPONSE_HEADER_KEY_TIME, duration + "ms");
        });
        context.next();
    }
}
