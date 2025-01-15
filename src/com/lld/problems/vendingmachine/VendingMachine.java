package com.lld.problems.vendingmachine;

import java.util.*;

public class VendingMachine {
    InventoryManager inventoryManager;
    TransactionManager transactionManager;
    MachineState currentState;

    public VendingMachine(InventoryManager inventoryManager, TransactionManager transactionManager) {
        this.currentState = new NoSelectionState(this);
        this.transactionManager = transactionManager;
        this.inventoryManager = inventoryManager;
    }

    public void restock(List<Product> products, List<ProductInfo> productInfoList) {
        inventoryManager.restock(products,productInfoList);
    }

    public void selectProduct(Product product) {
        currentState.selectProduct(product);
    }

    public void insertMoney(List<Coin> money) {
        currentState.insertMoney(money);
    }

    public Product executeTransaction() {
        return currentState.executeTransaction();
    }

    public void cancelTransaction() {
        currentState.cancelTransaction();
    }

    public void setCurrentState(MachineState currentState) {
        this.currentState = currentState;
    }
}

public class InventoryManager {
    List<ProductInfo> productInfoList;
    Map<ProductInfo,List<Product>> inventory;
    Product currentProduct;

    public InventoryManager() {
        this.productInfoList = new ArrayList<>();
        this.inventory = new HashMap<>();
        this.currentProduct = null;
    }


    public void restock(List<Product> products, List<ProductInfo> productInfoList) {
        this.productInfoList = productInfoList; //all product list
        for(Product product : products) {
            List<Product> oldList = inventory.getOrDefault(product.productInfo,new ArrayList<>());
            oldList.add(product);
            inventory.put(product.productInfo,oldList);
        }
    }

    public Product dispenseProduct() {
        List<Product> prod = this.inventory.getOrDefault(this.currentProduct.productInfo, null);
        if(prod == null) {
            throw new OutOfStockException("Product out of stock");
        }
        Product productToBeReturned = prod.remove(prod.size()-1);
        this.inventory.put(this.currentProduct.productInfo,prod);
        return productToBeReturned;
    }
}

public class TransactionManager {
    int insertedAmount;
    int refundAmount;

    public TransactionManager() {
        this.insertedAmount = 0;
        this.refundAmount = 0;
    }

    public void insertMoney(List<Coin> coins) {
        int value = 0;
        for(Coin c: coins) value+=c.getValue();
        this.insertedAmount = value;
    }


    public Product executeTransaction(InventoryManager inventoryManager) throws InsufficientFundsException, OutOfStockException{
        if(inventoryManager.currentProduct.productInfo.price > this.insertedAmount) {
            inventoryManager.currentProduct = null;
            throw new InsufficientFundsException("Not enough money. Transaction Cancelled");
        }
        //dispense current product
        Product prod = inventoryManager.dispenseProduct();
        //calculate refund
        this.refundAmount = this.insertedAmount - inventoryManager.currentProduct.productInfo.price;
        this.insertedAmount = 0;
        return  prod;
    }

    public List<Coin> processRefund() {
        int refundValue = this.refundAmount;
        //use coin change to get list of coins
        this.refundAmount = 0;
        return List.of(Coin.FIVE);

    }
}

public class ProductInfo {
    final String name;
    final int price;

    public ProductInfo(String name, int price) {
        this.name = name;
        this.price = price;
    }
}

public class Product {
    final ProductInfo productInfo;
    final String productCode;

    public Product(ProductInfo productInfo) {
        this.productInfo = productInfo;
        this.productCode = UUID.randomUUID().toString();
    }
}

public interface MachineState {
    public void selectProduct(Product product) ;

    public void insertMoney(List<Coin> coins);

    public Product executeTransaction();

    public List<Coin> processRefund();

    public void cancelTransaction();
}

public class NoSelectionState implements MachineState {
    VendingMachine vendingMachine;

