package com.lld.design.decorator.pizzabilling;

public class VegMeal implements Meal {
    public static final double VEG_MEAL_PRICE = 50;
    public static final String VEG_MEAL = "Veg Meal";
    @Override
    public String getName() {
        return VEG_MEAL;
    }

    @Override
    public double getPrice() {
        return VEG_MEAL_PRICE;
    }
}
