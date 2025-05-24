package org.shop.data.repository.impl;

import org.shop.data.model.Receipt;
import org.shop.data.repository.ReceiptRepository;
import java.io.*;
import java.nio.charset.StandardCharsets;


public class ReceiptRepositoryImpl implements ReceiptRepository {


    /** Papka za .txt i .ser file-ovete. */
    private static final File RECEIPTS_FOLDER = new File("receipts");

    static {
        if (!RECEIPTS_FOLDER.exists() && !RECEIPTS_FOLDER.mkdirs()) {
            throw new ExceptionInInitializerError(
                    "Cannot create receipts directory: " + RECEIPTS_FOLDER.getAbsolutePath());
        }
    }


    @Override
    public void saveReceipt(Receipt receipt) {
        if (receipt == null) throw new NullPointerException("receipt == null");

        String base = "receipt-" + receipt.getSerialNumber();
        File txt = new File(RECEIPTS_FOLDER, base + ".txt");
        File ser = new File(RECEIPTS_FOLDER, base + ".ser");

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(txt), StandardCharsets.UTF_8))) {

            bw.write(receipt.toString());

        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write txt file", e);
        }

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(ser))) {

            oos.writeObject(receipt);

        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write ser file", e);
        }
    }

    @Override
    public Receipt readReceipt(long serialNumber) {
        File ser = new File(RECEIPTS_FOLDER, "receipt-" + serialNumber + ".ser");
        if (!ser.exists()) return null;

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(ser))) {

            return (Receipt) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Cannot read receipt " + serialNumber, e);
        }
    }

    @Override
    public long countReceipts() {
        String[] list = RECEIPTS_FOLDER.list((d, n) -> n.endsWith(".ser"));
        if (list == null) {
            throw new IllegalStateException(
                    "Cannot list receipts in " + RECEIPTS_FOLDER.getAbsolutePath());
        }
        return list.length;
    }
}