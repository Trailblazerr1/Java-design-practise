package com.lld.design.decorator.pizzabilling;

public class Driver {
    public static void main(String[] args) {
        Meal vegMeal = new VegMeal();
        vegMeal = new CheeseAddon(vegMeal);
        //vegMeal = new KetchupAddon(vegMeal);
        System.out.println(vegMeal.getName());
        System.out.println(vegMeal.getPrice());

    }
}