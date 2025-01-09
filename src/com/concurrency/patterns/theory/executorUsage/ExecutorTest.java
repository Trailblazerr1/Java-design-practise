package com.concurrency.patterns.theory.executorUsage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ExecutorTest {
    public static void main(String[] args)  {
        ExecutorService cachedE = Executors.newCachedThreadPool();
        Callable<List<Integer>> callable = () -> {
            List<Integer> list = new ArrayList<>();
            for(Integer i = 0;i< 100; i++)
                list.add(i);
            System.out.println(Thread.currentThread().getName());
            return list;
        };

        Future<List<Integer>> fut =  cachedE.submit(callable);

        try {
            System.out.println("value is "+ fut.get().get(0));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); //Preseve interrupt status for others to see
            throw new RuntimeException("Task interrupted",e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Task failed",e);
        }
        //for bulk execution
        List<Callable<List<Integer>>> callableList = new ArrayList<>();
        for(int i=0; i< 20 ; i++)
            callableList.add(callable);
        try {
            cachedE.invokeAll(callableList);
            System.out.println("done bulk threads");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Task failed",e);
        }


        cachedE.shutdown();
    }
}
