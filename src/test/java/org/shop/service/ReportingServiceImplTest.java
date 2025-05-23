package org.shop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.shop.data.model.Cashier;
import org.shop.data.model.Receipt;
import org.shop.data.repository.ReceiptRepository;
import org.shop.service.impl.ReportingServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ReportingServiceImplTest {

    @Mock
    ReceiptRepository repo;
    @Mock InventoryService inventory;

    List<Cashier> cashiers;
    ReportingService reporting;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cashiers = List.of(
                new Cashier(1L, "Maria", new BigDecimal("800")),
                new Cashier(2L, "Ivan",  new BigDecimal("1200"))
        );
        reporting = new ReportingServiceImpl(repo, cashiers, inventory);
    }

    @Test
    void getTotalTurnover_IfItReturnsSumOfReceipts() {
        Receipt r1 = mockReceipt(1L, "100.50");
        Receipt r2 = mockReceipt(2L, "50.00");

        when(repo.countReceipts()).thenReturn(2L);
        when(repo.readReceipt(1)).thenReturn(r1);
        when(repo.readReceipt(2)).thenReturn(r2);

        assertEquals(new BigDecimal("150.50"), reporting.getTotalTurnover());
        verify(repo, times(1)).countReceipts();
    }

    @Test
    void getTotalSalaries_IfItReturnsSumOfAll() {
        assertEquals(new BigDecimal("2000"), reporting.getTotalSalaries());
    }

//    @Test
//    void getProfit_combinesAllParts() {
//        // turnover 1000, salaries 600, purchase 200 -> profit 200
//        when(repo.countReceipts()).thenReturn(1L);
//        when(repo.readReceipt(1L)).thenReturn(mockReceipt(1L, "1000"));
//        when(inventory.getTotalPurchaseCost()).thenReturn(new BigDecimal("200"));
//
//        BigDecimal profit = reporting.getProfit();
//        assertEquals(new BigDecimal("200"), profit);
//    }

    private Receipt mockReceipt(long id, String total) {
        Receipt r = mock(Receipt.class);
        when(r.getSerialNumber()).thenReturn(id);
        when(r.getTotalPrice()).thenReturn(new BigDecimal(total));
        when(r.getLines()).thenReturn(List.of());
        return r;
    }

}
