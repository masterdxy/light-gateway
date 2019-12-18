package com.github.masterdxy.gateway.handler;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.handler.after.ResponseTimeHandler;
import com.github.masterdxy.gateway.handler.before.AccessLogHandler;
import com.github.masterdxy.gateway.handler.before.RequestParserHandler;
import com.github.masterdxy.gateway.handler.before.TraceHandler;
import com.github.masterdxy.gateway.handler.error.ErrorHandler;
import com.github.masterdxy.gateway.handler.manager.ManagerHandler;
import com.github.masterdxy.gateway.handler.plugin.PluginHandler;
import com.github.masterdxy.gateway.spring.SpringContext;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.micrometer.PrometheusScrapingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Handler组装与路由映射
 *
 * @author tomoyo
 */
@Component
@Lazy(value = false)
public class HandlerMapping {
	
	private static final Logger logger = LoggerFactory.getLogger(HandlerMapping.class);
	
	/**
	 * Handler Thread Model
	 * Global Before Handler (AccessLog,Trace...)  -->  Plugin Handler (Auth,Dubbo,Http,Hystrix...)  --> After Handler
	 * Event Loop                                  -->  Worker Pool                                  --> Event Loop
	 */
	public Handler<HttpServerRequest> getGatewayHandler (Vertx vertx) {
		Router router = Router.router(vertx);
		/* Api entry route */
		router.route(Constant.ROUTE_BASE_PATH).produces(Constant.APPLICATION_JSON).handler(BodyHandler.create(false))
		      /* Before Handler */.handler(SpringContext.instance(RequestParserHandler.class))
		      .handler(SpringContext.instance(TraceHandler.class))
		      .handler(SpringContext.instance(AccessLogHandler.class))
		      /* After Handler */.handler(SpringContext.instance(ResponseTimeHandler.class))
		      /* Plugin Handler */.handler(SpringContext.instance(PluginHandler.class))
		      /* Error Handler */.failureHandler(SpringContext.instance(ErrorHandler.class));
		
		return router;
	}
	
	public Handler<HttpServerRequest> getManagerHandler (Vertx vertx) {
		Router router = Router.router(vertx);
		String managerPrefix = "/internal";
		/* Dynamic manage gateway*/
		router.get(managerPrefix + "/check_startup").handler(SpringContext.instance(ManagerHandler.class))
		      .failureHandler(SpringContext.instance(ErrorHandler.class));
		router.get(managerPrefix + "/log_setup").handler(null);
		/* Prometheus metrics export*/
		router.route(managerPrefix + "/metrics").handler(PrometheusScrapingHandler.create());
		return router;
	}
}
