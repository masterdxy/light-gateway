package com.github.masterdxy.gateway.config;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration public class HazelcastConfiguration {

    @Bean public Config hazelCastConfig() {
        return new Config().setInstanceName("gateway-hazelcast-instance");
    }

    @Bean public HazelcastInstance hazelcastInstance(Config config) {
        return Hazelcast.getOrCreateHazelcastInstance(config);
    }
}
