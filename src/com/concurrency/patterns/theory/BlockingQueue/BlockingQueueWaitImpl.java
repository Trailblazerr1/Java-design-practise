package com.concurrency.patterns.theory.BlockingQueue;

import java.util.LinkedList;
import java.util.Queue;

//implement a blockingqueue, with poll,offer method using wait notify
interface MyBlockingQueue<T> {
    void offer(T t);
    T poll();
}

public class BlockingQueueWaitImpl<T> implements MyBlockingQueue<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int cap ;
    public BlockingQueueWaitImpl(int size) {
        this.cap = size;
    }

    @Override
    public synchronized void offer(T t) {
        try {
            while(queue.size() == cap)
                this.wait();
            System.out.println("Inserted "+t.toString());
            queue.offer(t);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(e.toString());
        } finally {
            this.notifyAll();
        }
    }

    @Override
    public synchronized T poll() {
        T t = null;
        try {
            while(queue.isEmpty())
                this.wait();
            t = queue.poll();
            System.out.println("Removed "+t.toString());
            return t;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(e.toString());
        } finally {
            this.notifyAll();
            return t;
        }
    }
}
