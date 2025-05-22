package org.shop.validation;

import org.shop.data.model.Cashier;
import org.shop.exception.InvalidProductDataException;

public class CashierValidator {

    public void validate(Cashier cashier) {
        if (cashier == null) {
            throw new InvalidProductDataException("A cashier must be present");
        }
    }
}
