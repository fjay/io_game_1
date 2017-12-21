package com.vipsfin.competition.stat;

import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import com.xiaoleilu.hutool.util.ThreadUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Jay Wu
 */
public class TaskService {

    private final Log log = LogFactory.get();

    private ExecutorService pool = ThreadUtil.newExecutor(3);

    private BrandService brandService;
    private RecordService recordService;

    public TaskService(BrandService brandService, RecordService recordService) {
        this.recordService = recordService;
        this.brandService = brandService;
    }

    public List<Result2> run(String recordPath, int splitFileSize) throws Exception {
        List<File> files = recordService.split(recordPath, splitFileSize);

        brandService.clear();

        BoundedPriorityQueue<Result2> resultQueue = recordService.newQueue();

        for (File recordFile : files) {
            pool.execute(() -> {
                BoundedPriorityQueue<Result2> tempQueue = recordService.sort(recordFile.getAbsolutePath());

                for (Result2 result2 : tempQueue.toList()) {
                    resultQueue.offer(result2);
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.HOURS);
        return resultQueue.toList();
    }
}