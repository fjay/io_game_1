package org.io.competition.stat.game.one;

import com.xiaoleilu.hutool.io.LineHandler;
import org.io.competition.stat.game.BrandService;
import org.io.competition.stat.util.AmountCounter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Jay Wu
 */
public class RecordLineHandler implements LineHandler {

    protected AtomicLong counter;
    protected AmountCounter amountCounter = new AmountCounter();

    protected BrandService brandService;

    public RecordLineHandler(BrandService brandService, AtomicLong counter) {
        this.brandService = brandService;
        this.counter = counter;
    }

    @Override
    public void handle(String line) {
        counter.incrementAndGet();

        String[] temp = line.split(" ");
        if (!temp[temp.length - 3].equals("VIP_NH")) {
            return;
        }

        String[] dateTemp = temp[temp.length - 1].split("-");
        StringBuilder dateBuilder = new StringBuilder(8);
        dateBuilder.append(dateTemp[0]);
        for (int i = 1; i < dateTemp.length; i++) {
            if (dateTemp[i].length() == 1) {
                dateBuilder.append("0");
            }
            dateBuilder.append(dateTemp[i]);
        }

        int date = Integer.valueOf(dateBuilder.toString());
        if (date < 20110101 || date > 20161231) {
            return;
        }

        StringBuilder brandBuilder = new StringBuilder();
        for (int i = 0; i < temp.length - 4; i++) {
            if (i != 0) {
                brandBuilder.append(" ");
            }
            brandBuilder.append(temp[i]);
        }

        Integer brandOrder = brandService.getOrder(brandBuilder.toString());
        if (brandOrder == null) {
            return;
        }

        amountCounter.addAmount(brandOrder, Integer.valueOf(temp[temp.length - 2]));
    }


    public Map<Integer, BigDecimal> getRecordAmountMap() {
        return amountCounter.getRecordAmountMap();
    }
}