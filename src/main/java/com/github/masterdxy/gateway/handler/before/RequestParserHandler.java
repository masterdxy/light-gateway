package com.github.masterdxy.gateway.handler.before;

import com.alibaba.fastjson.JSON;
import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.config.VertxInitialization;
import com.github.masterdxy.gateway.protocol.v1.GatewayRequest;
import com.github.masterdxy.gateway.protocol.v1.GatewayResponse;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component @Lazy(value = false) public class RequestParserHandler implements Handler<RoutingContext> {

    private static Logger logger = LoggerFactory.getLogger(RequestParserHandler.class);

    @Override public void handle(RoutingContext context) {
        if (!VertxInitialization.isStarted()) {
            context.response().setStatusCode(Constant.RESPONSE_STATUS_OK)
                .end(GatewayResponse.asErrorJson("Gateway is starting"));
            return;
        }
        if (context.request().method() == HttpMethod.POST) {
            String data = context.getBodyAsString();
            if (StringUtils.isNotEmpty(data)) {
                GatewayRequest request = JSON.parseObject(data, GatewayRequest.class);
                //remove /api prefix
                request.setExtraUrl(context.request().path().substring(Constant.ROUTE_BASE_PATH_LENGTH));
                logger.info("Parse gateway request : {}", data);
                context.put(Constant.GATEWAY_REQUEST_KEY, request);
                context.next();
            } else {
                context.response().end(GatewayResponse.asErrorJson("POST data empty."));
            }
        } else {
            //todo parse GET url params to gwr.
            context.response().end(GatewayResponse.asErrorJson("Only support POST method."));
        }
    }
}
