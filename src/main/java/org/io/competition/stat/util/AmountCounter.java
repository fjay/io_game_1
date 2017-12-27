package org.io.competition.stat.util;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Jay Wu
 */
public class AmountCounter {

    private Map<Integer, BigInteger> recordAmountMap = new HashMap<>();
    private DefaultAmountValue defaultAmountValue = new DefaultAmountValue();

    public BigInteger getAmount(Integer brandOrder) {
        return recordAmountMap.get(brandOrder);
    }

    public BigInteger addAmount(Integer brandOrder, Integer amount) {
        BigInteger totalAmount = recordAmountMap.computeIfAbsent(brandOrder, defaultAmountValue);
        totalAmount = totalAmount.add(BigInteger.valueOf(amount));
        recordAmountMap.put(brandOrder, totalAmount);
        return totalAmount;
    }

    public Map<Integer, BigInteger> getRecordAmountMap() {
        return recordAmountMap;
    }

    private class DefaultAmountValue implements Function<Integer, BigInteger> {

        @Override
        public BigInteger apply(Integer integer) {
            return BigInteger.ZERO;
        }
    }
}