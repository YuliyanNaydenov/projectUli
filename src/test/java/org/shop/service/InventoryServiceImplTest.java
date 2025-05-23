package org.shop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.shop.data.model.FoodProduct;
import org.shop.data.model.NonFoodProduct;
import org.shop.data.model.Product;
import org.shop.exception.ExpiredProductException;
import org.shop.exception.InsufficientQuantityException;
import org.shop.service.impl.InventoryServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryServiceImplTest {
private InventoryService inventory;
private Product milk;      // EDIBLE, 2.00 lv
private Product tv;        // NON_EDIBLE, 1 000 lv


    @BeforeEach
    void setUp() {
        inventory = new InventoryServiceImpl(
                new BigDecimal("0.20"),   // 20 % mark-up EDIBLE
                new BigDecimal("0.30"),   // 30 % mark-up NON_EDIBLE
                3,                        // markdownDays
                new BigDecimal("0.40"));  // 40 % markdown

        milk = new FoodProduct(1L, "Milk",
                new BigDecimal("2.00"),
                LocalDate.now().plusDays(7), 0);

         tv= new NonFoodProduct(2L, "TV",
                new BigDecimal("1000.00"),
                LocalDate.now().plusYears(5), 0);
    }

    @Test
    void deliver_WhenQuantityAndTotalPurchaseCostAreInsufficient() {
        inventory.deliver(milk, 10);                // +20 lv
        inventory.deliver(tv,  3);                  // +3 000 lv

        assertEquals(10, milk.getQuantity());
        assertEquals(3,  tv.getQuantity());

        assertEquals(new BigDecimal("3020.00"),
                inventory.getTotalPurchaseCost());
    }

    @Test
    void remove_IfQuantityDecreases()  {
        inventory.deliver(milk, 5);
        inventory.remove(milk, 3);
        assertEquals(2, milk.getQuantity());
    }

    @Test
    void remove_WhenNotEnoughInInventory() {
        inventory.deliver(milk, 2);
        assertThrows(InsufficientQuantityException.class,
                () -> inventory.remove(milk, 5));
    }

    @Test
    void getSellingPrice_IfItAddsMarkUp() {
        inventory.deliver(tv, 1);
        BigDecimal price = inventory.getSellingPrice(tv, LocalDate.now());
        assertEquals(new BigDecimal("1300.00"), price);     // 1000 + 30 %
    }

    @Test
    void getSellingPrice_IfItAddsMarkdownWhenNearExpiry() {
        Product bread = new FoodProduct(3L, "Bread",
                new BigDecimal("1.00"),
                LocalDate.now().plusDays(2), 0);   // < markdownDays = 3

        inventory.deliver(bread, 1);
        BigDecimal price = inventory.getSellingPrice(bread, LocalDate.now());
        // base 1.00 + 20 % = 1.20, suotvetno -40 % = 0.72
        assertEquals(new BigDecimal("0.72"), price);
    }

    @Test
    void getSellingPrice_IfProductIsExpired() {
        Product yoghurt = new FoodProduct(4L, "Yoghurt",
                new BigDecimal("0.60"),
                LocalDate.now().minusDays(1), 0);

        inventory.deliver(yoghurt, 1);
        assertThrows(ExpiredProductException.class,
                () -> inventory.getSellingPrice(yoghurt, LocalDate.now()));
    }

    @Test
    void listExpiringSoon_IfReturnsOnlyRelevantProducts() {
        Product cheese = new FoodProduct(5L, "Cheese",
                new BigDecimal("5.00"),
                LocalDate.now().plusDays(2), 0);

        inventory.deliver(cheese, 4);   // expiry < markdownDays
        inventory.deliver(tv, 1);       // expiry ne e skoro

        List<Product> soon = inventory.listExpiringSoon();
        assertEquals(1, soon.size());
        assertTrue(soon.contains(cheese));
        assertFalse(soon.contains(tv));
    }
}
