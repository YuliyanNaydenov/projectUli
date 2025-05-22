package org.shop.data.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class Cashier implements Serializable {

    private final long id;
    private String name;
    private BigDecimal dailySalary;

    public Cashier(long id, String name, BigDecimal salary) {
        this.id = id;
        this.name = name;
        this.dailySalary = salary;
    }

    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getSalary() {
        return dailySalary;
    }

    public void setSalary(BigDecimal salary) {
        this.dailySalary = salary;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cashier cashier = (Cashier) o;
        return id == cashier.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Cashier{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", salary=" + dailySalary +
                '}';
    }
}
