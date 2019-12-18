package com.github.masterdxy.gateway.handler.manager;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author tomoyo
 */
@Component
@Lazy(value = false)
public class ManagerHandler implements Handler<RoutingContext> {
	
	@Override
	public void handle (RoutingContext event) {
	
	}
}
