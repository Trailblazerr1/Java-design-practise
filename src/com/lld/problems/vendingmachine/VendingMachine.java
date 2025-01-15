package com.lld.problems.vendingmachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VendingMachine {
    List<ProductInfo> productInfoList;
    Map<ProductInfo,List<Product>> inventory;

    Product currentProduct;
    MachineState state;
    int insertedAmount;
    int refundAmount;

    public VendingMachine() {
        this.state = new NoSelectionState(this);
        this.insertedAmount = 0;
        currentProduct = null;
    }

    public void restock(List<Product> products, List<ProductInfo> productInfoList) {
        this.productInfoList = productInfoList; //all product list
        for(Product product : products) {
            List<Product> oldList = inventory.getOrDefault(product.productInfo,new ArrayList<>());
            oldList.add(product);
            inventory.put(product.productInfo,oldList);
        }

    }

    public void selectProduct(Product product) {
        state.selectProduct(product);
    }

    public void insertMoney(List<Coin> money) {
        state.insertMoney(money);
    }

    public Product executeTransaction() {
        return state.executeTransaction();
    }

    public void cancelTransaction() {
        state.cancelTransaction();
    }

    public void setState(MachineState state) {
        this.state = state;
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
        this.vendingMachine.setState(new HasSelectionState(vendingMachine));
        this.vendingMachine.currentProduct = product;
    }

    @Override
    public void insertMoney(List<Coin> coins) {
        throw new InvalidOperationException("No product selected yet"); //make custom error
    }

    @Override
    public Product executeTransaction() {
        throw new InvalidOperationException("No product selected yet"); //make custom error
    }

    @Override
    public List<Coin> processRefund() {
        throw new InvalidOperationException("No product selected yet");
    }

    @Override
    public void cancelTransaction() {
        throw new InvalidOperationException("No product selected yet"); //make custom error
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
        int change = vendingMachine.insertedAmount - vendingMachine.currentProduct.productInfo.price;
        //dispense current product
        List<Product> prod = vendingMachine.inventory.getOrDefault(vendingMachine.currentProduct.productInfo, null);
        if(prod == null) {
            vendingMachine.refundAmount = vendingMachine.insertedAmount;
            vendingMachine.setState(new RefundState(vendingMachine));
        }
        Product productToBeReturned = prod.remove(prod.size()-1);
        vendingMachine.inventory.put(vendingMachine.currentProduct.productInfo,prod);
        //return change
        if(change == 0) {
            vendingMachine.refundAmount = 0;
            vendingMachine.insertedAmount = 0;
            vendingMachine.setState(new NoSelectionState(vendingMachine));
        }
        else {
            vendingMachine.refundAmount = change;
            vendingMachine.setState(new RefundState(vendingMachine));
        }
        return productToBeReturned;
    }

    @Override
    public List<Coin> processRefund() {
        throw new InvalidOperationException("No refund to process");
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
        int value=0;
        for(Coin c: coins) value+=c.getValue();
        if(vendingMachine.currentProduct.productInfo.price > value) {
            vendingMachine.setState(new NoSelectionState(vendingMachine));
            vendingMachine.currentProduct = null;
            throw new InsufficientFundsException("Not enough money. Transaction Cancelled");
        }
        vendingMachine.insertedAmount = value;
        vendingMachine.setState(new VendingState(vendingMachine));
        vendingMachine.executeTransaction();
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
        throw new InvalidOperationException("No product selected yet"); //make custom error
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
        int refundValue = vendingMachine.refundAmount;
        vendingMachine.setState(new NoSelectionState(vendingMachine));
        vendingMachine.refundAmount = 0;
        vendingMachine.insertedAmount = 0;
        //use coin change to get list of coins
        return List.of(Coin.FIVE);
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