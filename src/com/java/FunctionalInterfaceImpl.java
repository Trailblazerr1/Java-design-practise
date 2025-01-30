package com.java;
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
    }
}
