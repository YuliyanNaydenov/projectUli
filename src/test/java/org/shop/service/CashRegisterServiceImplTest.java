package org.shop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.shop.data.model.Cashier;
import org.shop.data.model.FoodProduct;
import org.shop.data.model.Product;
import org.shop.data.model.Receipt;
import org.shop.data.repository.ReceiptRepository;
import org.shop.exception.ExpiredProductException;
import org.shop.exception.InsufficientFundsException;
import org.shop.exception.InsufficientQuantityException;
import org.shop.service.impl.CashRegisterServiceImpl;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CashRegisterServiceImplTest {


        @Mock
        InventoryService inventory;
        @Mock
        ReceiptRepository repo;

        Cashier cashier;
        Product product;
        CashRegisterService cash;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            cash = new CashRegisterServiceImpl(inventory, repo);
            cashier = new Cashier(1, "Maria", new BigDecimal("1000"));
            product  = new FoodProduct(1L, "Milk", new BigDecimal("2"),
                    LocalDate.now().plusDays(5), 3);

        }



    @Test
    void selling_SaveReceiptAndUpdateInventory() {
        // given
        Map<Long,Integer> basket = Map.of(1L, 2);
        Product milk = new FoodProduct(1L, "Milk", new BigDecimal("2"),LocalDate.now().plusDays(5), 10);

        when(inventory.findProductById(1L)).thenReturn(milk);
        when(inventory.getSellingPrice(eq(milk), any())).thenReturn(new BigDecimal("2.40"));

        // when
        Receipt receipt = cash.selling(cashier,basket, new BigDecimal("10.00"));

        // then
        verify(inventory).remove(milk, 2);
        verify(repo).saveReceipt(receipt);

        assertEquals(new BigDecimal("4.80"), receipt.getTotalPrice());
        assertEquals(1, receipt.getLines().size());
    }

    @Test
    void selling_WhenCashIsNotEnough()  {
        when(inventory.findProductById(1L)).thenReturn(product);
        when(inventory.getSellingPrice(eq(product), any())).thenReturn(new BigDecimal("100"));

        assertThrows(InsufficientFundsException.class,
                () -> cash.selling(cashier,
                        Map.of(1L, 1),
                        new BigDecimal("50")));

        verify(repo, never()).saveReceipt(any());
    }


    @Test
    void selling_WhenQuantityIsInsufficient() {
        when(inventory.findProductById(1L)).thenReturn(product);
        when(inventory.getSellingPrice(eq(product), any())).thenReturn(new BigDecimal("2.40"));
        doThrow(new InsufficientQuantityException("")).when(inventory).remove(product, 3);

        assertThrows(InsufficientQuantityException.class,
                () -> cash.selling(cashier, Map.of(1L, 3), new BigDecimal("20")));
        verify(repo, never()).saveReceipt(any());
    }

    @Test
    void selling_WhenProductIsExpired() {
        Product expired = new FoodProduct(2L, "Yoghurt",
                new BigDecimal("1"), LocalDate.now().minusDays(1), 5);

        when(inventory.findProductById(2L)).thenReturn(expired);
        when(inventory.getSellingPrice(eq(expired), any()))
                .thenThrow(new ExpiredProductException(""));

        assertThrows(ExpiredProductException.class,
                () -> cash.selling(cashier, Map.of(2L, 1), new BigDecimal("5")));
        verify(repo, never()).saveReceipt(any());
    }

    @Test
    void selling_IfItIncrementsSerialNumber()  {
        when(inventory.findProductById(1L)).thenReturn(product);
        when(inventory.getSellingPrice(eq(product), any()))
                .thenReturn(new BigDecimal("2"));

        cash.selling(cashier, Map.of(1L, 1), new BigDecimal("10"));
        cash.selling(cashier, Map.of(1L, 1), new BigDecimal("10"));

        ArgumentCaptor<Receipt> captor = ArgumentCaptor.forClass(Receipt.class);
        verify(repo, times(2)).saveReceipt(captor.capture());

        List<Receipt> saved = captor.getAllValues();
        assertEquals(1L, saved.get(0).getSerialNumber());   // -> #1
        assertEquals(2L, saved.get(1).getSerialNumber());   // -> #2
    }


}
