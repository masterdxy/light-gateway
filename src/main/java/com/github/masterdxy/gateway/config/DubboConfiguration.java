package com.github.masterdxy.gateway.config;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.masterdxy.gateway.common.Constant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubboConfiguration {

    @NacosValue("${gateway.dubbo.registry}")
    private String registryAddress;

    //todo need use meta-data reporter to load iface method and param.

    private ApplicationConfig ac;
    private RegistryConfig rc;

    public GenericService getDubboService(String interfaceName, String version) {
        if (StringUtils.isNoneEmpty(interfaceName, version)) {
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
            reference.setInterface(interfaceName);
            reference.setVersion(version);
            //reference.setGroup("");
            reference.setGeneric(true);
            reference.setApplication(ac);
            return ReferenceConfigCache.getCache().get(reference);
        }
        throw new RuntimeException("interfaceName is null");
    }

}
