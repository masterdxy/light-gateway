package com.github.masterdxy.gateway.plugin.impl.mock;

import com.github.masterdxy.gateway.common.Endpoint;
import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import com.github.masterdxy.gateway.plugin.PluginResult;
import com.github.masterdxy.gateway.utils.ContextUtils;
import io.vertx.ext.web.RoutingContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(value = false)
public class MockPlugin implements Plugin {
    @Override
    public int order() {
        return -99;
    }

    @Override
    public boolean match(RoutingContext context) {
        Endpoint endpoint = ContextUtils.getEndpoint(context);
        return endpoint.isMock();
    }

    private String mockJsonStr = "{\"error_code\":\"20000\",\"error_msg\":\"this is mock data.\"}";

    @Override
    public PluginResult execute(RoutingContext context, PluginChain chain) {
        //Get mock data from storage.
        //TODO
        return PluginResult.success(mockJsonStr);
    }
}
