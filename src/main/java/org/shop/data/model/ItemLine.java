package org.shop.data.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class ItemLine implements Serializable {

    private Product product;
    private int quantity;
    private BigDecimal itemPrice;

    public ItemLine(Product product, int quantity, BigDecimal itemPrice) {
        this.product = product;
        this.quantity = quantity;
        this.itemPrice = itemPrice;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getLineTotal() {
        return itemPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemLine itemLine = (ItemLine) o;
        return quantity == itemLine.quantity &&
                Objects.equals(product, itemLine.product) &&
                Objects.equals(itemPrice, itemLine.itemPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity, itemPrice);
    }

    @Override
    public String toString() {
        return "ItemLine{" +
                "product=" + product.getName() +
                ", quantity=" + quantity +
                ", itemPrice=" + itemPrice +
                '}';
    }
}
