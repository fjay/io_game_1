package com.vipsfin.competition.stat.game.two;

import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Jay Wu
 */
public abstract class RecordLineHandler implements LineHandler {

    protected BoundedPriorityQueue<Result2> queue;
    private AtomicLong counter;
    private Map<Integer, BigDecimal> recordAmountMap = new HashMap<>();

    public RecordLineHandler(AtomicLong counter, BoundedPriorityQueue<Result2> queue) {
        this.counter = counter;
        this.queue = queue;
    }

    @Override
    public void handle(String line) {
        counter.incrementAndGet();

        String[] temp = line.split(",");
        String date = temp[0];
        Integer brandOrder = Integer.valueOf(temp[1]);
        Integer amount = Integer.valueOf(temp[2]);

        BigDecimal totalAmount = recordAmountMap.computeIfAbsent(brandOrder, k -> BigDecimal.ZERO);
        totalAmount = totalAmount.add(new BigDecimal(amount));
        recordAmountMap.put(brandOrder, totalAmount);

        Result2 result = new Result2()
                .setOrder(brandOrder)
                .setAmount(totalAmount)
                .setCount(count(brandOrder, date));

        queue.remove(result);
        queue.offer(result);
    }

    protected abstract int count(Integer brandOrder, String date);
}
