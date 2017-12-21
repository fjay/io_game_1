package com.vipsfin.competition.stat;

import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import com.xiaoleilu.hutool.util.ThreadUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Jay Wu
 */
public class TaskService {

    private static ExecutorService pool = ThreadUtil.newExecutor(2);

    public static List<Result2> run(String brandPath, String recordPath, int splitFileSize) throws Exception {
        BrandService.load(brandPath);

        List<File> files = RecordService.split(recordPath, splitFileSize);

        BrandService.clear();

        BoundedPriorityQueue<Result2> resultQueue = RecordService.newQueue();

        for (File recordFile : files) {
            pool.execute(() -> {
                BoundedPriorityQueue<Result2> tempQueue = RecordService.sort(recordFile.getAbsolutePath());

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