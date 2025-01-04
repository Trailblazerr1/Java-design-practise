package com.lld.design.decorator.pizzabilling;

public class NonVegMeal implements Meal {
    @Override
    public String getName() {
        return "";
    }

    @Override
    public double getPrice() {
        return 0;
    }
}
