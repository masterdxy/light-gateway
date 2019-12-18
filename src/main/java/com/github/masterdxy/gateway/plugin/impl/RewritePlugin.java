package com.github.masterdxy.gateway.plugin.impl;

import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import com.github.masterdxy.gateway.plugin.PluginResult;
import com.github.masterdxy.gateway.utils.ContextUtils;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

//for http upstream
@Component
@Lazy(value = false)
public class RewritePlugin implements Plugin {
	private static Logger logger = LoggerFactory.getLogger(RewritePlugin.class);
	
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
		logger.info("RewritePlugin...");
		return chain.execute();
	}
}
