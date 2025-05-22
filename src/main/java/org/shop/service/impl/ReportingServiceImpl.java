package org.shop.service.impl;

import org.shop.data.model.Cashier;
import org.shop.data.model.Receipt;
import org.shop.data.repository.ReceiptRepository;
import org.shop.service.InventoryService;
import org.shop.service.ReportingService;

import java.math.BigDecimal;
import java.util.List;

public class ReportingServiceImpl implements ReportingService {

    private final ReceiptRepository repository;
    private final List<Cashier> cashiers;
    private final InventoryService inventoryService;

    public ReportingServiceImpl(ReceiptRepository repository,
                                List<Cashier> cashiers,
                                InventoryService inventoryService) {
        this.repository = repository;
        this.cashiers = cashiers;
        this.inventoryService = inventoryService;
    }
    @Override
    public BigDecimal getTotalTurnover() {
        long count = repository.countReceipts();
        BigDecimal sum = BigDecimal.ZERO;
        for (long i = 1; i <= count; i++) {
            Receipt r = repository.readReceipt(i);
            if (r != null) {
                sum = sum.add(r.getTotalPrice());
            }
        }
        return sum;
    }

    @Override
    public int getIssuedReceiptsCount() {
        return (int) repository.countReceipts();
    }

    @Override
    public BigDecimal getTotalSalaries() {
        return cashiers.stream()
                .map(Cashier::getSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalPurchaseCosts() {
        return inventoryService.getTotalPurchaseCost();
    }

    @Override
    public BigDecimal getProfit() {
        return getTotalTurnover()
                .subtract(getTotalSalaries())
                .subtract(getTotalPurchaseCosts());
    }
}
