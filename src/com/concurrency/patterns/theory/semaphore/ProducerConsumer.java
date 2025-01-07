package com.concurrency.patterns.theory.semaphore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ProducerConsumer {
    public static void main(String[] args) {
        //Infinite producer consumer with buffer size 1

        List<Integer> buffer = new ArrayList<>(1);
        Semaphore semaphoreProd = new Semaphore(1);
        Semaphore semaphoreCons = new Semaphore(0); //consumer woultn't start directly
        Semaphore semaphoreBuffer = new Semaphore(1);
        //producer
        Runnable producer = () -> {
            try {
                while (true) {
                    semaphoreProd.acquire();
                    semaphoreBuffer.acquire();

                    buffer.add(1);
                    System.out.println("Producing ");

                    semaphoreBuffer.release();
                    semaphoreCons.release();
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupted ");;
            }
        };

        Runnable consumer = () -> {
        try {
            while(true) {
                semaphoreCons.acquire();
                semaphoreBuffer.acquire();

                buffer.remove(buffer.size() - 1);
                System.out.println("Consuming ");

                semaphoreBuffer.release();
                semaphoreProd.release();
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }
        };

        Thread prodThread = new Thread(producer);
        Thread consThread  = new Thread(consumer);
        prodThread.start();
        consThread.start();
    }
}
