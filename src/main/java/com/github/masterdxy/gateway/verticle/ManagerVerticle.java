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
import org.springframework.stereotype.Component;

@Component
public class ManagerVerticle extends AbstractVerticle {
	
	//ManagerVerticle  manage cluster of gateway.
	//Init hazelcast for response cache and ratelimiter,sub redis some channel, and push manage event,like endpoint
    // changes.
	//Handle ThreadDump and HeadDump req
	
	//1.✅Init cluster with hazelcast. Or redis.
	//2.✅Load endpoint config and others with distributed lock from remote db. (jdbc)
	//3.Register manager endpoint e.g. /thread_dump and listen. (web)
	//4.Watch config changes by period retrieve remote db, send msg through event bus.(timer)
	
	private static Logger logger = LoggerFactory.getLogger(ManagerVerticle.class);
	
	@Autowired
	private HandlerMapping handlerMapping;
	
	@NacosValue("${gateway.bind.manager.port:8081}")
	private int    bindPort;
	@NacosValue("${gateway.bind.host:}")
	private String bindHost;
	
	@Override
	public void start (Future<Void> startFuture) throws Exception {
		//Build router and handlers
		Handler<HttpServerRequest> handler = handlerMapping.getManagerHandler(vertx);
		//TODO use hazelcast address picker
		if (StringUtils.isEmpty(bindHost)) {
			bindHost = AddressUtils.getLocalIpAddress();
			logger.warn("bind host is null, pick :{}", bindHost);
		}
		SocketAddress bindAddress = SocketAddress.inetSocketAddress(bindPort, bindHost);
		logger.info("Manager is bind to {}", bindAddress.toString());
		vertx.createHttpServer().requestHandler(handler).listen(bindAddress, (httpServerAsyncResult -> {
			if (httpServerAsyncResult.succeeded()) {
				startFuture.complete();
			}
			else {
				startFuture.fail(httpServerAsyncResult.cause());
			}
		}));
	}
	
	@Override
	public void stop (Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
		
	}
}
