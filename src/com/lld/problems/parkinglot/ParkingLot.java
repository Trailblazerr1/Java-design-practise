package com.lld.problems.parkinglot;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ParkingLot {
    private final List<Level> levelsList; //parking lot won't contain vehicles
    private final ReadWriteLock lock;

    private static class ParkingLotHolder {
        final private static ParkingLot INSTANCE = new ParkingLot(); //make this singleton using holder idiom
    }

    private ParkingLot() {   //make this private so noone can instantiate it
        this.levelsList = new ArrayList<>(); //throwing error
        this.lock = new ReentrantReadWriteLock();
    }

    public static ParkingLot getParkingLot() {
        return ParkingLotHolder.INSTANCE;
    }

    public void addLevel(Level level) { //if we didn't add lock here, then
        lock.writeLock().lock(); //in method getParkingLotCurrentStatus
        try {    //we read total and occupied spots in two different lines
            levelsList.add(level);  //if a thread adds a new level in between
        } finally {     //those lines, then occupied would be more than total
            lock.writeLock().unlock();  //we have to put lock on getParkingLotCurrentStatus method too
        }        //So it can access proper value, but it would be a read lock
    }

    //Suppose we don't take a lock on this method, then T1 and T2 threads go inside simultaneously and call
    //findAvailableSpot(), suppose both return the same spot. Now while occupying, only one thread will be able
    //to write and other thread will throw an exception (due to double checking)
    //which is not desired, since spots are empty. So we take read lock here, so two
    //threads won't get same same and conflict on write
    public Receipt parkVehicle(Vehicle vehicle) {
        Objects.requireNonNull(vehicle,"Vehicle cannot be null");
        lock.readLock().lock();
        try {
            Optional<Spot> spot = findSpaceAvailableForParking(vehicle);
            spot.map(s -> {                    //map will only execute if optional contains valid spot else throw error
                s.parkVehicle();
                return new Receipt(vehicle, s);  //map can be nested
            }).orElseThrow(() -> new ParkingLotFullException("Parking lot full"));
        } finally {
            lock.readLock().unlock();
        }
    }

    //Receipt is unique per parking transaction and spot class
    //already handles clearSpot thread safely, so no need of thread safe here.
    //make all other fields except isOccupied immutable
    public void clearSpot(Receipt receipt) {
        Objects.requireNonNull(receipt,"Receipt cannot be null");
        if(!receipt.isValid) {
            throw new InvalidTicketException("Receipt used");
        }
        Spot spot = receipt.spot;//Use getter setter instead
        Level level = spot.l;
        level.clearSpot(spot); //Don't access spot directly, go via level
        receipt.invalidate();
    }

    //Same logic as above for not using thread safe
    public BigDecimal calculateParkingPrice(Receipt receipt) {
        Objects.requireNonNull(receipt,"Receipt cannot be null");
        if(!receipt.isValid) {  //same receipt can't come twice, leading to multithreaded situation
            throw new InvalidTicketException("Receipt used");
        }  //above statements should be in a new method
        Duration durationHours = Duration.between(receipt.entryTime, Instant.now());//ignore extra time
        return receipt.vehicle.calculateFee(durationHours); //putting behavior close to the data it operates
        //so it has been moved to Vehicle, SRP- ParkingLot doesn't need to know how fees are calculated
    }

    //Should be lock as explained in addLevel() for proper level data
    public ParkingLotStatus getParkingLotCurrentStatus() {
        lock.readLock().lock();
        try {
            int total = 0;
            int filled = 0;
            for (Level l : levelsList) {
                total += l.totalCount;
                filled += l.getOccupiedSeats();
            }
            return new ParkingLotStatus(total, filled);
        } finally {
            lock.readLock().unlock();
        }
    }

    //Two threads might find the same spot empty, so this must by locked.
    //It'll cause one of them to fail, if they went to occupy later.
    private Optional<Spot> findSpaceAvailableForParking(Vehicle vehicle) {
        lock.readLock().lock();
        try {
            for (Level l : levelsList) {
                Optional<Spot> spot = l.findSpaceAvailableForParking(vehicle);
                if (spot.isPresent()) return spot;
            }
            return Optional.empty();
        } finally {
            lock.readLock().unlock();
        }
    }
}



