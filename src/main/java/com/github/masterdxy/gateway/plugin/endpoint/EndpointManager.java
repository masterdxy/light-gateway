package com.github.masterdxy.gateway.plugin.endpoint;

import com.github.masterdxy.gateway.common.EndpointConfig;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EndpointManager {

    //TODO resolve endpoint config from datasource.
    //TODO watch endpoint changes.

    public static List<EndpointConfig> loadEndpointConfigs(){
        //todo decide db
        EndpointConfig config = new EndpointConfig();
        config.setUri("/crud");
        config.setUpstreamType("dubbo");
        config.setUpstreamUrl("com.jiaoma.service.sample.api.SampleService");
        config.setNeedAuth(true);
        config.setNeedSign(true);
        EndpointConfig config_2 = new EndpointConfig();
        config_2.setUri("/echo");
        config_2.setUpstreamType("http");
        config_2.setUpstreamUrl("http://127.0.0.1:8080/echo");
        config_2.setNeedAuth(true);
        config_2.setNeedSign(true);
        return Lists.newArrayList(config,config_2);
    }
}
