package com.concurrency.patterns.theory.uberRideProblem;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class SeatArrangement {
    Runnable barrierAction = () -> {
        synchronized(this) {
            System.out.println("Off we go!");
            demo = 0;
            rep = 0;
            this.notifyAll();
        }
    };
    private final CyclicBarrier barrier = new CyclicBarrier(4, barrierAction);//cyclic barrier automatically
                                 //resets after broken. Don't use Reset
    private int demo = 0;
    private int rep = 0;

    public void seated() throws InterruptedException, BrokenBarrierException {
        synchronized (this) {
            String name = Thread.currentThread().getName();
            boolean isRepublican = name.contains("Republican");
            while (true) {  //since we aren't maintaining a queue this thread needs to keep checking.
                int currentDemo = demo;
                int currentRep = rep;
                if (isRepublican) {
                    int newRep = currentRep + 1;
                    int total = currentDemo + newRep;
                    boolean valid = (currentDemo == 0) || (currentDemo <= 2 && newRep <= 2);
                    if (total > 4 || !valid) {
                        wait();
                        continue;
                    }
                    rep = newRep;
                    break;
                } else {
                    int newDemo = currentDemo + 1;
                    int total = newDemo + currentRep;
                    boolean valid = (currentRep == 0) || (newDemo <= 2 && currentRep <= 2);
                    if (total > 4 || ! valid) {
                        wait();
                        continue;
                    }
                    demo = newDemo;
                    break;
                }
            }
        }

        try {
            System.out.println("Waiting people " + barrier.getNumberWaiting() + " - " + demo + " " + rep);
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }
}
