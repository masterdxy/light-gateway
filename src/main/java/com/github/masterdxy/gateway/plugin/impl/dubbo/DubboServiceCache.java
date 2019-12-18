package com.github.masterdxy.gateway.plugin.impl.dubbo;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentMap;

@Component
public class DubboServiceCache {
	
	private static Logger logger = LoggerFactory.getLogger(DubboServiceCache.class);
	
	private ConcurrentMap<DubboServiceIdentity,DubboProxyService> cache = Maps.newConcurrentMap();
	
	//only use cache, service init by locatingDubboService()
	DubboProxyService getDubboService (DubboServiceIdentity serviceIdentity) {
		return cache.get(serviceIdentity);
	}
	
	public ConcurrentMap<DubboServiceIdentity,DubboProxyService> getCache () {
		return cache;
	}
}
