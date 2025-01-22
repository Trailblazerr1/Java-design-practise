package com.concurrency.patterns.theory.BlockingQueue;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        BlockingQueueWaitImpl<Integer> bq = new BlockingQueueWaitImpl<>(10);
        ExecutorService service = Executors.newCachedThreadPool();

        Runnable producer = () -> {
            for (int i = 0; i < 100; i++) {
                bq.offer(i);
            }
        };

        Runnable consumer = () -> {
            try {
                while (true) {
                    int val = bq.poll();
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        service.submit(producer);
        service.submit(consumer);
        service.shutdown();
    }
}
