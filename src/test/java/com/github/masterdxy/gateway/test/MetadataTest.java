package com.github.masterdxy.gateway.test;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.metadata.definition.model.FullServiceDefinition;
import org.apache.dubbo.rpc.service.GenericService;

import com.alibaba.fastjson.JSON;
import com.github.masterdxy.gateway.common.Constant;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MetadataTest {

    RedisClient redisClient;
    StatefulRedisConnection<String, String> connection;
    RedisStringCommands<String, String> sync;

    @BeforeEach
    void setUp() {
        redisClient = RedisClient.create("redis://192.168.3.67:6379");
        connection = redisClient.connect();
        sync = connection.sync();
    }

    @AfterEach
    void tearDown() {
        connection.close();
        redisClient.shutdown();
    }

    @Test
    void testServiceDefinitionParse() throws InterruptedException {
        //todo use set ?
        String key = "com.jiaoma.service.sample.api.CRUDService:1.0.0:provider:sample-service.metaData";
        String value = sync.get(key);
        Assertions.assertNotNull(value);
        Gson gson = new Gson();
        FullServiceDefinition serviceDefinition = gson.fromJson(value, FullServiceDefinition.class);
        Assertions.assertNotNull(serviceDefinition);

        ApplicationConfig ac = new ApplicationConfig();
        ac.setName(Constant.DUBBO_CONSUMER_APPLICATION_NAME);
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("nacos://127.0.0.1:8848");
        ac.setRegistry(registry);

        ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
        reference.setInterface(serviceDefinition.getCanonicalName());
        reference.setGeneric(true);
        Map<String, String> map = Maps.newHashMap();
        map.put(Constants.HEARTBEAT_KEY, String.valueOf(1000));
        reference.setParameters(map);
        reference.setApplication(ac);
        reference.setProtocol("dubbo");
        reference.setRetries(NumberUtils.toInt(serviceDefinition.getParameters().get("retries"), 0));
        reference.setVersion(serviceDefinition.getParameters().get("version"));
        GenericService service = reference.get();

        for (; ; ) {
            try {
                Object result = service.$invoke("retrieve", null,
                        new Object[]{"2"});
                System.out.println(JSON.toJSONString(result));

            } catch (Exception e) {
                System.out.println("Error" + e.getMessage());
            }
            Thread.sleep(1000);
        }

    }
}
