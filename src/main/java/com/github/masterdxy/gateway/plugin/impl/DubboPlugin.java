package com.github.masterdxy.gateway.plugin.impl;

import org.apache.dubbo.rpc.service.GenericService;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.config.DubboConfiguration;
import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import com.github.masterdxy.gateway.protocol.v1.GatewayRequest;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(value = false)
public class DubboPlugin implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(DubboPlugin.class);
    @Autowired
    private DubboConfiguration dubboConfiguration;


    @Override
    public int order() {
        return -60;
    }

    @Override
    public boolean match(RoutingContext context) {
        //only match dubbo upstream
        return true;
    }

    @Override
    public boolean execute(RoutingContext context, PluginChain chain) {
        //invoke rpc in worker thread pool;
        //add result into context;
        //invoke chain next;
        GatewayRequest request = context.get(Constant.GATEWAY_REQUEST_KEY);
            GenericService service = dubboConfiguration.getDubboService(request.getNamespace(), request.getVersion());
            Object object = service.$invoke(request.getMethod(), null,
                    new Object[]{request.getData()});
            logger.info("DubboPlugin execute result : {}", object);
            context.put(Constant.PLUGIN_RESULT_KEY, object);
        return chain.execute();
    }
}
