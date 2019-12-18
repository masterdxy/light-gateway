package com.github.masterdxy.gateway.verticle;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.masterdxy.gateway.handler.HandlerMapping;
import com.github.masterdxy.gateway.utils.AddressUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.SocketAddress;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * @author tomoyo
 */
@Component
@Scope(value = SCOPE_PROTOTYPE)
public class GatewayVerticle extends AbstractVerticle {
	
	private static Logger log = LoggerFactory.getLogger(GatewayVerticle.class);
	
	@Autowired
	private HandlerMapping handlerMapping;
	
	@NacosValue("${gateway.bind.port:8080}")
	private int    bindPort;
	@NacosValue("${gateway.bind.host:}")
	private String bindHost;
	
	@Override
	public void start (Future<Void> startFuture) {
		//TODO use hazelcast address picker
		if (StringUtils.isEmpty(bindHost)) {
			bindHost = AddressUtils.getLocalIpAddress();
			log.warn("bind host is null, pick :{}", bindHost);
		}
		SocketAddress bindAddress = SocketAddress.inetSocketAddress(bindPort, bindHost);
		log.info("Gateway is bind to {}", bindAddress.toString());
		//Build router and handlers
		Handler<HttpServerRequest> handler = handlerMapping.getGatewayHandler(vertx);
		vertx.createHttpServer().requestHandler(handler).listen(bindAddress, (httpServerAsyncResult -> {
			if (httpServerAsyncResult.succeeded()) {
				startFuture.complete();
			}
			else {
				startFuture.fail(httpServerAsyncResult.cause());
			}
		}));
		
	}
	
}
