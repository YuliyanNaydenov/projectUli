package org.shop.data.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public abstract class Product implements Comparable<Product>, Serializable {

    protected   Long id;
    protected  String name;
    protected BigDecimal purchasePrice;
    protected LocalDate expiryDate;
    protected int quantity;

    public Product(Long id, String name, BigDecimal purchasePrice, LocalDate expiryDate, int quantity) {
        if (id == null || name == null || purchasePrice == null || expiryDate == null) {
            throw new IllegalArgumentException("Please fill characteristics (cannot be null) ");
        }
        this.id = id;
        this.name = name;
        this.purchasePrice = purchasePrice;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
    }
    public abstract Category getCategory();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }
    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }
    public void decreaseQuantity(int quantity) {
        this.quantity -= quantity;
    }

    @Override
    public int compareTo(Product other) {
        return this.expiryDate.compareTo(other.expiryDate);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", purchasePrice=" + purchasePrice +
                ", expiryDate=" + expiryDate +
                ", quantity=" + quantity +
                '}';
    }
}
