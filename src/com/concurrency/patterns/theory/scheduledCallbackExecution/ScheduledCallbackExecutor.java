package com.concurrency.patterns.theory.scheduledCallbackExecution;

import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ScheduledCallbackExecutor {
    private final PriorityQueue<Callback> pq = new PriorityQueue<>((c1,c2) ->
            Long.compare(c1.getExecuteAt(),c2.getExecuteAt()));
    private final Lock lock = new ReentrantLock();
    private final Condition newEntry = lock.newCondition();
    private volatile boolean isShutdown = false;

    void registerCallback(Callback callback) {
        lock.lock();
        try {
            pq.offer(callback);
            System.out.println("Callback registered");
            newEntry.signalAll();
        } finally {
            lock.unlock();
        }
    }
    //to be started by a factory or main thread or an executor
    void callableExecutor() throws InterruptedException {
        while (!isShutdown) {
            lock.lock();
            try {
                while (pq.isEmpty()) {
                    newEntry.await(); // Wait indefinitely if queue is empty
                }
                // Wait until the next callback's execution time
                long waitTime = pq.peek().getExecuteAt() - System.currentTimeMillis();
                if (waitTime > 0) {
                    newEntry.await(waitTime, TimeUnit.MILLISECONDS); //  Timed wait
                } else {
                    Callback topCallback = pq.poll();
                    new Thread(() ->
                            System.out.println("Callback executed" + topCallback.getCallbackStr())
                    ).start();
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
