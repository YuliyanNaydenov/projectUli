package org.shop.service;

import org.shop.data.model.Product;
import org.shop.exception.InsufficientQuantityException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface InventoryService {

    void deliver(Product product, int quantity);
    void remove(Product product, int quantity) throws InsufficientQuantityException;
    BigDecimal getSellingPrice(Product product, LocalDate onDate);
    List<Product> listExpiringSoon();
    Product findProductById(Long id);

    Map<Product, Integer> getStockSnapshot();


    java.math.BigDecimal getTotalPurchaseCost();
}
