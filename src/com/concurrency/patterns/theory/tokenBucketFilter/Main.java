package com.concurrency.patterns.theory.tokenBucketFilter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        NaiveTokenBucket naiveTokenBucket = new NaiveTokenBucket(5);
        ExecutorService service = Executors.newCachedThreadPool();

        Runnable backgroundThread = () -> {
            try { //This should be done by NaiveTokenBucket class
                //So, make a factory, which give its object, and also creates
                // and starts the fillerThread()
                naiveTokenBucket.fillerThread();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Runnable getTokenThread = () -> {
            try {
                naiveTokenBucket.getToken();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        service.submit(backgroundThread);
        Thread.sleep(5000);
        for(int i=0; i<10; i++)
            service.submit(getTokenThread);
        service.shutdown();
    }
}
