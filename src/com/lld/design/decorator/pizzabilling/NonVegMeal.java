package com.lld.design.decorator.pizzabilling;

public class NonVegMeal implements Meal {
    public static final double NONVEG_MEAL_PRICE = 70;
    public static final String NONVEG_MEAL = "Nonveg Meal";

    @Override
    public String getName() {
        return NONVEG_MEAL;
    }

    @Override
    public double getPrice() {
        return NONVEG_MEAL_PRICE;
    }
}
