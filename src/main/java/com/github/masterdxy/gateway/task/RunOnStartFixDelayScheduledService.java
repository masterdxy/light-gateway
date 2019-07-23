package com.github.masterdxy.gateway.task;

import com.google.common.util.concurrent.AbstractScheduledService;

//Note:  may be set sub-class's scheduler's initialDelay = delay to void twice execute of runOneIteration().
public abstract class RunOnStartFixDelayScheduledService extends AbstractScheduledService implements TaskRegistry.Task {

    public void startAfterRunOnce() throws Exception {
        this.runOneIteration();
        super.startAsync();
    }

}
