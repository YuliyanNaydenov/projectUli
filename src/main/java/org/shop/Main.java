package org.shop;

import org.shop.data.model.*;
import org.shop.data.repository.ReceiptRepository;
import org.shop.data.repository.impl.ReceiptRepositoryImpl;
import org.shop.service.CashRegisterService;
import org.shop.service.InventoryService;
import org.shop.service.ReportingService;
import org.shop.service.impl.CashRegisterServiceImpl;
import org.shop.service.impl.InventoryServiceImpl;
import org.shop.service.impl.ReportingServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {



        /* ---------------------------------------------------------
         * 1. Начална конструкция (Inventory, Persistence, Cash Register)
         * --------------------------------------------------------- */

        InventoryService inventory =  new InventoryServiceImpl(
                new BigDecimal("0.25"),      // 25% mark‑up за EDIBLE
                new BigDecimal("0.30"),      // 30% mark‑up за NON_EDIBLE
                3,                               // discountDays
                new BigDecimal("0.60"));     // 60% markdown

        ReceiptRepository repo = new ReceiptRepositoryImpl();
        CashRegisterService cashRegister = new CashRegisterServiceImpl(inventory, repo);

        /* ---------------------------------------------------------
         * 2. Дневни данни за касиер и продукти
         * --------------------------------------------------------- */

        Cashier maria = new Cashier(1L, "Maria", new BigDecimal("150"));

        Product milk = new FoodProduct(
                5L, "Milk",
                new BigDecimal("2.0"),
                LocalDate.now().plusDays(2), 0);

        Product PS5 = new NonFoodProduct(
                7654L, "PlayStation 5",
                new BigDecimal("999.99"),
                LocalDate.now().plusYears(4), 1
        );

        Product tv = new NonFoodProduct(
                5678L, "TV",
                new BigDecimal("1000.0"),
                LocalDate.now().plusYears(4), 1);

        /* ---------------------------------------------------------
         * 3. Зареждане на магазина
         * --------------------------------------------------------- */

        inventory.deliver(milk, 5);
        inventory.deliver(PS5,1);
        inventory.deliver(tv, 0);
        inventory.remove(milk,1);

        /* ---------------------------------------------------------
         * 4. Продажби + сериализация и десериализация
         * --------------------------------------------------------- */

        Map<Long, Integer> basket = Map.of(
                milk.getId(), 3,
                PS5.getId(), 1,
                tv.getId(), 1);

        Receipt soldReceipt = cashRegister.selling(maria, basket, new BigDecimal("7000.00"));

        long serial = soldReceipt.getSerialNumber();
        Receipt fromDisk = repo.readReceipt(serial);

        System.out.println();
        System.out.println("=== READ BACK FROM .ser ===");
        System.out.println(fromDisk);
        System.out.println();

        /* ---------------------------------------------------------
         * 5. Отчети (печат в конзолата)
         * --------------------------------------------------------- */

        ReportingService reporting = new ReportingServiceImpl(repo, List.of(maria), inventory);

        System.out.println("========== DAILY REPORT ==========");
        System.out.println("Turnover:          " + reporting.getTotalTurnover());
        System.out.println("Receipts issued:   " + reporting.getIssuedReceiptsCount());
        System.out.println("Purchase costs:    " + reporting.getTotalPurchaseCosts());
        System.out.println("Salaries:          " + reporting.getTotalSalaries());
        System.out.println("----------------------------------");
        System.out.println("PROFIT:            " + reporting.getProfit());
        System.out.println("==================================");

        /* ---------------------------------------------------------
         * 7. Проверка на инвентар
         * --------------------------------------------------------- */

        System.out.println();
        System.out.println("============= STOCK =============");
        inventory.getStockState()
                .forEach((p, quantity) ->
                        System.out.printf("%-20s Quantity: %d%n", p.getName(), quantity));
        System.out.println("=================================");


        /* ---------------------------------------------------------
         * 8. Проверка за продукти с изтичащ срок
         * --------------------------------------------------------- */

        System.out.println();
        System.out.println("========== ZA BRAKUVANE ============");

        System.out.println(inventory.listExpiringSoon());

        System.out.println("====================================");


    }

}