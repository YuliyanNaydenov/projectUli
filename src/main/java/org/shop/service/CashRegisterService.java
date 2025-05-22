package org.shop.service;

import org.shop.data.model.Cashier;
import org.shop.data.model.Receipt;
import org.shop.exception.ExpiredProductException;
import org.shop.exception.InsufficientQuantityException;

import java.math.BigDecimal;
import java.util.Map;

public interface CashRegisterService {

    Receipt selling (Cashier cashier, Map< Long, Integer> productIdToQty,
                     BigDecimal cashPaid)
            throws InsufficientQuantityException,
            ExpiredProductException,
            InsufficientQuantityException;
}
