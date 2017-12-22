package com.vipsfin.competition.stat.game.three;

import com.vipsfin.competition.stat.game.BrandService;
import com.vipsfin.competition.stat.game.two.Record2Service;
import com.vipsfin.competition.stat.game.two.RecordLineHandler;
import com.vipsfin.competition.stat.game.two.Result2;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Jay Wu
 */
public class Record3Service extends Record2Service {

    public Record3Service(BrandService brandService) {
        super(brandService);
    }

    @Override
    protected RecordLineHandler newRecordLineHandler(AtomicLong counter, BoundedPriorityQueue<Result2> queue) {
        return new RecordLineHandler(counter, queue) {
            private final static String MAX_COUNT_KEY = "";
            private Map<Integer, Map<String, Integer>> recordDateMap = new HashMap<>();

            @Override
            protected int count(Integer brandOrder, String date) {
                Map<String, Integer> uniqueDates = recordDateMap.computeIfAbsent(brandOrder, k -> new HashMap<>());
                int count = uniqueDates.computeIfAbsent(date, k -> 0);
                uniqueDates.put(date, ++count);

                int maxCount = uniqueDates.computeIfAbsent(MAX_COUNT_KEY, k -> 0);
                if (maxCount < count) {
                    uniqueDates.put(MAX_COUNT_KEY, count);
                }

                return maxCount;
            }
        };
    }
}