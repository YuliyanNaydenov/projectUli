package org.shop.data.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FoodProduct extends Product {

    public FoodProduct(Long id, String name, BigDecimal purchasePrice
            , LocalDate expiryDate, int quantity) {
        super(id, name, purchasePrice, expiryDate, quantity);
    }

    @Override
    public Category getCategory() {
        return Category.EDIBLE;
    }


}
