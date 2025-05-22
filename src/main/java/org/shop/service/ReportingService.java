package org.shop.service;

import java.math.BigDecimal;

public interface ReportingService {

    BigDecimal getTotalTurnover();
    int getIssuedReceiptsCount();
    BigDecimal getTotalSalaries();
    BigDecimal getTotalPurchaseCosts();
    BigDecimal getProfit();
}
