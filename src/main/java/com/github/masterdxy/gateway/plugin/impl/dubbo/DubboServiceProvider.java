package com.github.masterdxy.gateway.plugin.impl.dubbo;

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
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Component
public class DubboServiceProvider {

    @NacosValue("${gateway.dubbo.registry}")
    private String registryAddress;

    @Autowired
    private RedisClient redisClient;

    private static Logger logger = LoggerFactory.getLogger(DubboServiceProvider.class);

    //TODO metadata clean up supported by service provider
    private ApplicationConfig ac;

    private RegistryConfig rc;

    private ConcurrentMap<DubboServiceIdentity, DubboProxyService> cache = Maps.newConcurrentMap();

    //to prevent reference-config destroy warn log.
    private ConcurrentMap<DubboServiceIdentity, ReferenceConfig<GenericService>> referenceConfigConcurrentMap =
            Maps.newConcurrentMap();

    private Function<DubboServiceIdentity, GenericService> serviceLoader = (serviceIdentity) -> {
        ReferenceConfig<GenericService> referenceConfig =
                referenceConfigConcurrentMap.get(serviceIdentity);
        if (referenceConfig != null) {
            return referenceConfig.get();
        }
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
        reference.setInterface(serviceIdentity.getInterfaceName());
        reference.setVersion(serviceIdentity.getVersion());
        Map<String, String> map = Maps.newHashMap();
        map.put(Constants.HEARTBEAT_KEY, String.valueOf(1000));     //every 1s try to reconnect target provider
        reference.setParameters(map);
        reference.setGeneric(true);
        reference.setRetries(0);
        reference.setApplication(ac);
        reference.setCheck(false);//prevent no provider found exception.
        reference.setProtocol(Constant.DUBBO_PROTOCOL_DUBBO);
        referenceConfigConcurrentMap.put(serviceIdentity, reference);
        //todo add consumer config improve performance e.g. share connections and thread pool size.
        return reference.get();
    };

    //only use cache, service init by locatingDubboService()
    DubboProxyService getDubboService(DubboServiceIdentity serviceIdentity) {
        return cache.get(serviceIdentity);
    }

    private DubboProxyService locatingDubboService(DubboServiceIdentity serviceIdentity) {
        DubboProxyService proxyService = cache.get(serviceIdentity);
        if (proxyService == null) {
            GenericService genericService = serviceLoader.apply(serviceIdentity);
            proxyService = DubboProxyService.wrap(serviceIdentity, genericService);
            cache.put(serviceIdentity, proxyService);
        }
        return proxyService;
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
                    String version = serviceDefinition.getParameters().getOrDefault(Constants.VERSION_KEY, "1.0.0");
                    DubboProxyService service =
                            locatingDubboService(DubboServiceIdentity.as(serviceDefinition.getCanonicalName(), version));
                    if (service != null) {
                        loaded.incrementAndGet();
                        logger.info("metadata load success, service :{}, version :{}",
                                serviceDefinition.getCanonicalName(), version);
                    }
                } catch (Exception e) {
                    logger.error("metadata load error", e);
                }
            });
        } finally {
            connection.close();
        }

        return loaded.get();
    }
}
