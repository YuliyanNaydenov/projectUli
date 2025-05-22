package org.shop.data.repository;

import org.shop.data.model.Receipt;

public interface ReceiptRepository {

    void saveReceipt (Receipt receipt);

    Receipt readReceipt(long serialNumber);
    long countReceipts();
}
