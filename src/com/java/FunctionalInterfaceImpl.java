package com.java;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

interface FunctionalInterfaceExample {
    abstract void callMe();
    static void staticMethod() {
        System.out.println("Hey");
    }
}

public class FunctionalInterfaceImpl implements FunctionalInterfaceExample{
    public static void main(String[] args) {
        FunctionalInterfaceExample oldest = new FunctionalInterfaceImpl();
        oldest.callMe();
    }
    @Override
    public void callMe() {
        System.out.println("Child");
    }
}
class LambdaImpl {
    public static void main(String[] args) {
        FunctionalInterfaceExample oldest = new FunctionalInterfaceImpl() {
            public void callMe() {
                System.out.println("In anonymous class");
            }
        };
        FunctionalInterfaceExample older = () -> System.out.println("In lambda");
        FunctionalInterfaceExample old = System.out::println;
        oldest.callMe();
        older.callMe();
        old.callMe();
        StringBuilder sb = new StringBuilder("Hello Consumer");
        Consumer<StringBuilder> consumer = builder ->  System.out.println(sb.toString());
        Supplier<String> producer = () -> "Hello from producer";
        Function<Integer,Integer> square = no -> no*no;
        Predicate<Integer> testEven  = no -> no % 2 == 0;

        consumer.accept(sb);
        System.out.println(producer.get());
        System.out.println(square.apply(2));
        System.out.println(testEven.test(2));
    }
}
