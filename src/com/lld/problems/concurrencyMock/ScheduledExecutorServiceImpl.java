package com.lld.problems.concurrencyDiscord;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

//
interface ScheduledExecutorService {
    void schedule(Runnable command, long delay, TimeUnit unit);
    void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);
    void scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit);
}

/***
 * Implement following method of ScheduledExecutorService interface in Java
 *
 * schedule(Runnable command, long delay, TimeUnit unit)
 * Creates and executes a one-shot action that becomes enabled after the given delay.
 *
 * scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
 * Creates and executes a periodic action that becomes enabled first after the given initial delay, and subsequently with the given period; that is executions will commence after initialDelay then initialDelay+period, then initialDelay + 2 * period, and so on.
 *
 * scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
 * Creates and executes a periodic action that becomes enabled first after the given initial delay, and subsequently with the given delay between the termination of one execution and the commencement of the next.
 */
class Task implements Comparator {
    Runnable runnable;
    long scheduleAtNS;

    public Task(Runnable runnable, long delay, TimeUnit unit) {
        this.runnable = runnable;
        this.scheduleAtNS = unit.toNanos(delay) + System.nanoTime() ;
    }



    @Override
    public int compare(Object o, Object t1) {
        return Long.compare((Task)t1.scheduleAtNS,t1.scheduleAtNS);
    }

    @Override
    public int compareTo(Task o) {
        return Long.compare((Task)o.scheduleAtNS,this.scheduleAtNS);
    }

//Bac
}

public class ScheduledExecutorServiceImpl implements ScheduledExecutorService{
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
        //
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
