package com.concurrency.patterns.theory.threadsandrunnables;

public class BasicThread {
    public static void main(String[] args) {
        //don't use this
//        Thread thread = new Thread();
//        thread.start();

        //use this
        Thread thread1 = new Thread(new RunnableExample());
        thread1.start();

        //to simplify above using lambda, pass a runnable instance
        Thread thread2 = new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName()+ " goes to sleep");
                Thread.sleep(30000);
                System.out.println(Thread.currentThread().getName()+ " wakes up");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread2.setDaemon(true);
        //or
        Runnable printTask = () -> {
            System.out.println("Starting thread "+ Thread.currentThread().getName());
        };
        Thread thread3 = new Thread(printTask);

        thread2.start();
        thread3.start();
    }
}
