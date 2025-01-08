package com.concurrency.patterns.theory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SynchronizedCollections {
    public static void main(String[] args) {
        List<Integer> synchronizedIntegerList = Collections.synchronizedList(new ArrayList<>());
//        lock the smallest possible scope
        Thread t = new Thread(() -> {
            synchronized (synchronizedIntegerList) {
                if (synchronizedIntegerList.size() == 0)
                    synchronizedIntegerList.add(1);
            }
        });
    }
}

