package com.lld.problems.restaurantmgmtsystem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class RestaurantManagementSystem {
    private static final AtomicInteger reservationCounter = new AtomicInteger(0);
    private static final AtomicInteger orderCounter = new AtomicInteger(0);
    private static final AtomicInteger billCounter = new AtomicInteger(0);

    private static class RestaurantHolder { //Idiom holder for singleton
        private final static RestaurantManagementSystem INSTANCE = new RestaurantManagementSystem();
    }
    private RestaurantManagementSystem() {

    }
    public static RestaurantManagementSystem getRestaurant() {
        return  RestaurantHolder.INSTANCE;
    }
    public Order placeOrder(User user, List<String> dishes) {
        int orderId = orderCounter.incrementAndGet();
        Order order = new Order(orderId,user,dishes);
        InMemoryDb.getOrders().add(order);
        return order;  //We can use observer pattern to notify customer
        //Make an interface Observer with update method. All customer to implement it.
        //Now systemNotfier class must add observer and notify them
    }

    public Bill getBill(User user, Order order) {
        int billId = billCounter.incrementAndGet();
        return new Bill(user,order);
    }

    public Bill payBill(Bill bill, PaymentStrategy paymentStrategy ) {
        return bill.payBill(bill,paymentStrategy);

    }
    public List<String> checkInventory() {
        List<String> emptyItems = new ArrayList<>();

        for(Map.Entry<String, ItemStatus> entry : InMemoryDb.getInventory().entrySet()) {
            if(entry.getValue().quantity == 0 ) emptyItems.add(entry.getKey());
        }
        return emptyItems;
    }
    public void scheduleStaff() {
        //add individual schedule
    }
    public void generateReport() {
        //generate report object will summation of all order values
    }

    public Reservation addReservation(User user, int noOfPeople, Instant time) {
        int reservationId = reservationCounter.incrementAndGet();
        Reservation reservation = new Reservation(user,reservationId,noOfPeople,time);
        InMemoryDb.getReservations().add(reservation);
        return reservation;
    }

    public Map<String,Integer> getMenu() {
        return InMemoryDb.getMenu();
    }
    public User addUser() {
        return null; //add new user
    }
}

public class InMemoryDb { //make values final, since their reference isn't gonna change
    private static final List<Employee> employees = new CopyOnWriteArrayList<>();
    private static final List<Order> orders = new CopyOnWriteArrayList<>();
    private static final List<Reservation> reservations= new CopyOnWriteArrayList<>();
    private static final List<Table> tables= new CopyOnWriteArrayList<>();
    private static final List<User> users= new CopyOnWriteArrayList<>();
    private static final Map<String,Integer> menu = new ConcurrentHashMap<>();
    private static final Map<String,ItemStatus> inventory = new ConcurrentHashMap<>();

    public static Map<String, Integer> getMenu() {
        return menu;
    }

    public static Map<String, ItemStatus> getInventory() {
        return inventory;
    }

    public static List<Employee> getEmployees() {
        return employees;
    }

    public static List<Order> getOrders() {
        return orders;
    }

    public static List<Reservation> getReservations() {
        return reservations;
    }

    public static List<Table> getTables() {
        return tables;
    }

    public static List<User> getUsers() {
        return users;
    }
}

public class ItemStatus {
    int quantity;
    Date expiryDate;
}

public class User {
    int userId;
    String name;
    List<Reservation> userReservations;
    List<Order> userOrders;
}

public enum Designation {
    CHEF, HELPER, WAITER, RECEPTIONIST
}
public class Employee extends User {
    Designation designation;
    List<Date> workingDates;
}

public enum OrderStatus {
    ORDERED, PREPARING, SERVED
}


public class Order {
    int orderId;
    OrderStatus orderStatus;
    User user;
    Instant orderTime;
    List<String> dishes;

    public Order(int orderId, User user, List<String> dishes) {
        this.orderTime = Instant.now();
        this.orderId = orderId;
        this.orderStatus = OrderStatus.ORDERED;
        this.user = user;
        this.dishes = dishes;
    }
}

public class Bill {
    User user;
    int amount;
    BillStatus billStatus;
    PaymentStrategy paymentStrategy = null;

    public Bill(User user, Order order) {
        this.user = user;
        this.billStatus = BillStatus.UNPAID;
        this.amount = getBillAmount(order);
    }

    private int getBillAmount(Order order) {
        Map<String,Integer> menu = InMemoryDb.getMenu();
        int value = 0;
        for(String dish : order.dishes ) {
            value+=menu.get(dish);
        }
        return value;
    }

    public Bill payBill(Bill bill, PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
        paymentStrategy.payBill(bill);
        return bill;
    }
}
public enum BillStatus {
    PAID, UNPAID
}

public enum TableStatus {
    OCCUPIED,AVAILABLE
}

public class Table {
    int tableNo;
    TableStatus tableStatus;
    int noOfChairs;
}

public interface PaymentStrategy {
    void payBill(Bill bill);
}

public class CreditCardPaymentStrategy implements PaymentStrategy{
    @Override
    public void payBill(Bill bill) {
        bill.billStatus = BillStatus.PAID;
    }
}

public class Reservation {
    User user;
    int reservationId;
    Instant reservationTime;
    Table table;

    public Reservation(User user, int reservationId, int noOfPeople, Instant time) {
        this.user = user;
        this.reservationTime = time;
        this.reservationId = reservationId;
        table = getTableforReservation(noOfPeople,time);
        if(table == null) {
            throw new RuntimeException("No tables available");
        }
    }

    private Table getTableforReservation(int noOfPeople, Instant time) {
        List<Table> tables  = InMemoryDb.getTables();
        for(Table table: tables) {
            if(table.tableStatus == TableStatus.AVAILABLE && table.noOfChairs >= noOfPeople)
                return table;
        }
        return null;
    }

}