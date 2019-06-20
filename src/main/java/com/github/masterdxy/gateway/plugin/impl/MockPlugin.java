package com.github.masterdxy.gateway.plugin.impl;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.common.EndpointConfig;
import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import io.vertx.ext.web.RoutingContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Lazy(value = false)
public class MockPlugin implements Plugin {
    @Override
    public int order() {
        return -99;
    }

    @Override
    public boolean match(RoutingContext context) {
        EndpointConfig endpointConfig = Objects.requireNonNull(context.get(Constant.ENDPOINT_CONFIG));
        return endpointConfig.isMock();
    }

    @Override
    public boolean execute(RoutingContext context, PluginChain chain) {
        return false;
    }
}
