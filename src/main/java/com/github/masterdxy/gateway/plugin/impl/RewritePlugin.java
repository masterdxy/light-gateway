package com.github.masterdxy.gateway.plugin.impl;

import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import io.vertx.ext.web.RoutingContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

//for http upstream
@Component
@Lazy(value = false)
public class RewritePlugin implements Plugin {

    @Override
    public int order() {
        return -70;
    }

    @Override
    public boolean match(RoutingContext context) {
        //only match http upstream
        return false;
    }

    @Override
    public boolean execute(RoutingContext context, PluginChain chain) {
        return false;
    }
}
