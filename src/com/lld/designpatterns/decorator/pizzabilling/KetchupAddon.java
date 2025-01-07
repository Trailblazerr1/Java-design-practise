package com.lld.designpatterns.decorator.pizzabilling;

public class KetchupAddon implements Addon {
    public static final double KETCHUP_ADDON_PRICE = 5;
    public static final String KETCHUP_ADDON = " Addon ketchup";
    Meal meal;

    public KetchupAddon(Meal meal) {
        this.meal = meal;
    }

    @Override
    public String getName() {
        return meal.getName() + KETCHUP_ADDON;
    }

    @Override
    public double getPrice() {
        return meal.getPrice() + KETCHUP_ADDON_PRICE;
    }
}
