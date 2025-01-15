package com.lld.problems.vendingmachine;

import java.util.List;

public class VendingMachinDriver {
    public static void main(String[] args) {
        ProductInfo productInfo = new ProductInfo("Biscuit",10);
        VendingMachine vendingMachine = new VendingMachine(List.of(productInfo));
    }
}
