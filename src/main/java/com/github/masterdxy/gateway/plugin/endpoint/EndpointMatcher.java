package com.github.masterdxy.gateway.plugin.endpoint;

import com.github.masterdxy.gateway.common.Endpoint;
import com.github.masterdxy.gateway.protocol.v1.GatewayRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class EndpointMatcher {
	
	private static final Logger logger = LoggerFactory.getLogger(EndpointManager.class);
	
	@Autowired
	private EndpointManager endpointManager;
	
	private AntPathMatcher antPathMatcher = new AntPathMatcher();
	
	//return matched endpoint
	
	//    Endpoint endpoint = new Endpoint();
	//        endpoint.setNeedSign(true);
	//        endpoint.setNeedAuth(true);
	//        endpoint.setUriPattern("/crud");
	//        endpoint.setUpstreamUrl("com.jiaoma.service.sample.api.CRUDService");
	//        endpoint.setUpstreamType("dubbo");
	//        endpoint.setId(1L);
	//        endpoint.setMock(false);
	//        return endpoint;
	public Optional<Endpoint> match (GatewayRequest request) {
		Map<String,Endpoint> endpointMap = endpointManager.getEndpointMap();
		Set<String> uriSet = endpointMap.keySet();
		Optional<String> firstMatchUri = uriSet.stream().filter(uri -> {
			boolean match = antPathMatcher.match(uri, request.getExtraUrl());
			if (match) {
				Endpoint endpoint = endpointMap.get(uri);
				if (endpoint.getStatus() != 0) {
					logger.info("matching endpoint matched but epc is stopped.");
					return false;
				}
				if (!StringUtils.equals(endpoint.getNamespace(), request.getNamespace())) {
					logger.info("matching endpoint matched but namespace is not equals.");
					return false;
				}
				if (!StringUtils.equals(endpoint.getVersion(), request.getVersion())) {
					logger.info("matching endpoint matched but version is not equals.");
					return false;
				}
				return true;
			}
			return false;
		}).findFirst();
		return firstMatchUri.map(endpointMap::get);
		//TODO should detail match e.g. isMock ,namespace, version...
	}
	
}
