package com.github.masterdxy.gateway.task.loader;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.common.Endpoint;
import com.github.masterdxy.gateway.common.dao.EndpointDao;
import com.github.masterdxy.gateway.plugin.endpoint.EndpointManager;
import com.github.masterdxy.gateway.task.RunOnStartFixDelayScheduledService;
import com.github.masterdxy.gateway.task.TaskRegistry;
import com.google.common.collect.Maps;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.jfaster.mango.operator.Mango;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component public class EndpointLoader extends RunOnStartFixDelayScheduledService implements TaskRegistry.Task {

    private static final Logger logger = LoggerFactory.getLogger(EndpointLoader.class);

    @Autowired private Mango mango;
    @Autowired private MasterLocker masterLocker;
    @Autowired private EndpointManager endpointManager;
    @Autowired private HazelcastInstance hazelcastInstance;

    private EndpointDao endpointDao;

    @Override protected void runOneIteration() {
        fetchEndpointData();
    }

    private void fetchEndpointData() {
        if (masterLocker.isHasMasterLock()) {
            logger.info("fetch endpoint data from MySQL store with lock ...");
            if (endpointDao == null) {
                endpointDao = mango.create(EndpointDao.class);
            }
            Map<String, Endpoint> endpointConfigMap = Maps.newConcurrentMap();
            List<Endpoint> allEpc = endpointDao.getAll();
            allEpc.forEach(epc -> endpointConfigMap.put(epc.getUri(), epc));
            hazelcastInstance.getMap(Constant.HAZELCAST_EPC_MAP_KEY).putAll(endpointConfigMap);
            endpointManager.updateEpcMap(endpointConfigMap);
        } else {
            logger.info("fetch endpoint data from hazelcast store without lock ...");
            IMap<String, Endpoint> iMap = hazelcastInstance.getMap(Constant.HAZELCAST_EPC_MAP_KEY);
            Set<String> keySet = iMap.keySet();
            Map<String, Endpoint> endpointConfigMap = Maps.newConcurrentMap();
            keySet.forEach(key -> endpointConfigMap.put(key, iMap.get(key)));
            endpointManager.updateEpcMap(endpointConfigMap);
        }

    }

    @Override protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(Constant.DELAY_LOAD_EPC, Constant.DELAY_LOAD_EPC, TimeUnit.MILLISECONDS);
    }

    @Override public String name() {
        return "endpoint-loader";
    }

    @Override public void stop() {
        super.stopAsync();
    }

    @Override public int order() {
        return 2;
    }
}
