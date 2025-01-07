package com.lld.designpatterns.builder.housebuilder;

public abstract class HouseBuilder {
    private StringBuilder description = new StringBuilder();

    protected abstract String getMaterial();
    public HouseBuilder addWalls() {
        description.append(getMaterial()).append(" Walls ");
        return this;
    }

    public HouseBuilder addDoorsAndWindows() {
        description.append(getMaterial()).append(" Doors and Windows ");
        return this;
    }

    public HouseBuilder addLawn() {
        description.append("Lawn ");
        return this;
    }

    public HouseBuilder addGate() {
        description.append(getMaterial()).append(" Gate ");
        return this;
    }

    public HouseBuilder parkingSpot() {
        description.append("Parking Spot ");
        return this;
    }

    public String build() {
        return description.toString();
    }

}
