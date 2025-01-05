package com.lld.design.builder.housebuilder;

public class Driver {
    public static void main(String[] args) {
        StoneHouseBuilder stoneHouseBuilder = new StoneHouseBuilder();
        String stoneHouse = stoneHouseBuilder.addWalls().addDoorsAndWindows().addGate().build();
        System.out.println(stoneHouse);

        WoodenHouseBuilder woodenHouseBuilder = new WoodenHouseBuilder();
        String woodenHouse = woodenHouseBuilder.addWalls().addDoorsAndWindows().addGate().build();
        System.out.println(woodenHouse);
    }
}