    public NoSelectionState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void selectProduct(Product product) {
        this.vendingMachine.inventoryManager.currentProduct = product;
        this.vendingMachine.setCurrentState(new HasSelectionState(vendingMachine));
    }

    @Override
    public void insertMoney(List<Coin> coins) {
        throw new InvalidOperationException("No product selected yet"); //make custom error
    }

    @Override
    public Product executeTransaction() {
        throw new InvalidOperationException("No product selected yet");
    }

    @Override
    public List<Coin> processRefund() {
        throw new InvalidOperationException("No product selected yet");
    }

    @Override
    public void cancelTransaction() {
        throw new InvalidOperationException("No product selected yet");
    }
}

public class VendingState implements MachineState {
    VendingMachine vendingMachine;

    public VendingState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void selectProduct(Product product) {
        throw new InvalidOperationException("Product already selected");
    }

    @Override
    public void insertMoney(List<Coin> coins) {
        throw new InvalidOperationException("Money already inserted");
    }

    @Override
    public Product executeTransaction() {
        Product product;
        try {
             product = vendingMachine.transactionManager.executeTransaction(vendingMachine.inventoryManager);
        } catch (InsufficientFundsException e) {
            vendingMachine.setCurrentState(new NoSelectionState(vendingMachine));
            return null;
        } catch (OutOfStockException e) {
            vendingMachine.transactionManager.refundAmount = vendingMachine.transactionManager.insertedAmount;
            vendingMachine.setCurrentState(new RefundState(vendingMachine));
            return null;
        }
        vendingMachine.setCurrentState(new RefundState(vendingMachine));
        return product;

    }

    @Override
    public List<Coin> processRefund() {
        throw new InvalidOperationException("No refund to process yet");
    }

    @Override
    public void cancelTransaction() {
        throw new InvalidOperationException("No product selected yet"); //make custom error
    }
}

public class HasSelectionState implements MachineState {
    VendingMachine vendingMachine;

    public HasSelectionState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void selectProduct(Product product) {
        throw new InvalidOperationException("Product already selected");
    }

    @Override
    public void insertMoney(List<Coin> coins) {
        vendingMachine.transactionManager.insertMoney(coins);
        vendingMachine.setCurrentState(new VendingState(vendingMachine));
        vendingMachine.executeTransaction(); //call vendingState's execute transaction
    }

    @Override
    public Product executeTransaction() {
        throw new InvalidOperationException("Insert money first");
    }

    @Override
    public List<Coin> processRefund() {
        throw new InvalidOperationException("No refund to process");
    }

    @Override
    public void cancelTransaction() {
        vendingMachine.inventoryManager.currentProduct = null;
        vendingMachine.setCurrentState(new NoSelectionState(vendingMachine));
        System.out.println("Transaction cancelled. Select product again.");
    }
}

public class RefundState implements MachineState {
    VendingMachine vendingMachine;

    public RefundState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void selectProduct(Product product) {
        throw new InvalidOperationException("Product already selected");
    }

    @Override
    public void insertMoney(List<Coin> coins) {
        throw new InvalidOperationException("Product already selected");
    }

    @Override
    public Product executeTransaction() {
        throw new InvalidOperationException("Insert money first");
    }

    @Override
    public List<Coin> processRefund() {
        List<Coin> refund = vendingMachine.transactionManager.processRefund();
        vendingMachine.setCurrentState(new NoSelectionState(vendingMachine));
        return refund;
    }


    @Override
    public void cancelTransaction() {
        throw new InvalidOperationException("No product selected yet"); //make custom error
    }
}

public enum Coin {
    ONE(1),TWO(2),FIVE(5);

    private final int value;

    Coin(int value) {
        this.value = value;
    }
    public int getValue() {
        return this.value;
    }
}

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}

public class OutOfStockException extends RuntimeException {
    public OutOfStockException(String message) {
        super(message);
    }
}