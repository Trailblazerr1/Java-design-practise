package com.concurrency.patterns.theory;

import java.util.concurrent.*;

public class CyclicBarrierImpl {
    public static void main(String[] args) {
        final ExecutorService service = Executors.newCachedThreadPool();
        Runnable printBreach = () -> {
            System.out.println("Barrier breached");
        };
        final CyclicBarrier barrier = new CyclicBarrier(10, printBreach);
        Runnable doTask = () -> {
            try {
                Thread.sleep(100);
                System.out.println("Waiting for 10 threads");
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Thread name: "+ Thread.currentThread().getName());
        };

        for(int i = 0; i< 20 ; i++)
            service.submit(doTask);
        service.shutdown();
    }
}
