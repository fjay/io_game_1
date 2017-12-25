package org.io.competition.stat.game.two;

import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import org.io.competition.stat.game.BrandService;
import org.io.competition.stat.game.Record;
import org.io.competition.stat.game.one.Result1;

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
        String[] temp = line.split(" ");
        String[] dateTemp = temp[temp.length - 1].split("-");
        StringBuilder dateBuilder = new StringBuilder(8);
        dateBuilder.append(dateTemp[0]);
        for (int i = 1; i < dateTemp.length; i++) {
            if (dateTemp[i].length() == 1) {
                dateBuilder.append("0");
            }
            dateBuilder.append(dateTemp[i]);
        }
        StringBuilder brand = new StringBuilder();
        for (int i = 0; i < temp.length - 4; i++) {
            if (i != 0) {
                brand.append(" ");
            }
            brand.append(temp[i]);
        }
        Record record = new Record().setAmount(Integer.valueOf(temp[temp.length - 2])).setBrandName(brand.toString()).setDate(dateBuilder.toString());
        return record;
    }

    @Override
    public void handle(String line) {
        counter.incrementAndGet();

        Record record = parseLine(line);

        if(record == null){
            return;
        }

        Integer brandOrder = brandService.getOrder(record.getBrandName());
        if (brandOrder == null) {
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