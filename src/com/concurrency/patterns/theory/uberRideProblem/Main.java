package com.concurrency.patterns.theory.uberRideProblem;

import java.util.concurrent.BrokenBarrierException;

public class Main {
    public static void main(String[] args) {
        SeatArrangement seatArrangement = new SeatArrangement();
        Runnable demo = () -> {
            try {
                seatArrangement.seated();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        };
        Runnable rep = () -> {
            try {
                seatArrangement.seated();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        };
        new Thread(demo,"Democrat").start();
        new Thread(demo,"Democrat").start();
        new Thread(demo,"Democrat").start();
        new Thread(demo,"Democrat").start();
        new Thread(demo,"Republican").start();
        new Thread(demo,"Democrat").start();
        new Thread(demo,"Republican").start();
        new Thread(demo,"Democrat").start();
    }
}
