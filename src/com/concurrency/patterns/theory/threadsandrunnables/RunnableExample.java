package com.concurrency.patterns.theory.threadsandrunnables;

public class RunnableExample implements Runnable {

    @Override
    public void run() {
        System.out.println("Starting thread "+ Thread.currentThread().getName());
    }
}
