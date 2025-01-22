package com.concurrency.patterns.theory.scheduledCallbackExecution;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        ScheduledCallbackExecutor scheduledCallbackExecutor = new ScheduledCallbackExecutor();
        ExecutorService service = Executors.newCachedThreadPool();
        service.submit(() -> {
            try {
                scheduledCallbackExecutor.callableExecutor();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        for(int i=0; i< 10; i++) {
            Callback callback = new Callback("Hey again "+i, System.currentTimeMillis() + 1000);
            service.submit(() -> scheduledCallbackExecutor.registerCallback(callback));
        }
        service.shutdown();
    }
}
