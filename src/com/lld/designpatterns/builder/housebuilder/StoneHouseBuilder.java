package com.lld.designpatterns.builder.housebuilder;

public class StoneHouseBuilder extends HouseBuilder {
    public static final String STONE = "Stone";

    @Override
    protected String getMaterial() {
        return STONE;
    }
}
