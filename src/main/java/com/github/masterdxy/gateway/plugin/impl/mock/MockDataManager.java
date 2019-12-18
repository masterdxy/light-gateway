package com.github.masterdxy.gateway.plugin.impl.mock;

import com.github.masterdxy.gateway.common.MockData;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class MockDataManager {
	
	private static final Logger                              logger             =
		LoggerFactory.getLogger(MockDataManager.class);
	private              AtomicReference<Map<Long,MockData>> mapAtomicReference =
		new AtomicReference<>(Maps.newConcurrentMap());
	
	public MockDataManager () {
	}
	
	public void updateMockDataMap (Map<Long,MockData> dataMap) {
		Objects.requireNonNull(dataMap);
		logger.info("update mock data map size :{}", dataMap.size());
		mapAtomicReference.compareAndSet(mapAtomicReference.get(), dataMap);
	}
	
	MockData getMockDataByEpcId (Long epcId) {
		Objects.requireNonNull(epcId);
		return mapAtomicReference.get().get(epcId);
	}
	
}
