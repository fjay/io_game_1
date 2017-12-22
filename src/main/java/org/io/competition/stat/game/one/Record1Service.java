package org.io.competition.stat.game.one;

import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import org.io.competition.stat.game.BrandService;
import org.io.competition.stat.util.Stopwatch;

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

    public BoundedPriorityQueue<Result1> sort(String path) {
        log.info("Loading {}", path);

        Stopwatch stopwatch = Stopwatch.create().start();
        AtomicLong counter = new AtomicLong();
        BoundedPriorityQueue<Result1> queue = newQueue();

        FileUtil.readUtf8Lines(FileUtil.file(path), newRecordLineHandler(counter, queue));

        stopwatch.stop();
        log.info("Loaded {} size:{}, duration:{}", path, counter.get(), stopwatch.duration());
        return queue;
    }

    protected LineHandler newRecordLineHandler(AtomicLong counter, BoundedPriorityQueue<Result1> queue) {
        return new RecordLineHandler(brandService, counter, queue);
    }

    public BoundedPriorityQueue<Result1> newQueue() {
        return new BoundedPriorityQueue<>(
                40,
                (o1, o2) -> {
                    int result = -o1.getAmount().compareTo(o2.getAmount());

                    if (result == 0) {
                        return o1.getOrder().compareTo(o2.getOrder());
                    }

                    return result;
                }
        );
    }
}