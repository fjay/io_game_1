package org.io.competition.stat.game.three;

import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import org.io.competition.stat.game.BrandService;
import org.io.competition.stat.game.two.Record2Service;
import org.io.competition.stat.game.two.Result2;
import org.io.competition.stat.game.two.SimpleRecordLineHandler;

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
    protected SimpleRecordLineHandler newRecordLineHandler(AtomicLong counter, BoundedPriorityQueue<Result2> queue) {
        return new SimpleRecordLineHandler(counter, queue) {
            private final static int MAX_COUNT_KEY = 0;
            private Map<Integer, Map<Integer, Integer>> recordDateMap = new HashMap<>();

            @Override
            protected int count(Integer brandOrder, Integer date) {
                Map<Integer, Integer> uniqueDates = recordDateMap.computeIfAbsent(brandOrder, k -> new HashMap<>());
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