package com.github.masterdxy.gateway.plugin.impl.loadbalance;

import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import com.github.masterdxy.gateway.plugin.PluginResult;
import com.github.masterdxy.gateway.utils.ContextUtils;
import io.vertx.ext.web.RoutingContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(value = false)
public class LoadBalancePlugin implements Plugin {
	
	@Override
	public int order () {
		return -70;
	}
	
	@Override
	public boolean match (RoutingContext context) {
		return ContextUtils.isHttp(context);
	}
	
	@Override
	public PluginResult execute (RoutingContext context, PluginChain chain) {
		return null;
	}
}
