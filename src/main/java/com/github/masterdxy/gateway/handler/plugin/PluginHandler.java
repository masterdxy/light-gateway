package com.github.masterdxy.gateway.handler.plugin;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.plugin.PluginChain;
import com.github.masterdxy.gateway.plugin.PluginResult;
import com.github.masterdxy.gateway.protocol.v1.GatewayResponse;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/*
 * Execute plugin chain
 */
@Component
@Lazy(value = false)
public class PluginHandler implements Handler<RoutingContext> {
    private static Logger logger = LoggerFactory.getLogger(PluginHandler.class);

    @NacosValue("${gateway.debug:true}")
    private boolean debug;

    @Override
    public void handle(RoutingContext context) {
        // Execute plugin chain here
        PluginChain chain = PluginChain.build(context);
        context.vertx().executeBlocking((Handler<Future<PluginResult>>)future -> {
            try {
                PluginResult result = chain.execute();
                future.complete(result);
            } catch (Exception e) {
                future.fail(e);
            }
        }, asyncResult -> {
            logger.info("plugin chain async result success :{}", asyncResult.succeeded());
            PluginResult result = asyncResult.result();
            if (asyncResult.succeeded()) {
                context.response().setStatusCode(Constant.RESPONSE_STATUS_OK);
                if (asyncResult.result().isError()) {
                    logger.error("plugin chain execute has error, msg : {}", result.getErrorMsg());
                    context.response()
                        .end(debug ? GatewayResponse.asErrorJson(result.getErrorMsg()) : StringUtils.EMPTY);
                } else {
                    context.response().end(GatewayResponse.asSuccessJson(result.getData()));
                }
            } else {
                // exception
                logger.error("plugin chain has exception", asyncResult.cause());
                context.fail(Constant.RESPONSE_STATUS_ERROR, asyncResult.cause());
            }
        });

    }

}
