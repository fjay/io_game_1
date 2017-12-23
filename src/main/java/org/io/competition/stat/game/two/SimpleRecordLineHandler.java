package org.io.competition.stat.game.two;

import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Jay Wu
 */
public abstract class SimpleRecordLineHandler implements LineHandler {

    protected BoundedPriorityQueue<Result2> queue;
    private AtomicLong counter;
    private Map<Integer, BigDecimal> recordAmountMap = new HashMap<>();

    public SimpleRecordLineHandler(AtomicLong counter, BoundedPriorityQueue<Result2> queue) {
        this.counter = counter;
        this.queue = queue;
    }

    @Override
    public void handle(String line) {
        counter.incrementAndGet();

        String[] temp = line.split(",");
        Integer date = Integer.valueOf(temp[0], Character.MAX_RADIX);
        Integer brandOrder = Integer.valueOf(temp[1], Character.MAX_RADIX);
        Integer amount = Integer.valueOf(temp[2], Character.MAX_RADIX);

        BigDecimal totalAmount = recordAmountMap.computeIfAbsent(brandOrder, k -> BigDecimal.ZERO);
        totalAmount = totalAmount.add(new BigDecimal(amount));
        recordAmountMap.put(brandOrder, totalAmount);

        Result2 result = new Result2()
                .setCount(count(brandOrder, date));
        result.setOrder(brandOrder)
                .setAmount(totalAmount);

        queue.remove(result);
        queue.offer(result);
    }

    protected abstract int count(Integer brandOrder, Integer date);
}
