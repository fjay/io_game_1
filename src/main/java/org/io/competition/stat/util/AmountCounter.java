package org.io.competition.stat.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Jay Wu
 */
public class AmountCounter {

    private Map<Integer, BigDecimal> recordAmountMap = new HashMap<>();
    private DefaultAmountValue defaultAmountValue = new DefaultAmountValue();

    public BigDecimal getAmount(Integer brandOrder) {
        return recordAmountMap.get(brandOrder);
    }

    public BigDecimal addAmount(Integer brandOrder, Integer amount) {
        BigDecimal totalAmount = recordAmountMap.computeIfAbsent(brandOrder, defaultAmountValue);
        totalAmount = totalAmount.add(new BigDecimal(amount));
        recordAmountMap.put(brandOrder, totalAmount);
        return totalAmount;
    }

    public Map<Integer, BigDecimal> getRecordAmountMap() {
        return recordAmountMap;
    }

    private class DefaultAmountValue implements Function<Integer, BigDecimal> {

        @Override
        public BigDecimal apply(Integer integer) {
            return BigDecimal.ZERO;
        }
    }
}