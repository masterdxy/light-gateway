package com.github.masterdxy.gateway.handler.error;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.masterdxy.gateway.protocol.v1.GatewayResponse;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Global error handler
 *
 * @author tomoyo
 */
@Component
@Lazy(value = false)
public class ErrorHandler implements Handler<RoutingContext> {
	
	@NacosValue("${gateway.debug:true}")
	private boolean debug;
	
	@NacosValue("${gateway.error.msg:Server Error}")
	private String errorMsg;
	
	private static Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
	
	@Override
	public void handle (RoutingContext context) {
		Throwable error = context.failure();
		if (error != null) {
			if (debug) {
				errorMsg = error.getMessage();
			}
		}
		logger.error("handle error", error);
		context.response().end(GatewayResponse.asErrorJson(errorMsg));
	}
}