public class Level {
    final int floorNo;  //make private final
    final int totalCount;
    final List<Spot> spotsList;
    private final ReadWriteLock lock ;

    public Level(int floorNo, int totalCount) {
        this.floorNo = floorNo;
        this.spotsList = new ArrayList<>(totalCount);
        this.totalCount = totalCount;
        this.lock = new ReentrantReadWriteLock();
        initializeSpots();
    }

    private void initializeSpots() {
        for(int i =0; i< totalCount; i++) {
            VehicleType vehicleType = (i%3 == 0) ? VehicleType.BIKE : (i % 2 == 0) ? VehicleType.CAR : VehicleType.TRUCK;
            spotsList.add(new Spot(this,i,vehicleType));
        }
    }

    public void clearSpot(Spot s) {
        lock.readLock().lock();
        try {
            s.freeThisSpot();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Optional<Spot> findSpaceAvailableForParking(Vehicle vehicle) {
        //spotlist should be traversed properly,
        //so that another thread doesn't get the same spot and throws error while occupying.
        lock.readLock().lock();
        try {
            for (Spot s : spotsList) {
                if (!s.isOccupied && s.vehicleType == vehicle.vehicleType) {
                   // s.updateSpotsOccupied(); //parking and counting, two responsibility in same method
                    return Optional.of(s);
                }
            }
            return Optional.empty();
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getOccupiedSeats() {
        //getcount should be thread safe, for proper data.
        //Suppose we didn't take lock and a newer thread comes
        //and updates spotsList then that'll lead to wrong value.
        lock.readLock().lock();
        try {
            int count = 0;
            for (Spot s : spotsList) {
                if (s.isOccupied) count++;
            }
            return count;
        } finally {
            lock.readLock().unlock();
        }
    }
}


public class Spot {
    final VehicleType vehicleType;
    boolean isOccupied;
    final int spotNo;
    final Level l;
    final ReadWriteLock lock;

    public Spot(Level l, int spotNo, VehicleType vehicleType) {
        this.l = l;
        this.spotNo = spotNo;
        this.isOccupied = false;
        this.vehicleType = vehicleType;
        this.lock = new ReentrantReadWriteLock();
    }
    //this is a critical sense and a writing operation
    //will be done here, so take lock
    //after taking lock, we have to double check, since
    //suppose there are two threads which want to update the same lock
    //then only one would update it, other would throw error.
    //Though this situation won't come due to locks on upper level classes
    void parkVehicle() {
        lock.writeLock().lock();
        try {
            if(this.isOccupied)
                throw new SpotOccupiedException("Spot Occupied");
            this.isOccupied = true;
        } finally {
            lock.writeLock().unlock();
        }
    }
    //double check condidition, if two threads came at same time
    //allow only one
    public synchronized void freeThisSpot() { //so mutliple exits won't happen at once
        lock.writeLock().lock();
        try {
            if(!this.isOccupied)
                throw new SpotOccupiedException("Spot Empty");
            this.isOccupied = false;
        } finally {
            lock.writeLock().unlock();
        }    }
}

public enum VehicleType {
    CAR,
    BIKE,
    TRUCK
}

public abstract class Vehicle {
    private final String regNo;
    final VehicleType vehicleType;
    private final String color;

    public Vehicle(String regNo, String color, VehicleType vehicleType) {
        this.regNo = regNo;
        this.color = color;
        this.vehicleType = vehicleType;
    }

    public abstract BigDecimal calculateFee(Duration duration) ;

}

public class Car extends Vehicle {
    private final BigDecimal HOURLY_RATE = BigDecimal.TEN;

    public Car(String regNo, String color) {
        super(regNo, color, VehicleType.CAR);
    }


    @Override
    public BigDecimal calculateFee(Duration duration) {
        return HOURLY_RATE.multiply(BigDecimal.valueOf(duration.toHours()));
    }
}

public class Receipt {
    final String id;
    boolean isValid;
    final Vehicle vehicle;
    final Spot spot;
    final Instant entryTime;


    public Receipt(Vehicle vehicle, Spot spot) {
        this.id = UUID.randomUUID().toString();
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

public class SpotOccupiedException extends RuntimeException {
    public SpotOccupiedException(String msg) {
        super(msg);
    }
}