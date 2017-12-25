package org.io.competition.stat.game.two;

import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import org.io.competition.stat.util.AmountCounter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Jay Wu
 */
public abstract class SimpleRecordLineHandler implements LineHandler {

    protected BoundedPriorityQueue<Result2> queue;
    private AtomicLong counter;
    protected AmountCounter amountCounter = new AmountCounter();

    public SimpleRecordLineHandler(AtomicLong counter, BoundedPriorityQueue<Result2> queue) {
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

        Result2 result = new Result2()
                .setCount(count(brandOrder, date));
        result.setOrder(brandOrder)
                .setAmount(amountCounter.addAmount(brandOrder, amount));

        queue.remove(result);
        queue.offer(result);
    }

    protected abstract int count(Integer brandOrder, String date);
}
