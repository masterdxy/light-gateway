package com.github.masterdxy.gateway.handler.before;

import com.github.masterdxy.gateway.protocol.Protocol;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component @Lazy(value = false) public class AccessLogHandler implements Handler<RoutingContext> {

    private static final Logger logger = LoggerFactory.getLogger(AccessLogHandler.class);
    //todo config
    private static String format = "--> Remote IP :{}, Request URI :{}, Protocol Version :{}";

    @Override public void handle(RoutingContext context) {
        logger.info(format, context.request().host(), context.request().absoluteURI(),
            context.request().getHeader(Protocol.VERSION_HEADER));
        context.next();
    }

}
