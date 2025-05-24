package org.shop.service.impl;

import org.shop.data.model.Category;
import org.shop.data.model.Product;
import org.shop.exception.ExpiredProductException;
import org.shop.exception.InsufficientQuantityException;
import org.shop.service.InventoryService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class InventoryServiceImpl implements InventoryService {

    private final Map<Long, Product> inventory = new HashMap<>();
    private java.math.BigDecimal totalPurchaseCost = java.math.BigDecimal.ZERO;

    // cenoobrazuvane

    private final BigDecimal foodMarkUp;
    private final BigDecimal nonFoodMarkUp;
    private final int discountDays;
    private final BigDecimal discountPercentage;

    public InventoryServiceImpl(BigDecimal foodMarkUp,
                                BigDecimal nonFoodMarkUp,
                                int discountDays,
                                BigDecimal discountPercentage) {
        this.foodMarkUp = foodMarkUp;
        this.nonFoodMarkUp = nonFoodMarkUp;
        this.discountDays = discountDays;
        this.discountPercentage = discountPercentage;
    }

    @Override
    public void deliver(Product product, int quantity) {
        java.util.Objects.requireNonNull(product, "product");
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        Product existing = inventory.get(product.getId());
        if (existing == null) {
            product.increaseQuantity(quantity);
            inventory.put(product.getId(), product);
        } else {
            existing.increaseQuantity(quantity);
        }
        java.math.BigDecimal addedCost = product.getPurchasePrice().multiply(java.math.BigDecimal.valueOf(quantity));
        totalPurchaseCost = totalPurchaseCost.add(addedCost);
    }

    @Override
    public void remove(Product product, int quantity) throws InsufficientQuantityException {
        Objects.requireNonNull(product, "product");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        Product existing = inventory.get(product.getId());
        if (existing == null || existing.getQuantity() < quantity) {
            throw new InsufficientQuantityException("Need " + quantity +
                    " units of " + product.getName() +
                    ", available " + (existing == null ? 0 : existing.getQuantity()));
        }
        existing.decreaseQuantity(quantity);
    }

    @Override
    public BigDecimal getSellingPrice(Product product, LocalDate onDate) {
        Objects.requireNonNull(product, "product");
        if (onDate == null) onDate = LocalDate.now();

        long daysToExpiry = ChronoUnit.DAYS.between(onDate, product.getExpiryDate());
        if (daysToExpiry < 0) {
            throw new ExpiredProductException("Product " + product.getName() + " is expired.");
        }

        BigDecimal base = product.getPurchasePrice();
        BigDecimal markUp = product.getCategory() == Category.EDIBLE ? foodMarkUp : nonFoodMarkUp;
        BigDecimal price = base.add(base.multiply(markUp));

        if (daysToExpiry < discountDays) {
            price = price.subtract(price.multiply(discountPercentage));
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public List<Product> listExpiringSoon() {
        LocalDate today = LocalDate.now();

        List<Product> expiringSoon = inventory.values().stream()
                .filter(p -> {
                    long d = ChronoUnit.DAYS.between(today, p.getExpiryDate());
                    return d >= 0 && d < discountDays;
                })
                .sorted(Comparator.comparing(Product::getExpiryDate))
                .collect(Collectors.toList())
                .reversed();

        if (expiringSoon.isEmpty()) {
            System.out.println("No found products that will expire soon :D");
        }  else {
        System.out.println("Found " + expiringSoon.size() + " product(s) with soon expiry: ");
            System.out.println();
    }

        return expiringSoon;
    }


    @Override
    public Product findProductById(Long id) {
        return inventory.get(id);
    }

    @Override
    public Map<Product, Integer> getStockState() {
        return inventory.values()
                .stream()
                .collect(java.util.stream.Collectors
                        .toUnmodifiableMap(p -> p, Product::getQuantity));
    }


    @Override
    public BigDecimal getTotalPurchaseCost() {
        return totalPurchaseCost;
    }
}
