package com.github.masterdxy.gateway;

import com.github.masterdxy.gateway.config.GatewaySpringConfiguration;
import com.github.masterdxy.gateway.config.SystemPropertiesConfig;
import com.github.masterdxy.gateway.config.VertxInitialization;
import com.github.masterdxy.gateway.spring.SpringContext;
import com.github.masterdxy.gateway.task.TaskRegistry;
import com.github.masterdxy.gateway.utils.GatewayShutdownHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Bootstrap {
	
	private static Logger log = LoggerFactory.getLogger(Bootstrap.class);
	
	/**
	 * For local test only.
	 */
	public static void main (String[] args) {
		
		SystemPropertiesConfig.prepareProperties();
		
		// Build ApplicationContext before vertx startup.
		SpringContext.initContext(GatewaySpringConfiguration.class);
		
		CountDownLatch countDownLatch = new CountDownLatch(2);// deploy 2 verticle
		// Init vertx.
		VertxInitialization vertxInitialization = SpringContext.instance(VertxInitialization.class);
		vertxInitialization.initialization(countDownLatch);
		
		// Register tasks
		List<TaskRegistry.Task> taskList = SpringContext.instances(TaskRegistry.Task.class);
		TaskRegistry.startAll(taskList);
		try {
			countDownLatch.await();
		}
		catch (InterruptedException e) {
			log.error("Start failed.", e);
		}
		Runtime.getRuntime().addShutdownHook(GatewayShutdownHook.getGatewayShutdownHook());
		VertxInitialization.setStarted(true);
		log.info("Gateway Started.");
		
	}
	
}
