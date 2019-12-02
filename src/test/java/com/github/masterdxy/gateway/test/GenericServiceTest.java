package com.github.masterdxy.gateway.test;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;

import com.alibaba.fastjson.JSON;
import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.protocol.v1.GatewayRequest;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class GenericServiceTest {
    @Test void testGetGenericService() {
        ApplicationConfig ac = new ApplicationConfig();
        ac.setName(Constant.DUBBO_CONSUMER_APPLICATION_NAME);
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("nacos://127.0.0.1:8848");
        ac.setRegistry(registry);

        ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
        reference.setInterface("com.jiaoma.service.sample.api.CRUDService");
        reference.setGeneric(true);
        reference.setApplication(ac);
        reference.setProtocol("dubbo");
        reference.setVersion("1.0.0");
        GenericService service = reference.get();
        //        Map<String, Object> params = new HashMap<String, Object>();
        //        params.put("id", "2");
        Object result = service.$invoke("retrieve", null, new Object[] {"2"});
        System.out.println(JSON.toJSONString(result));
    }

    @Test void testMapToJson() {
        GatewayRequest req = new GatewayRequest();
        Map<String, String> data = Maps.newHashMap();
        data.put("id", "2");
        data.put("method", "retrieve");
        data.put("reqClass", "com.jiaoma.service.sample.request.RetrieveRequest");
        req.setData(JSON.toJSONString(data));
        req.setNamespace("com.jiaoma.service.sample.api.CRUDService");
        req.setVersion("1.0.0");
        System.out.println(JSON.toJSONString(req));
    }
}
