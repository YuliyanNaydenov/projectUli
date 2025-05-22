package org.shop.data.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class NonFoodProduct extends Product{

    public NonFoodProduct(Long id, String name, BigDecimal purchasePrice,
                          LocalDate expiryDate, int quantity) {
        super(id, name, purchasePrice, expiryDate, quantity);
    }

    @Override
    public Category getCategory() {
        return Category.NON_EDIBLE;
    }
}
