package com.lld.problems.concurrencyMock;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

//
interface ExecutorService {
    void submit(Runnable runnable);
    void shutdown();
}
// takes no. of thread as input.
//whenever we do a submit. Create a thread pool of fixed size and utilized . wait,notify, join
// shutdown must wait for all threads to complete and stop all of them

public class FixedExecutorServiceImpl implements ExecutorService{
    private final int noOfThreads;
    private BlockingQueue<Runnable> queue ;
    private final List<Thread> threads ;
    private AtomicBoolean isShutdown ; //.compareAndSwap by OS

    public FixedExecutorServiceImpl(int noOfThreads) {
        this.noOfThreads = noOfThreads;
        this.queue = new LinkedBlockingQueue<Runnable>();
        this.threads = new ArrayList<>();
        this.isShutdown = new AtomicBoolean(false);
        for(int i=0; i<noOfThreads; i++) {
            Thread thread = new Thread(this.queueChecker,"Thread "+i);
            thread.start();
            threads.add(thread);
        }
    }

    Runnable queueChecker =  () -> {
        while(!isShutdown.get()) {
            Runnable newTask = null;
            synchronized (queue) {
                try {
                    while (queue.isEmpty())
                        queue.wait();
                     newTask = queue.poll();
                } catch (InterruptedException e) {
                    System.out.println(e.toString());
                }
            }
            newTask.run(); //starts in the same thread
        }
    };

    @Override
    public void submit(Runnable runnable) {
        synchronized (queue) {
            queue.offer(runnable);
            queue.notifyAll();
        }
    }

    @Override
    public void shutdown() {
        while(queue.isEmpty()) {
            this.isShutdown.set(true);
        }
        System.out.println("Size of task queue: " + queue.size());
    //doing join would have been helpful if we needed shudown() to be a blocking operation
//        for(int i=0; i<noOfThreads; i++) {
//            Thread thread = threads.get(i);
//        }
    }

    public static void main(String[] args) {
        FixedExecutorServiceImpl fixedExecutorService = new FixedExecutorServiceImpl(10);
        Runnable runnable = () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName());
        };
        for(int i=0; i < 100; i++) {
            if(i == 50) {
                fixedExecutorService.shutdown();
                break;
            }
            fixedExecutorService.submit(runnable);
        }
    }
}
