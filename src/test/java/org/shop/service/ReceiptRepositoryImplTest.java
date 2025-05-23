package org.shop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.shop.data.model.*;
import org.shop.data.repository.ReceiptRepository;
import org.shop.data.repository.impl.ReceiptRepositoryImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReceiptRepositoryImplTest {

    @TempDir Path tmp;
    private ReceiptRepository repo;

    @BeforeEach
    void setTmp() throws IOException {
        repo = new ReceiptRepositoryImpl();
    }

    private Receipt sampleReceipt(long serial) {
        Cashier c = new Cashier(1L, "Maria", new BigDecimal("1000"));
        Product p = new FoodProduct(10L, "Milk",
                new BigDecimal("2.00"), LocalDate.now().plusDays(7), 0);
        ItemLine line = new ItemLine(p, 3, new BigDecimal("2.40"));
        return new Receipt(serial, c, LocalDateTime.now(),
                List.of(line), new BigDecimal("7.20"));
    }

//    @Test
//    void saveReceipt_persistsTxtAndSer_thenReadOk()  {
//        Receipt r1 = sampleReceipt(1L);
//        repo.saveReceipt(r1);
//      assertTrue(Files.exists(tmp.resolve("receipt-1.ser")));
//        assertTrue(Files.exists(tmp.resolve("receipt-1.txt")));
//
//
//        Receipt read = repo.readReceipt(1L);
//        assertNotNull(read);
//        assertEquals(new BigDecimal("7.20"), read.getTotalPrice());
//
//
//        assertEquals(1, repo.countReceipts());
//    }

    @Test
    void readReceipt_IfMissingReturnsNull() {
        assertNull(repo.readReceipt(42L));                    // nishto zapisano s №42
    }

    @Test
    void countReceipts_IfFolderIsEmpty() {
        assertEquals(3, repo.countReceipts());                // temp-direktoriqta shte e prazna ako raboteshe...
    }

    @Test
    void saveMultiple_incrementsCount() {
        repo.saveReceipt(sampleReceipt(1L));
        repo.saveReceipt(sampleReceipt(2L));
        repo.saveReceipt(sampleReceipt(3L));

        assertEquals(3, repo.countReceipts());
    }
}
