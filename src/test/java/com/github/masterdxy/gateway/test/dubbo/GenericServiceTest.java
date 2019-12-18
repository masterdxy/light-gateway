package com.github.masterdxy.gateway.test.dubbo;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;

import com.alibaba.fastjson.JSON;
import com.github.masterdxy.gateway.common.Constant;
import org.junit.jupiter.api.Test;

public class GenericServiceTest {
	@Test
	void testGetGenericService () {
		ApplicationConfig ac = new ApplicationConfig();
		ac.setName(Constant.DUBBO_CONSUMER_APPLICATION_NAME);
		ac.setQosEnable(false);
		RegistryConfig registry = new RegistryConfig();
		registry.setAddress("nacos://127.0.0.1:8848");
		ac.setRegistry(registry);
		
		ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
		reference.setInterface("com.github.masterdxy.gateway.test.dubbo.provider.ISampleService");
		reference.setGeneric(true);
		reference.setApplication(ac);
		reference.setProtocol("dubbo");
		reference.setVersion("1.0.0");
		GenericService service = reference.get();
		//        Map<String, Object> params = new HashMap<String, Object>();
		//        params.put("id", "2");
		Object result = service.$invoke("echo", null, new Object[] {"Gateway"});
		System.out.println(JSON.toJSONString(result));
	}
	
}
