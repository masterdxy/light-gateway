package com.github.masterdxy.gateway.handler.plugin;

import com.alibaba.fastjson.JSON;
import com.github.masterdxy.gateway.plugin.PluginChain;
import com.github.masterdxy.gateway.plugin.PluginResult;
import com.google.gson.JsonObject;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/*
Execute plugin chain
 */
@Component
@Lazy(value = false)
public class PluginHandler implements Handler<RoutingContext> {
    private static Logger logger = LoggerFactory.getLogger(PluginHandler.class);

    @Override
    public void handle(RoutingContext context) {
        //Execute plugin chain here
        PluginChain chain = PluginChain.build(context);
        context.
                vertx().
                executeBlocking((Handler<Future<PluginResult>>) future -> {
                            try {
                                PluginResult result = chain.execute();
                                future.complete(result);
                            } catch (Exception e) {
                                if (e instanceof IllegalStateException){
                                    logger.error("execute plugin chain error cause no return data after plugin " +
                                            "executed.", e);
                                }
                                logger.error("execute plugin chain error", e);
                                future.fail(e);
                            }
                        },
                        asyncResult -> {
                            PluginResult result = asyncResult.result();
                            logger.info("finish running plugin chain, success :{}", !result.isError());
                            if (!asyncResult.succeeded()) {
                                logger.error("pluginChain execute error, cause : {}", asyncResult.cause());
                                context.fail(500, asyncResult.cause());
                            } else {
                                if (asyncResult.result().isError()) {
                                    logger.error("pluginChain execute error, msg : {}",
                                            result.getErrorMsg());
                                    context.response().setStatusCode(400).end(result.getErrorMsg());
                                } else {
                                    JsonObject jsonObject = new JsonObject();
                                    Object pluginResult = result.getData();
                                    jsonObject.addProperty("result", JSON.toJSONString(pluginResult));
                                    context.response().setStatusCode(200).end(jsonObject.toString());
                                }
                            }
                        });

    }
}
