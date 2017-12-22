package com.vipsfin.competition.stat.game.two;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Jay Wu
 */
public class Result2 {
    private Integer order = 0;
    private BigDecimal amount = new BigDecimal(0);
    private Integer count = 0;

    public BigDecimal getAmount() {
        return amount;
    }

    public Result2 setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public Integer getCount() {
        return count;
    }

    public Result2 setCount(Integer count) {
        this.count = count;
        return this;
    }

    public Integer getOrder() {
        return order;
    }

    public Result2 setOrder(Integer order) {
        this.order = order;
        return this;
    }

    @Override
    public String toString() {
        return order + "|" + count + "|" + amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result2 Result2 = (Result2) o;
        return Objects.equals(order, Result2.order);
    }

    @Override
    public int hashCode() {

        return Objects.hash(order);
    }
}