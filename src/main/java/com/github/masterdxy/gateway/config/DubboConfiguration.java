package com.github.masterdxy.gateway.config;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.metadata.definition.model.FullServiceDefinition;
import org.apache.dubbo.rpc.service.GenericService;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.masterdxy.gateway.common.Constant;
import com.google.common.collect.Maps;
import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

@Configuration
public class DubboConfiguration {

    @NacosValue("${gateway.dubbo.registry}")
    private String registryAddress;

    @Autowired
    private RedisClient redisClient;

    private static Logger logger = LoggerFactory.getLogger(DubboConfiguration.class);

    //TODO move to other package.
    //TODO metadata clean up.
    private ApplicationConfig ac;
    private RegistryConfig rc;
    private ConcurrentMap<String, GenericService> cache = Maps.newConcurrentMap();
    private BiFunction<String, String, GenericService> serviceLoader = (iFaceName, version) -> {
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
        reference.setInterface(iFaceName);
        reference.setVersion(version);
        Map<String, String> map = Maps.newHashMap();
        //every 1s try to reconnect target provider
        map.put(Constants.HEARTBEAT_KEY, String.valueOf(1000));
        reference.setParameters(map);
        reference.setGeneric(true);
        reference.setRetries(0);
        reference.setApplication(ac);
        reference.setProtocol(Constant.DUBBO_PROTOCOL_DUBBO);
        //todo add consumer config improve performance e.g. share connections and thread pool size.

        return reference.get();
    };

    public GenericService getDubboService(String interfaceName, String version) {
        GenericService service = cache.get(interfaceName + ":" + version);
        if (service == null) {
            service = serviceLoader.apply(interfaceName, version);
            cache.put(interfaceName + ":" + version, service);
        }
        return service;
    }

    public int initDubboGenericService() {
        AtomicInteger loaded = new AtomicInteger(0);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommand = connection.sync();
        try {
            List<String> metaDataKeys = syncCommand.keys("*.metaData");
            logger.info("MetaData : {}", JSON.toJSONString(metaDataKeys));
            if (metaDataKeys == null || metaDataKeys.size() == 0) {
                return loaded.get();
            }
            List<KeyValue<String, String>> metaData = syncCommand.mget(metaDataKeys.toArray(new String[]{}));
            if (metaData == null || metaData.size() == 0) {
                return loaded.get();
            }
            metaData.forEach(stringStringKeyValue -> {
                String defStr = stringStringKeyValue.getValue();
                try {
                    FullServiceDefinition serviceDefinition = JSON.parseObject(defStr, FullServiceDefinition.class);
                    GenericService service = getDubboService(serviceDefinition.getCanonicalName(),
                            serviceDefinition.getParameters().getOrDefault(Constants.VERSION_KEY, "1.0.0"));
                    if (service != null) {
                        loaded.incrementAndGet();
                        logger.info("metadata load success, service :{}, version :{}",
                                serviceDefinition.getCanonicalName(),
                                serviceDefinition.getParameters().getOrDefault(Constants.VERSION_KEY, "1.0.0"));
                    }
                } catch (Exception e) {
                    logger.warn("metadata load error", e.getMessage());
                }
            });
        } finally {
            connection.close();
        }

        return loaded.get();
    }
}
