package com.lld.designpatterns.decorator.pizzabilling;

public class CheeseAddon implements Addon {
    public static final double CHEESE_ADDON_PRICE = 20;
    public static final String CHEESE_ADDON = " Addon cheese";
    Meal meal;

    public CheeseAddon(Meal meal) {
        this.meal = meal;
    }

    @Override
    public String getName() {
        return meal.getName() + CHEESE_ADDON;
    }

    @Override
    public double getPrice() {
        return meal.getPrice() + CHEESE_ADDON_PRICE;
    }
}
