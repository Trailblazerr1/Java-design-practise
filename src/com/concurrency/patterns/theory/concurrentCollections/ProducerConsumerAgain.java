package com.concurrency.patterns.theory.concurrentCollections;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ProducerConsumerAgain {
    public static void main(String[] args) {
        BlockingQueue<Integer> buffer = new ArrayBlockingQueue(1);
        Runnable prod = () -> {
            try {
                while(true) {
                    buffer.offer(1, 1,TimeUnit.SECONDS);
                    System.out.println("Producing");
                }
            } catch (Exception e) {
                System.out.println("Producer Exception");
            }
        };
        Runnable cons = () -> {
            try {
                while(true) {
                    buffer.poll(1, TimeUnit.SECONDS);
                    System.out.println("Consuming");
                }
            } catch (Exception e) {
                System.out.println(" Consumer exception ");
            }
        };
        Thread p = new Thread(prod);
        Thread c = new Thread(cons);
        p.start();
        c.start();
    }
}
