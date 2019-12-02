package com.github.masterdxy.gateway.task.loader;

import com.github.masterdxy.gateway.common.Constant;
import com.github.masterdxy.gateway.task.TaskRegistry;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author tomoyo
 */
@Component public class MasterWriteLocker extends AbstractScheduledService implements TaskRegistry.Task {

    private static final Logger logger = LoggerFactory.getLogger(MasterWriteLocker.class);

    @Autowired private HazelcastInstance hazelcastInstance;

    private boolean hasMasterLock = false;

    @Override protected void runOneIteration() {
        ILock iLock = hazelcastInstance.getLock(Constant.HAZELCAST_LOCK_KEY);
        Objects.requireNonNull(iLock);
        lock(iLock);
    }

    /**
     * Try lock when starting use scheduled thread.
     */
    @Override protected void startUp() throws Exception {
        super.startUp();
        ILock iLock = hazelcastInstance.getLock(Constant.HAZELCAST_LOCK_KEY);
        Objects.requireNonNull(iLock);
        lock(iLock);
    }

    private static String lockStatusInfo = "[MasterWriteLock] Locked=[{}], CurrentNode=[{}], RemainingLeaseTime=[{}]";
    private static String lockTryTimeoutInfo =
        "[MasterWriteLock] Locked=[{}], CurrentNode=[{}], " + "RemainingLeaseTime=[{}], TryLockSuccess=[TIMEOUT]";
    private static String lockTrySuccessInfo =
        "[MasterWriteLock] Locked=[{}], CurrentNode=[{}], " + "RemainingLeaseTime=[{}], TryLockSuccess=[SUCCESS]";

    private void lock(ILock iLock) {
        try {
            if (iLock.isLocked()) {
                logger.info(lockStatusInfo, iLock.isLocked(), iLock.isLockedByCurrentThread(),
                    iLock.getRemainingLeaseTime());
            } else {
                logger.info(lockStatusInfo, iLock.isLocked(), iLock.isLockedByCurrentThread(),
                    iLock.getRemainingLeaseTime());
                if (iLock.tryLock(Constant.LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS, Constant.LOCK_LEASE_TIME_MS,
                    TimeUnit.MILLISECONDS)) {
                    logger.info(lockTrySuccessInfo, iLock.isLocked(), iLock.isLockedByCurrentThread(),
                        iLock.getRemainingLeaseTime());
                    hasMasterLock = true;
                } else {
                    logger.info(lockTryTimeoutInfo, iLock.isLocked(), iLock.isLockedByCurrentThread(),
                        iLock.getRemainingLeaseTime());
                    hasMasterLock = false;
                }
            }
        } catch (Exception e) {
            logger.warn("[MasterWriteLock] LockTask has error", e);
            hasMasterLock = false;
        }
    }

    @Override protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(Constant.DELAY_TYR_LOCK, Constant.DELAY_TYR_LOCK, TimeUnit.MILLISECONDS);
    }

    public boolean isHasMasterLock() {
        return hasMasterLock;
    }

    @Override public void startAfterRunOnce() throws Exception {
        this.startAsync().awaitRunning();
    }

    @Override public String name() {
        return "master-write-locker";
    }

    @Override public void stop() {

        super.stopAsync();
    }

    @Override public int order() {
        return 1;
    }
}
