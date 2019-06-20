package com.github.masterdxy.gateway.plugin.endpoint;

import com.github.masterdxy.gateway.common.EndpointConfig;
import com.github.masterdxy.gateway.protocol.v1.GatewayRequest;
import org.springframework.stereotype.Component;

@Component
public class EndpointMatcher {

    //return matched endpoint
    public EndpointConfig match(GatewayRequest request){
        return null;
    }

}
