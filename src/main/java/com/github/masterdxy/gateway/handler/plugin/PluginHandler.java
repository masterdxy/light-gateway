package com.github.masterdxy.gateway.handler.plugin;

import com.alibaba.fastjson.JSON;
import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.plugin.PluginChain;
import com.google.gson.JsonObject;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/*
Execute plugin chain
 */
@Component
public class PluginHandler implements Handler<RoutingContext> {
    private static Logger logger = LoggerFactory.getLogger(PluginHandler.class);

    @Override
    public void handle(RoutingContext context) {
        //Execute plugin chain here
        PluginChain chain = PluginChain.build(context);
        context.
                vertx().
                executeBlocking((Handler<Future<Boolean>>) future -> {
                            logger.info("Running blocking code [chain.execute()]");
                            boolean result = chain.execute();
                            future.complete(result);
                        },
                        asyncResult -> {
                            logger.info("Finish running blocking code [chain.execute()] ,successed :{}", asyncResult.succeeded());
                            if (!asyncResult.succeeded()) {
                                logger.error("PluginChain execute failed, cause : {}", asyncResult.cause());
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("err_msg", asyncResult.cause().getMessage());
                                context.response().setStatusCode(400).end(jsonObject.toString());
                            } else {
                                if (asyncResult.result()) {
                                    JsonObject jsonObject = new JsonObject();
                                    Object pluginResult = context.get(Constant.PLUGIN_RESULT_KEY);
                                    if (pluginResult != null) {
                                        jsonObject.addProperty("result", JSON.toJSONString(pluginResult));
                                    }
                                    context.response().setStatusCode(200).end(jsonObject.toString());
                                } else {
                                    String pluginResult = context.get(Constant.PLUGIN_ERROR_MESSAGE_KEY);
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("err_msg", pluginResult);
                                    context.response().setStatusCode(400).end(jsonObject.toString());
                                }
                            }
                        });

    }
}
