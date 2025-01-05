package com.lld.design.builder.housebuilder;

public abstract class HouseBuilder {
    private String description;

    abstract HouseBuilder addWalls();
    abstract HouseBuilder addDoorsAndWindows();
    abstract HouseBuilder addLawn();
    abstract HouseBuilder addGate() ;
    abstract HouseBuilder parkingSpot();
    abstract String build();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
