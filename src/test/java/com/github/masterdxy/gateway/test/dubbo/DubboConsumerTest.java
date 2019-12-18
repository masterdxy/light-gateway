package com.github.masterdxy.gateway.test.dubbo;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;

import com.github.masterdxy.gateway.test.dubbo.provider.EchoDTO;
import com.github.masterdxy.gateway.test.dubbo.provider.ISampleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DubboConsumerTest {
	private static Logger logger = LoggerFactory.getLogger(DubboConsumerTest.class);

	public static void main (String[] args) {
		// Application Info
		ApplicationConfig application = new ApplicationConfig();
		application.setName("gateway-test");
		application.setQosEnable(false);

		// Registry Info
		RegistryConfig registry = new RegistryConfig();
		registry.setAddress("127.0.0.1:8848");
		registry.setProtocol("nacos");

		// NOTES: ReferenceConfig holds the connections to registry and providers, please cache it for performance.

		// Refer remote service
		ReferenceConfig<ISampleService> reference = new ReferenceConfig<ISampleService>(); // In case of memory leak,
		// please cache.
		reference.setApplication(application);
		reference.setRegistry(registry);
		reference.setProtocol("dubbo");
		reference.setInterface(ISampleService.class);
		// version为必填否则no provider
		reference.setVersion("1.0.0");

		// Use xxxService just like a local bean
		ISampleService sampleService = reference.get(); // NOTES: Please cache this proxy instance.
		logger.info("Dubbo client : {}", sampleService.toString());
		logger.info("Echo : {}", sampleService.echo("Gateway"));
		EchoDTO dto = new EchoDTO();
		dto.setName("Gateway");
		dto.setAge(1);
		dto.setSpeakEnglish(Boolean.TRUE);
		logger.info("EchoDTO : {}", sampleService.echo(dto));
	}
}
