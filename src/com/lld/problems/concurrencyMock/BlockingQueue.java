//package com.lld.problems.concurrencyDiscord;
//
//import java.util.LinkedList;
//import java.util.Queue;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.locks.ReentrantLock;
//
//public interface BlockingQueue<T> {
//    public T consume();
//    public void produce(T t);
//}
////fixed size queue
////Only consume if there queue is populate, else wait
////Only produce if queue size is less than max, else if queue is full then wait
//
//public class BlockingQueueImpl<T> implements  BlockingQueue<T> {
//    private final Queue<T> queue ;
//    private final int capacity;
//    Object lock = new Object();
//    Lock lock2 = new ReentrantLock();
//
//
//    public BlockingQueueImpl(int size) {
//        this.queue = new LinkedList<T>();
//        this.capacity = size;
//    }
//    //size 1
//    //consumer.wait() -> notify()......
//    //producer should get the lock, and after populating the whole queue, call notify on consumer
//
//    void getSize() {
//        //some string.
//        //info
//    }
//
//    @Override
//    public synchronized T consume(long timeout) {
//        try {
//            while(queue.isEmpty())  //when do we need to wakeup
//                this.wait(timeout);
//            if(queue.isEmpty()) return null; // after timeout what happens to that thread and how to check condn
//            return queue.poll();
//        } catch(InterruptedException e) {
//            System.out.println(e.toString());
//            throw new RuntimeException(e.toString());
//        } finally {
////            if(queue.isEmpty()) return
//            this.notify();
//        }
//    }
//
//    @Override
//    public synchronized void produce(T t, long timeout) {
//        try { //when do we want to wait
//            while(queue.size() == capacity) this.wait();  //spurious wakeup
//            queue.offer(t);
//        } catch (InterruptedException e) {
//            System.out.println(e.toString());
//        } finally {
//            this.notifyAll();
//        }
//    }
//}
//
