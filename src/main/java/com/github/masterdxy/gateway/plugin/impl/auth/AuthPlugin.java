package com.github.masterdxy.gateway.plugin.impl.auth;

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

import static com.github.masterdxy.gateway.common.Constant.AUTH_RESULT_KEY;
import static com.github.masterdxy.gateway.common.Constant.AUTH_SUCCESS;

/**
 * @author tomoyo
 */
@Component
@Lazy(value = false)
public class AuthPlugin implements Plugin {
	private static Logger logger = LoggerFactory.getLogger(AuthPlugin.class);
	
	@Autowired
	private SSOAuthProvider ssoAuthProvider;
	
	@Override
	public int order () {
		return -90;
	}
	
	@Override
	public boolean match (RoutingContext context) {
		Endpoint endpoint = ContextUtils.getEndpoint(context);
		return endpoint.isNeedAuth();
	}
	
	@Override
	public PluginResult execute (RoutingContext context, PluginChain chain) {
		logger.info("AuthPlugin...");
		GatewayRequest request = context.get(Constant.GATEWAY_REQUEST_KEY);
		AuthResult authResult = ssoAuthProvider.doAuth(request.getToken());
		if (authResult.getAuthCode() != AUTH_SUCCESS) {
			return PluginResult.fail("Auth failed.");
		}
		context.put(AUTH_RESULT_KEY, authResult);
		return chain.execute();
	}
}
