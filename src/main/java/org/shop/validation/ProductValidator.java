package org.shop.validation;

import org.shop.data.model.Product;
import org.shop.exception.InvalidProductDataException;

public class ProductValidator {

    public void validate(Product product) {
        if (product == null) {
            throw new InvalidProductDataException("Product cannot be null");
        }
    }
}
