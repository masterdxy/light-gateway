package com.github.masterdxy.gateway.task.loader;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.task.RunOnStartFixDelayScheduledService;
import com.github.masterdxy.gateway.task.TaskRegistry;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class MasterLocker extends RunOnStartFixDelayScheduledService implements TaskRegistry.Task {

    private static final Logger logger = LoggerFactory.getLogger(MasterLocker.class);

    @Autowired
    private HazelcastInstance hazelcastInstance;

    private boolean hasMasterLock = false;

    @Override
    protected void runOneIteration() throws Exception {
        ILock iLock = hazelcastInstance.getLock(Constant.HAZELCAST_LOCK_KEY);
        Objects.requireNonNull(iLock);
        lock(iLock);
    }


    private void lock(ILock iLock) {
        try {
            if (iLock.isLocked()) {
                if (iLock.isLockedByCurrentThread()) {
                    logger.info("lock is locked by current thread, remain lease time :{}", iLock.getRemainingLeaseTime());
                } else {
                    if (iLock.tryLock(Constant.LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS,
                            Constant.LOCK_LEASE_TIME_MS, TimeUnit.MILLISECONDS)) {
                        logger.info("current lock is locked, try lock success.");
                        hasMasterLock = true;
                    } else {
                        logger.info("current lock is locked, try lock timeout, remain lease time :{}, wait next " +
                                "period", iLock.getRemainingLeaseTime());
                        hasMasterLock = false;
                    }
                }
            } else {
                logger.info("current lock is NOT locked, try lock now.");
                if (iLock.tryLock(Constant.LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS,
                        Constant.LOCK_LEASE_TIME_MS, TimeUnit.MILLISECONDS)) {
                    logger.info("current lock is not locked, try lock success.");
                    hasMasterLock = true;
                } else {
                    logger.info("current lock is not locked, try lock timeout, remain lease time :{}, wait next " +
                            "period", iLock.getRemainingLeaseTime());
                    hasMasterLock = false;
                }
            }
        } catch (Exception e) {
            logger.warn("Periodic lock task error", e);
            hasMasterLock = false;
        }
    }


    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(Constant.DELAY_TYR_LOCK, Constant.DELAY_TYR_LOCK, TimeUnit.MILLISECONDS);
    }

    public boolean isHasMasterLock() {
        return hasMasterLock;
    }


    @Override
    public String name() {
        return "master-locker";
    }


    @Override
    public void stop() {
        super.stopAsync();
    }

    @Override
    public int order() {
        return 1;
    }
}
