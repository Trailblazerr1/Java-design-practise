package com.concurrency.patterns.theory.scheduledCallbackExecution;

import java.util.concurrent.Callable;

public class Callback {
    private final String callbackStr;
    private final long executeAt;

    public Callback(String callbackStr, long executeAt) {
        this.callbackStr = callbackStr;
        this.executeAt = executeAt;
    }

    public String getCallbackStr() {
        return callbackStr;
    }

    public long getExecuteAt() {
        return executeAt;
    }
}
