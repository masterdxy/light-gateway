package com.github.masterdxy.gateway.plugin.impl;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import com.github.masterdxy.gateway.protocol.v1.GatewayRequest;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

@Component
public class EndpointSelectorPlugin implements Plugin {

    @Override
    public int order() {
        return -100;
    }


    @Override
    public boolean match(RoutingContext context) {
        //All request is match for EndpointSelector
        return true;
    }

    @Override
    public boolean execute(RoutingContext context, PluginChain chain) {
        GatewayRequest request = context.get(Constant.GATEWAY_REQUEST_KEY);
        if (request != null) {

        } else {
            context.put(Constant.PLUGIN_RESULT_KEY, "GatewayRequest is null");
        }
        return false;
    }
}
