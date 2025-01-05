package com.lld.design.builder.housebuilder;

public class StoneHouseBuilder extends HouseBuilder {
    public static final String STONE = "Stone";

    public StoneHouseBuilder() {
        this.setDescription(STONE);
    }

    @Override
    HouseBuilder addWalls() {
        this.setDescription(this.getDescription()+ " Walls ");
        return this;
    }

    @Override
    HouseBuilder addDoorsAndWindows() {
        this.setDescription(this.getDescription()+ " Windows");
        return this;
}

    @Override
    HouseBuilder addLawn() {
        this.setDescription(this.getDescription()+ " Lawn ");
        return this;
    }

    @Override
    HouseBuilder addGate() {
        this.setDescription(this.getDescription()+ " Gate ");
        return this;
    }

    @Override
    HouseBuilder parkingSpot() {
        this.setDescription(this.getDescription()+ " Parking Spot ");
        return this;
    }

    @Override
    String build() {
        return this.getDescription();
    }

}
