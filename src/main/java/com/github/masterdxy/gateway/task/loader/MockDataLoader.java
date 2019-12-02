package com.github.masterdxy.gateway.task.loader;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.common.MockData;
import com.github.masterdxy.gateway.common.dao.MockDataDao;
import com.github.masterdxy.gateway.plugin.impl.mock.MockDataManager;
import com.github.masterdxy.gateway.task.RunOnStartFixDelayScheduledService;
import com.github.masterdxy.gateway.task.TaskRegistry;
import com.google.common.collect.Maps;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.jfaster.mango.operator.Mango;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.masterdxy.gateway.common.Constant.HAZELCAST_MOCK_DATA_MAP_KEY;

public class MockDataLoader extends RunOnStartFixDelayScheduledService implements TaskRegistry.Task {

    private static final Logger logger = LoggerFactory.getLogger(MockDataLoader.class);

    @Autowired private HazelcastInstance hazelcastInstance;
    @Autowired private Mango mango;
    @Autowired private MasterLocker masterLocker;
    @Autowired private MockDataManager mockDataManager;

    private MockDataDao mockDataDao;

    @Override public String name() {
        return "mock-data";
    }

    @Override public void stop() {
        super.stopAsync();
    }

    @Override public int order() {
        return 4;
    }

    @Override protected void runOneIteration() throws Exception {
        fetchMockData();
    }

    //retrieve data from database and cache into cache.
    private void fetchMockData() {
        if (masterLocker.isHasMasterLock()) {
            logger.info("fetch mock data from MySQL store with lock ...");
            if (mockDataDao == null) {
                mockDataDao = mango.create(MockDataDao.class);
            }
            List<MockData> allMockData = mockDataDao.getAll();
            Map<Long, MockData> mockDataMap = Maps.newHashMap();
            if (allMockData != null) {
                allMockData.forEach(mockData -> {
                    mockDataMap.put(mockData.getEndpointId(), mockData);
                });
            }
            IMap<Long, MockData> hazelMap = hazelcastInstance.getMap(HAZELCAST_MOCK_DATA_MAP_KEY);
            hazelMap.clear();
            hazelMap.putAll(mockDataMap);
            logger.info("fetch mock data from MySQL store with lock ... finished");
        } else {
            logger.info("fetch mock data from hazelcast store without lock ...");
            IMap<Long, MockData> mockDataIMap = hazelcastInstance.getMap(HAZELCAST_MOCK_DATA_MAP_KEY);
            Map<Long, MockData> mockDataMap = Maps.newConcurrentMap();
            mockDataIMap.keySet().forEach(key -> mockDataMap.put(key, mockDataIMap.get(key)));
            mockDataManager.updateMockDataMap(mockDataMap);
        }
    }

    @Override protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(Constant.DELAY_LOAD_EPC, Constant.DELAY_LOAD_EPC, TimeUnit.MILLISECONDS);
    }
}
