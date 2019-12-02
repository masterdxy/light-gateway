package com.github.masterdxy.gateway.test;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;

import com.jiaoma.service.sample.api.CRUDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DubboConsumerTest {
    private static Logger logger = LoggerFactory.getLogger(DubboConsumerTest.class);

    public static void main(String[] args) {
        // Application Info
        ApplicationConfig application = new ApplicationConfig();
        application.setName("gateway-test");

        // Registry Info
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("127.0.0.1:8848");
        registry.setProtocol("nacos");

        // NOTES: ReferenceConfig holds the connections to registry and providers, please cache it for performance.

        // Refer remote service
        ReferenceConfig<CRUDService> reference =
            new ReferenceConfig<CRUDService>(); // In case of memory leak, please cache.
        reference.setApplication(application);
        reference.setRegistry(registry);
        reference.setProtocol("dubbo");
        reference.setInterface(CRUDService.class);
        //version为必填否则no provider
        reference.setVersion("1.0.0");

        // Use xxxService just like a local bean
        CRUDService crudService = reference.get(); // NOTES: Please cache this proxy instance.
        logger.info("Dubbo client : {}", crudService.toString());
    }
}
