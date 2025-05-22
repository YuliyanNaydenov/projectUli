package org.shop.data.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Receipt implements Serializable {
    private Long serialNumber;
    private Cashier cashier;
    private LocalDateTime receiptDate;

    private List<ItemLine> lines; // list e nai ucdachno pozvolqva povtoreniq i pazi red
    private BigDecimal totalPrice;

    public Receipt(Long serialNumber, Cashier cashier, LocalDateTime receiptDate,
                   List<ItemLine> lines, BigDecimal totalPrice) {
        this.serialNumber = serialNumber;
        this.cashier = cashier;
        this.receiptDate = receiptDate;
        this.lines = List.copyOf(lines);
        this.totalPrice = totalPrice;
    }

    public Long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Long serialNumber) {
        this.serialNumber = serialNumber;
    }
    public Cashier getCashier() {
        return cashier;
    }
    public void setCashier(Cashier cashier) {
        this.cashier = cashier;
    }
    public LocalDateTime getReceiptDate() {
        return receiptDate;
    }
    public void setReceiptDate(LocalDateTime receiptDate) {
        this.receiptDate = receiptDate;
    }
    public List<ItemLine> getLines() {
        return lines;
    }
    public void setLines(List<ItemLine> lines) {
        this.lines = lines;
    }
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Receipt receipt = (Receipt) o;
        return Objects.equals(serialNumber, receipt.serialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(serialNumber);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Receipt #").append(serialNumber).append(System.lineSeparator());
        sb.append("Cashier: ").append(cashier.getName()).append(" (id=").append(cashier.getId()).append(")").append(System.lineSeparator());
        sb.append("Issued at: ").append(receiptDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append(System.lineSeparator());
        sb.append("-------------------------------------------------").append(System.lineSeparator());
        lines.forEach(l -> sb.append(String.format("%-20s x%-3d  %-8.2f  =  %-8.2f%n",
                l.getProduct().getName(),
                l.getQuantity(),
                l.getItemPrice(),
                l.getLineTotal())));
        sb.append("-------------------------------------------------").append(System.lineSeparator());
        sb.append(String.format("TOTAL: %.2f", totalPrice));
        return sb.toString();
    }
}
