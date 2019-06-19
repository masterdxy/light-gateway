package com.github.masterdxy.gateway.config;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.masterdxy.gateway.common.Constant;
import com.google.common.collect.Maps;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

@Configuration
public class DubboConfiguration {

    @NacosValue("${gateway.dubbo.registry}")
    private String registryAddress;

    //todo need use meta-data reporter to load iface method and param.
    //todo if use meta-data , call $echo method after reference service init to check connection.

    private ApplicationConfig ac;
    private RegistryConfig rc;
    private ConcurrentMap<String, GenericService> cache = Maps.newConcurrentMap();
    private BiFunction<String, String, GenericService> serviceLoader = (iFaceName, version) -> {
        if (ac == null || rc == null) {
            synchronized (this) {
                ac = new ApplicationConfig();
                ac.setName(Constant.DUBBO_CONSUMER_APPLICATION_NAME);
                RegistryConfig registry = new RegistryConfig();
                registry.setAddress(registryAddress);
                ac.setRegistry(registry);
            }
        }
        ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
        reference.setInterface(iFaceName);
        reference.setVersion(version);
        //reference.setGroup("");
        reference.setGeneric(true);
        reference.setApplication(ac);
        //todo add consumer config improve performance e.g. share connections and thread pool size.
        return reference.get();
    };

    public GenericService getDubboService(String interfaceName, String version) {
        GenericService service = cache.get(interfaceName + version);
        if (service == null) {
            service = serviceLoader.apply(interfaceName, version);
            cache.put(interfaceName + version, service);
        }
        return service;
    }
}
