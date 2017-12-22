package org.io.competition.stat.game.one;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Jay Wu
 */
public class Result1 {
    private Integer order = 0;
    private BigDecimal amount = new BigDecimal(0);

    public BigDecimal getAmount() {
        return amount;
    }

    public Result1 setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public Integer getOrder() {
        return order;
    }

    public Result1 setOrder(Integer order) {
        this.order = order;
        return this;
    }

    @Override
    public String toString() {
        return order + "|" + amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result1 Result2 = (Result1) o;
        return Objects.equals(order, Result2.order);
    }

    @Override
    public int hashCode() {

        return Objects.hash(order);
    }
}