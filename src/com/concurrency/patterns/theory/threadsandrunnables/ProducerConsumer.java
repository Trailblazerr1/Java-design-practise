package com.concurrency.patterns.theory.threadsandrunnables;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProducerConsumer {
    //a common buffer
    //producer starts , consumer starts from wait
    // producer produces, calls wait on itself and notifies consumer
    //consumer starts, consumes, calls wait on itself and notifies consumer.
    public static void main(String[] args) {
        final List<Integer> buffer = new ArrayList<>();
        ExecutorService executor = Executors.newCachedThreadPool();//over-engineer

        Runnable producer = () -> {
            while(!Thread.currentThread().isInterrupted()) {
                synchronized (buffer) {
                    try {
                        while (!buffer.isEmpty()) {
                            buffer.wait();
                        }
                        buffer.add(1);
                        System.out.println("Produced");
                        buffer.notify();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        Runnable consumer = () -> {
            while(!Thread.currentThread().isInterrupted()) {
                synchronized (buffer) {
                    try {
                        while (buffer.isEmpty()) { // deal with spurious wakeups
                            buffer.wait();
                        }
                        buffer.remove(0);
                        System.out.println("Consumed");
                        buffer.notify();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        executor.submit(producer);
        executor.submit(consumer);
        executor.shutdown();
    }
}
