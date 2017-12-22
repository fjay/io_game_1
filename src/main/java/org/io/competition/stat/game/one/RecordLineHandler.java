package org.io.competition.stat.game.one;

import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import org.io.competition.stat.game.BrandService;
import org.io.competition.stat.game.Record;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Jay Wu
 */
public class RecordLineHandler implements LineHandler {

    protected BoundedPriorityQueue<Result1> queue;
    protected AtomicLong counter;
    protected Map<Integer, BigDecimal> recordAmountMap = new HashMap<>();

    protected BrandService brandService;

    public RecordLineHandler(BrandService brandService, AtomicLong counter, BoundedPriorityQueue<Result1> queue) {
        this.brandService = brandService;
        this.counter = counter;
        this.queue = queue;
    }

    public static Record parseLine(String line) {
        Record record = new Record();

        String[] temp = line.split(" ");
        int pos = temp.length;

        String[] dateTemp = temp[--pos].split("-");
        StringBuilder dateBuilder = new StringBuilder(8);
        dateBuilder.append(dateTemp[0]);
        for (int i = 1; i < dateTemp.length; i++) {
            if (dateTemp[i].length() == 1) {
                dateBuilder.append("0");
            }
            dateBuilder.append(dateTemp[i]);
        }

        record.setDate(dateBuilder.toString())
                .setAmount(Integer.valueOf(temp[--pos]))
                .setLocation(temp[--pos]);
        String desc = temp[--pos];

        StringBuilder brand = new StringBuilder();
        for (int i = 0; i < pos; i++) {
            brand.append(temp[i]);
            if (i < pos - 1) {
                brand.append(" ");
            }
        }

        record.setBrandName(brand.toString());
        return record;
    }

    @Override
    public void handle(String line) {
        counter.incrementAndGet();

        Record record = parseLine(line);

        Integer brandOrder = brandService.getOrder(record.getBrandName());
        if (brandOrder == null) {
            return;
        }

        if (!record.getLocation().equals("VIP_NH")) {
            return;
        }

        int date = Integer.valueOf(record.getDate());
        if (date < 20110101 || date > 20161231) {
            return;
        }

        BigDecimal totalAmount = recordAmountMap.computeIfAbsent(brandOrder, k -> BigDecimal.ZERO);
        totalAmount = totalAmount.add(new BigDecimal(record.getAmount()));
        recordAmountMap.put(brandOrder, totalAmount);

        Result1 result = new Result1()
                .setOrder(brandOrder)
                .setAmount(totalAmount);

        queue.remove(result);
        queue.offer(result);
    }
}