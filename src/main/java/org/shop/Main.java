package org.shop;

import org.shop.data.model.Cashier;
import org.shop.data.model.FoodProduct;
import org.shop.data.model.NonFoodProduct;
import org.shop.data.model.Product;
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
         * 2. Данни за касиер и продукти
         * --------------------------------------------------------- */

        Cashier maria = new Cashier(1L, "Maria", new BigDecimal("150"));

        Product milk = new FoodProduct(
                5L, "Milk",
                new BigDecimal("2.0"),
                LocalDate.now().plusDays(7), 0);

        Product PS5 = new NonFoodProduct(
                7654L, "PlayStation 5",
                new BigDecimal("999.99"),
                LocalDate.now().plusYears(4), 0
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

        /* ---------------------------------------------------------
         * 4. Продажба
         * --------------------------------------------------------- */
        Map<Long, Integer> basket = Map.of(
                milk.getId(), 3,
                PS5.getId(), 1,
                tv.getId(), 1);

        cashRegister.selling(maria, basket, new BigDecimal("7000.00"));

        /* ---------------------------------------------------------
         * 5. Отчети – печат в конзолата
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
        System.out.println("=== STOCK ===");
        inventory.getStockSnapshot()
                .forEach((p, quantity) ->
                        System.out.printf("%-20s Quantity: %d%n", p.getName(), quantity));

    }

}