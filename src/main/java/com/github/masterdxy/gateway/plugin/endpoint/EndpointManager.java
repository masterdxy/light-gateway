package com.github.masterdxy.gateway.plugin.endpoint;

import com.github.masterdxy.gateway.common.Endpoint;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class EndpointManager {
	
	// ✅resolve endpoint config from datasource.
	// ✅watch endpoint changes.


    /*
    dubbo-service-proxy
    client: /crud , namespace=xxx-app version=1.0.0  method=findXX

    http-reserve-proxy
    client:/crud,  namespace=yyy-app version=1.2.0  method=findXX
     */
	
	private AtomicReference<Map<String,Endpoint>> mapAtomicReference = new AtomicReference<>(Maps.newConcurrentMap());
	
	private static Logger logger = LoggerFactory.getLogger(EndpointManager.class);
	
	public void updateEpcMap (Map<String,Endpoint> map) {
		Objects.requireNonNull(map);
		logger
			.info("updating endpoint map, before size :{}, now size :{}", mapAtomicReference.get().size(), map.size());
		mapAtomicReference.compareAndSet(mapAtomicReference.get(), map);
		
	}
	
	public Map<String,Endpoint> getEndpointMap () {
		return mapAtomicReference.get();
	}
	
}
