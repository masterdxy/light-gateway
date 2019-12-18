package com.github.masterdxy.gateway.task.loader;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.metadata.definition.model.FullServiceDefinition;
import org.apache.dubbo.metadata.identifier.MetadataIdentifier;
import org.apache.dubbo.rpc.service.GenericService;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.plugin.impl.dubbo.DubboProxyService;
import com.github.masterdxy.gateway.plugin.impl.dubbo.DubboServiceCache;
import com.github.masterdxy.gateway.plugin.impl.dubbo.DubboServiceIdentity;
import com.github.masterdxy.gateway.task.TaskRegistry;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.hazelcast.core.HazelcastInstance;
import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author tomoyo
 */
@Component
public class DubboServiceLoader extends AbstractScheduledService implements TaskRegistry.Task {
	
	private static final Logger logger = LoggerFactory.getLogger(DubboServiceLoader.class);
	
	@NacosValue("${gateway.dubbo.registry}")
	private String registryAddress;
	
	@Autowired
	private DubboServiceCache dubboServiceCache;
	@Autowired
	private HazelcastInstance hazelcastInstance;
	@Autowired
	private RedisClient       redisClient;
	
	/**
	 * TODO metadata clean up supported by service provider
	 */
	private ApplicationConfig applicationConfigCache;
	private RegistryConfig    registryConfigCache;
	
	private static final String SIDE_CONSUMER = "consumer";
	
	@Override
	protected void runOneIteration () {
		List<DubboServiceIdentity> serviceIdentityList = fetchDubboMetaData();
		loadDubboService(serviceIdentityList);
	}
	
	private List<DubboServiceIdentity> fetchDubboMetaData () {
		List<DubboServiceIdentity> serviceIdentityList = Lists.newArrayList();
		StatefulRedisConnection<String,String> connection = redisClient.connect();
		RedisCommands<String,String> syncCommand = connection.sync();
		try {
			List<String> metaDataKeys = syncCommand.keys("*.metaData");
			if (metaDataKeys == null || metaDataKeys.size() == 0) {
				return serviceIdentityList;
			}
			List<KeyValue<String,String>> metaData = syncCommand.mget(metaDataKeys.toArray(new String[] {}));
			if (metaData == null || metaData.size() == 0) {
				return serviceIdentityList;
			}
			metaData.forEach(stringStringKeyValue -> {
				String defKey = stringStringKeyValue.getKey();
				if (StringUtils.isNotBlank(defKey) && !StringUtils.equalsIgnoreCase(
					defKey.split(MetadataIdentifier.SEPARATOR)[2],
					SIDE_CONSUMER
				                                                                   )) {
					String defStr = stringStringKeyValue.getValue();
					try {
						FullServiceDefinition serviceDefinition = JSON.parseObject(defStr,
						                                                           FullServiceDefinition.class);
						String version = serviceDefinition.getParameters().getOrDefault(
							Constants.VERSION_KEY,
							Constant.DUBBO_DEFAULT_VERSION
						                                                               );
						serviceIdentityList.add(DubboServiceIdentity.as(serviceDefinition.getCanonicalName(),
						                                                version));
					}
					catch (Exception e) {
						logger.error(String.format("[DubboProvider] Parsing Metadata=[{%s}] Error", defKey), e);
					}
				}
			});
		}
		finally {
			connection.close();
		}
		return serviceIdentityList;
	}
	
	private void loadDubboService (List<DubboServiceIdentity> serviceIdentities) {
		Objects.requireNonNull(serviceIdentities).forEach(serviceIdentity -> {
			DubboProxyService service = locatingDubboService(serviceIdentity);
			if (service != null) {
				logger.info("[DubboProvider] Located [Service=[{}] Version=[{}]], Status=[SUCCESS]",
				            serviceIdentity.getInterfaceName(),
				            serviceIdentity.getVersion()
				           );
			}
		});
	}
	
	private DubboProxyService locatingDubboService (DubboServiceIdentity serviceIdentity) {
		DubboProxyService proxyService = dubboServiceCache.getCache().get(serviceIdentity);
		if (proxyService == null) {
			GenericService genericService = serviceLoader.apply(serviceIdentity);
			proxyService = DubboProxyService.wrap(serviceIdentity, genericService);
			dubboServiceCache.getCache().put(serviceIdentity, proxyService);
		}
		return proxyService;
	}
	
	/**
	 * to prevent reference-config destroy warn log.
	 */
	private ConcurrentMap<DubboServiceIdentity,ReferenceConfig<GenericService>> referenceConfigConcurrentMap =
		Maps.newConcurrentMap();
	
	private Function<DubboServiceIdentity,GenericService> serviceLoader = (serviceIdentity) -> {
		ReferenceConfig<GenericService> referenceConfig = referenceConfigConcurrentMap.get(serviceIdentity);
		if (referenceConfig != null) {
			return referenceConfig.get();
		}
		if (applicationConfigCache == null || registryConfigCache == null) {
			synchronized (this) {
				applicationConfigCache = new ApplicationConfig();
				applicationConfigCache.setName(Constant.DUBBO_CONSUMER_APPLICATION_NAME);
				registryConfigCache = new RegistryConfig();
				registryConfigCache.setAddress(registryAddress);
				applicationConfigCache.setRegistry(registryConfigCache);
			}
		}
		ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
		reference.setInterface(serviceIdentity.getInterfaceName());
		reference.setVersion(serviceIdentity.getVersion());
		Map<String,String> map = Maps.newHashMap();
		
		map.put(Constants.HEARTBEAT_KEY,
		        String.valueOf(Constant.DUBBO_HEART_BEAT_INTERVAL)
		       );     //every 1s try to reconnect target provider
		reference.setParameters(map);
		reference.setGeneric(true);
		reference.setRetries(Constant.DUBBO_RETRY);
		reference.setApplication(applicationConfigCache);
		reference.setCheck(false);//prevent no provider found exception.
		reference.setProtocol(Constant.PROTOCOL_DUBBO);
		referenceConfigConcurrentMap.put(serviceIdentity, reference);
		//todo add consumer config improve performance e.g. share connections and thread pool size.
		return reference.get();
	};
	
	@Override
	protected Scheduler scheduler () {
		return Scheduler.newFixedDelaySchedule(Constant.DELAY_LOAD_EPC, Constant.DELAY_LOAD_EPC,
		                                       TimeUnit.MILLISECONDS);
	}
	
	@Override
	public String name () {
		return "dubbo-service-loader";
	}
	
	@Override
	public void stop () {
		super.stopAsync();
	}
	
	@Override
	public int order () {
		return 3;
	}
	
	@Override
	public void startAfterRunOnce () throws Exception {
		startAsync().awaitRunning();
	}
	
	@Override
	protected void startUp () throws Exception {
		runOneIteration();
	}
}
