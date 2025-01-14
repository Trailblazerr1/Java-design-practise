package com.lld.problems.parkinglot;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;

public class ParkingLot {
    private final List<Level> levelsList; //parking lot won't contain vehicles

    private static class ParkingLotHolder {
        final static ParkingLot INSTANCE = new ParkingLot(); //make this singleton using holder idiom
    }

    private ParkingLot() {   //make this private so noone can instantiate it
        this.levelsList = new ArrayList<>(); //throwing error
    }

    public static ParkingLot getParkingLot() {
        return ParkingLotHolder.INSTANCE;
    }

    public Receipt parkVehicle(IVehicle vehicle) {
        Objects.requireNonNull(vehicle,"Vehicle cannot be null");
        Optional<Spot> spot = findSpaceAvailableForParking(vehicle);
        spot.map(s -> {                    //map will only execute if optional contains valid spot else throw error
            s.parkVehicle(vehicle);
            return new Receipt(vehicle, s);  //map can be nested
        }).orElseThrow(() -> new ParkingLotFullException("Parking lot full"));
    }


    public void clearSpot(Receipt receipt) {
        Objects.requireNonNull(receipt,"Receipt cannot be null");
        Objects.requireNonNull(receipt.spot,"Spot is null, invalid receipt");
        if(!receipt.isValid) {
            throw new InvalidTicketException("Receipt used");
        }
        receipt.spot.freeThisSpot();  //Use getter setter instead
        receipt.invalidate();
    }

    public BigDecimal calculateParkingPrice(Receipt receipt) {
        Objects.requireNonNull(receipt,"Receipt cannot be null");
        long durationHours = Duration.between(receipt.entryTime, Instant.now()).toHours();//ignore extra time
        int hourlyRate = receipt.vehicle.getHourlyRate();
        return BigDecimal.valueOf(hourlyRate).multiply(BigDecimal.valueOf(durationHours));
    }

    public ParkingLotStatus getParkingLotCurrentStatus() {
        int total = levelsList.size() * 100;
        int filled = 0;
        for(Level l : levelsList) {
            filled+=l.availableCount;
        }
        return new ParkingLotStatus(total,filled);
    }

    private Optional<Spot> findSpaceAvailableForParking(IVehicle vehicle) {
        for(Level l : levelsList) {
            Optional<Spot> spot = l.findSpaceAvailableForParking(vehicle);
            if(spot.isPresent()) return spot;
        }
        return Optional.empty();
    }
}



public class Level {
    private final int totalCount = 100;
    int floorNo;  //make private final
    int availableCount;
    List<Spot> spotsList;

    public Level(int floorNo, List<Spot> spotsList) {
        this.floorNo = floorNo;
        this.spotsList = spotsList;
        this.availableCount = totalCount;
    }


    public Optional<Spot> findSpaceAvailableForParking(IVehicle vehicle) {
        if(availableCount == 0) return Optional.empty();
        for(Spot s: spotsList) {
            if(!s.isOccupied && s.vehicleType == vehicle.vehicleType) {
                s.updateSpotsOccupied();
                availableCount--;
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }

}


public class Spot {
    VehicleType vehicleType;
    boolean isOccupied;
    int spotNo;
    Level l;

    public Spot(Level l, int spotNo, VehicleType vehicleType) {
        this.l = l;
        this.spotNo = spotNo;
        this.isOccupied = false;
        this.vehicleType = vehicleType;
    }
    void updateSpotsOccupied() {
        this.isOccupied = true;
    }

    public void parkVehicle(IVehicle vehicle) {
        isOccupied = true;
    }

    public void freeThisSpot() {
        this.l.availableCount++;
        this.isOccupied = false;
    }
}

public enum VehicleType {
    CAR,
    BIKE,
    TRUCK
}

public abstract class IVehicle {
    private final String regNo;
    final VehicleType vehicleType;
    private final String color;

    public IVehicle(String regNo, String color, VehicleType vehicleType) {
        this.regNo = regNo;
        this.color = color;
        this.vehicleType = vehicleType;
    }

    public abstract int getHourlyRate() ;
}

public class Car extends IVehicle {
    private final static int hourlyRate = 2;

    public Car(String regNo, String color, VehicleType vehicleType) {
        super(regNo, color, vehicleType);
    }

    @Override
    public int getHourlyRate() {
        return hourlyRate;
    }
}

public class Receipt {
    boolean isValid;
    final IVehicle vehicle;
    final Spot spot;
    final Instant entryTime;


    public Receipt(IVehicle vehicle, Spot spot) {
        this.vehicle = vehicle;
        this.spot = spot;
        entryTime = Instant.now();
        isValid = true ;
    }

    public void invalidate() {
        this.isValid = false;
    }
}

public class ParkingLotFullException extends RuntimeException {
    public ParkingLotFullException(String msg) {
        super(msg);
    }
}

public class InvalidTicketException extends RuntimeException {
    public InvalidTicketException(String msg) {
        super(msg);
    }
}

public class ParkingLotStatus {
    private int total;
    private int filled;
    public ParkingLotStatus(int total, int filled) {
        this.total = total;
        this.filled = filled;
    }
}
