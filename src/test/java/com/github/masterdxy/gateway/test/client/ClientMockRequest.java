package com.github.masterdxy.gateway.test.client;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

public class ClientMockRequest {

	static WebClient webClient;

	@BeforeAll
	static void setUp () {
		HttpClientOptions options = new HttpClientOptions();
		options.setMaxPoolSize(20);
		options.setKeepAlive(true);
		options.setMaxWaitQueueSize(1000);
		webClient = WebClient.create(Vertx.vertx(), new WebClientOptions(options));
	}

	@Test
	void mockRequest () throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(1);

		webClient.get(8848, "127.0.0.1", "/nacos").send(handler -> {
			if (handler.succeeded()) {
				System.out.println(handler.result().bodyAsString());
				countDownLatch.countDown();
			}
			else {
				handler.cause().printStackTrace();
				countDownLatch.countDown();
			}
		});
		countDownLatch.await();
	}
}
