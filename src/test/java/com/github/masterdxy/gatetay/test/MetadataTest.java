package com.github.masterdxy.gatetay.test;

import org.apache.dubbo.metadata.definition.model.ServiceDefinition;

import com.google.gson.Gson;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void testServiceDefinitionParse() {
        //todo use set ?
        String key = "com.jiaoma.service.sample.api.SampleService:1.0.0:provider:sample-service.metaData";
        String value = sync.get(key);
        Assertions.assertNotNull(value);
        Gson gson = new Gson();
        ServiceDefinition serviceDefinition = gson.fromJson(value, ServiceDefinition.class);
        Assertions.assertNotNull(serviceDefinition);
        System.out.println(serviceDefinition.getCanonicalName());
        System.out.println(serviceDefinition.getCanonicalName());
        System.out.println(serviceDefinition.getMethods().size());
    }
}
