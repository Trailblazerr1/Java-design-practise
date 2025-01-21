package com.concurrency.patterns.theory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountdownLatchImpl {
    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(10); //pass this latch through constructor for other classes to use it
        final ExecutorService service = Executors.newCachedThreadPool();

        Runnable doTask = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            latch.countDown();
            System.out.println("Thread name: "+ Thread.currentThread().getName());
        };

        for(int i=0; i< 20; i++) {
            service.submit(doTask);
        }
        latch.await(); //wait in the main thread until all the 10 threads are done executing
        //without await, main thread will complete execution without waiting for 10 threads, as in
        //it will print promptly,  before all the child threads.
        //With latch.await(), it prints after 10 threads are done. (Tries to print)
        System.out.println("10 Threads done "+Thread.currentThread().getName());
        service.shutdown();
    }
}
