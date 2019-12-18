package com.github.masterdxy.gateway.plugin.impl.dubbo;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.common.Endpoint;
import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import com.github.masterdxy.gateway.plugin.PluginResult;
import com.github.masterdxy.gateway.protocol.v1.GatewayRequest;
import com.github.masterdxy.gateway.utils.ContextUtils;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(value = false)
public class DubboPlugin implements Plugin {
	
	private static final Logger logger = LoggerFactory.getLogger(DubboPlugin.class);
	
	@Autowired
	private DubboServiceCache dubboServiceCache;
	
	@Override
	public int order () {
		return -60;
	}
	
	@Override
	public boolean match (RoutingContext context) {
		return ContextUtils.isDubbo(context);
	}
	
	@Override
	public PluginResult execute (RoutingContext context, PluginChain chain) {
		//invoke rpc in worker thread pool;
		//add result into context;
		//invoke chain next;
		Endpoint endpoint = ContextUtils.getEndpoint(context);
		GatewayRequest request = context.get(Constant.GATEWAY_REQUEST_KEY);
		DubboServiceIdentity serviceIdentity =
			DubboServiceIdentity.as(endpoint.getUpstreamUrl(), endpoint.getVersion());
		DubboProxyService service = dubboServiceCache.getDubboService(serviceIdentity);
		if (service == null) {
			return PluginResult.fail("service not found");
		}
		Object object = service.invoke(request.getMethod(), request.getData());
		logger.info("DubboPlugin execute result : {}", object);
		return PluginResult.success(object);
	}
}
