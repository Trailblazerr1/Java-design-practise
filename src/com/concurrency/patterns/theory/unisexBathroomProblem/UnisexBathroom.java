package com.concurrency.patterns.theory.unisexBathroomProblem;

import java.util.concurrent.Semaphore;

enum User {
    MALE,
    FEMALE,
    NONE
}

public class UnisexBathroom {
    private int employeesInBathroom = 0;
    private User currentUser = User.NONE;
    private final Semaphore semaphore = new Semaphore(3);//max 3 allowed

    public  void maleUseBathroom() throws InterruptedException {
        //when will male has to wait. Only when it is occupied by female
        synchronized(this) {
            while (currentUser.equals(User.FEMALE))
                this.wait();
            semaphore.acquire(); //Thread still holds lock, but is dormant. So other threads are stuck.
                                //Female can't use bathroom. And none other can, until this one uses it.
            employeesInBathroom++; //So this solution may lead to starvation.
            currentUser = User.MALE;
        }
        useBathroom();
        semaphore.release(); //release the semaphore. So that, if there's a waiting thread above to acquire, gets it
        //runs next two lines and releases lock. So, this coming thread(after sleep) can use it again.
        synchronized (this) {
            employeesInBathroom--;
            if(employeesInBathroom == 0) currentUser = User.NONE;
            this.notifyAll();
        }
    }

    public void femaleUseBathroom() throws InterruptedException {
            //when will female has to wait. Only when it is occupied by male
        synchronized(this) {
            while (currentUser.equals(User.MALE))
                this.wait();
            semaphore.acquire();
            employeesInBathroom++;
            currentUser = User.FEMALE;
        }
        useBathroom();
        semaphore.release();
        synchronized (this) {
            employeesInBathroom--;
            if(employeesInBathroom == 0) currentUser = User.NONE;
            this.notifyAll();
        }
    }

    private void useBathroom() throws InterruptedException {
        System.out.println(Thread.currentThread().getName()+ " Entered");
        Thread.sleep(2000);
    }
}
