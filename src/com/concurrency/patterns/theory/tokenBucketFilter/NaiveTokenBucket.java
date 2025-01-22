package com.concurrency.patterns.theory.tokenBucketFilter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class NaiveTokenBucket {
    private final int MAX_TOKENS;
    private final int ONE_SEC = 1000;
    private long tokenCount;

    public NaiveTokenBucket(int bucketSize) {
        this.MAX_TOKENS = bucketSize;
        this.tokenCount = 0;
    }

    public synchronized void getToken() throws InterruptedException {
        //when will thread wait
        while(tokenCount == 0)
            this.wait();
        tokenCount--;
        System.out.println("Granted a token at "+ System.currentTimeMillis());
    }

    public  void fillerThread() throws InterruptedException {
        //when will thread wait. We can't use this logic, since thread is repeating, not waiting.
        while(true) {
            synchronized (this) {
                if (tokenCount < MAX_TOKENS) {
                    tokenCount++;
                    this.notifyAll();
                }
            }
            Thread.sleep(ONE_SEC);   //wait at the end, in case of periodic thread
        }
    }
}
