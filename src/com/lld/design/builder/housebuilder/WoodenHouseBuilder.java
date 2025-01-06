package com.lld.design.builder.housebuilder;

public class WoodenHouseBuilder extends HouseBuilder {
    public static final String WOOD = "Wood";

    @Override
    protected String getMaterial() {
        return WOOD;
    }
}
