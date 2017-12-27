package org.io.competition.stat.game.one;

import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import org.io.competition.stat.game.BrandService;
import org.io.competition.stat.util.Stopwatch;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Jay Wu
 */
public class Record1Service {
    private final Log log = LogFactory.get();

    private BrandService brandService;

    public Record1Service(BrandService brandService) {
        this.brandService = brandService;
    }

    public BoundedPriorityQueue<Map.Entry<Integer, BigInteger>> sort(String path) {
        log.info("Loading {}", path);

        Stopwatch stopwatch = Stopwatch.create().start();
        AtomicLong counter = new AtomicLong();
        BoundedPriorityQueue<Map.Entry<Integer, BigInteger>> queue = newQueue();

        RecordLineHandler lineHandler = newRecordLineHandler(counter);
        FileUtil.readUtf8Lines(FileUtil.file(path), lineHandler);

        for (Map.Entry<Integer, BigInteger> entry : lineHandler.getRecordAmountMap().entrySet()) {
            queue.offer(entry);
        }

        stopwatch.stop();
        log.info("Loaded {} size:{}, duration:{}", path, counter.get(), stopwatch.duration());
        return queue;
    }

    protected RecordLineHandler newRecordLineHandler(AtomicLong counter) {
        return new RecordLineHandler(brandService, counter);
    }

    public BoundedPriorityQueue<Map.Entry<Integer, BigInteger>> newQueue() {
        return new BoundedPriorityQueue<>(
                40,
                (o1, o2) -> {
                    int result = -o1.getValue().compareTo(o2.getValue());

                    if (result == 0) {
                        return o1.getKey().compareTo(o2.getKey());
                    }

                    return result;
                }
        );
    }
}