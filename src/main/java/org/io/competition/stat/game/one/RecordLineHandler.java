package org.io.competition.stat.game.one;

import com.xiaoleilu.hutool.io.LineHandler;
import org.io.competition.stat.game.BrandService;
import org.io.competition.stat.util.AmountCounter;

import java.math.BigInteger;
import java.util.ArrayList;
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

    public static void main(String[] args) {
        String line = "bjjXzeYDNeMDOOWJ PrOiCyVxjMfxlrygDg VIP_HZ 374291948 2015-2-2";
        ArrayList<Integer> temp = new ArrayList<>(10);
        for (int j = 0; j < line.length(); j++) {
            if (line.charAt(j) == ' ') {
                temp.add(j);
            }
        }
        System.out.println(line.substring(temp.get(temp.size() - 1) + 1));
        System.out.println(line.substring(temp.get(temp.size() - 1) + 1).length());
        System.out.println(line.substring(temp.get(temp.size() - 2) + 1, temp.get(temp.size() - 1)));
        System.out.println(line.substring(temp.get(temp.size() - 2) + 1, temp.get(temp.size() - 1)).length());
        System.out.println(line.substring(temp.get(temp.size() - 3) + 1, temp.get(temp.size() - 2)));
        System.out.println(line.substring(temp.get(temp.size() - 3) + 1, temp.get(temp.size() - 2)).length());
        System.out.println(line.substring(0, temp.get(temp.size() - 4)));
    }

    @Override
    public void handle(String line) {
        counter.incrementAndGet();
        ArrayList<Integer> temp = new ArrayList<>(10);
        for (int j = 0; j < line.length(); j++) {
            if (line.charAt(j) == ' ') {
                temp.add(j);
            }
        }


        if (!line.substring(temp.get(temp.size() - 3) + 1, temp.get(temp.size() - 2)).equals("VIP_NH")) {
            return;
        }

        StringBuilder dateBuilder = new StringBuilder(8);
        String dateStr = line.substring(temp.get(temp.size() - 1) + 1);
        for (int k = 0; k < dateStr.length(); k++) {
            char a = dateStr.charAt(k);
            if (a != '-') {
                dateBuilder.append(a);
            } else {
                if (k == 4 && dateStr.charAt(6) == '-') {
                    dateBuilder.append('0');
                } else if (k + 2 == dateStr.length()) {
                    dateBuilder.append('0');
                }
            }
        }

        int date = Integer.valueOf(dateBuilder.toString());
        if (date < 20110101 || date > 20161231) {
            return;
        }


        Integer brandOrder = brandService.getOrder(line.substring(0, temp.get(temp.size() - 4)));
        if (brandOrder == null) {
            return;
        }

        Integer amount = Integer.valueOf(line.substring(temp.get(temp.size() - 2) + 1, temp.get(temp.size() - 1)));
        amountCounter.addAmount(brandOrder, amount);
    }

    public Map<Integer, BigInteger> getRecordAmountMap() {
        return amountCounter.getRecordAmountMap();
    }
}