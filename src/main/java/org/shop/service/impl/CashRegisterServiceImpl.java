package org.shop.service.impl;

import org.shop.data.model.Cashier;
import org.shop.data.model.ItemLine;
import org.shop.data.model.Product;
import org.shop.data.model.Receipt;
import org.shop.data.repository.ReceiptRepository;
import org.shop.exception.ExpiredProductException;
import org.shop.exception.InsufficientFundsException;
import org.shop.exception.InsufficientQuantityException;
import org.shop.service.CashRegisterService;
import org.shop.service.InventoryService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CashRegisterServiceImpl implements CashRegisterService {

    private final InventoryService inventoryService;
    private final ReceiptRepository repository;
    private static long serialGenerator = 1L;
    private static synchronized long nextSerial() {
        return serialGenerator++;
    }

    public CashRegisterServiceImpl(InventoryService inventoryService,
                                   ReceiptRepository repository) {
        this.inventoryService = inventoryService;
        this.repository = repository;
    }

    @Override
    public Receipt selling(Cashier cashier,
                           Map<Long, Integer> productIdToQty,
                           BigDecimal cashPaid)
            throws InsufficientQuantityException,
            ExpiredProductException,
            InsufficientFundsException {

        Objects.requireNonNull(cashier, "cashier");
        Objects.requireNonNull(productIdToQty, "productIdToQty");
        Objects.requireNonNull(cashPaid, "cashPaid");

        List<ItemLine> lines = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : productIdToQty.entrySet()) {
            Long productId = entry.getKey();
            int qty = entry.getValue();

            Product product = inventoryService.findProductById(productId);
            if (product == null) {
                throw new InsufficientQuantityException("Product id " + productId + " not found");
            }

            BigDecimal unitPrice = inventoryService.getSellingPrice(product, LocalDate.now());
            inventoryService.remove(product, qty);
            ItemLine line = new ItemLine(product, qty, unitPrice);
            lines.add(line);
            total = total.add(unitPrice.multiply(BigDecimal.valueOf(qty)));
        }

        total = total.setScale(2, RoundingMode.HALF_UP);

        if (cashPaid.compareTo(total) < 0) {
            throw new InsufficientFundsException("Need " + total + ", but paid " + cashPaid);
        }

        Long serial = nextSerial();
        Receipt receipt = new Receipt(serial, cashier, LocalDateTime.now(), lines, total);

        repository.saveReceipt(receipt);
        return receipt;
    }
}
