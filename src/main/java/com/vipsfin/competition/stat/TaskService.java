package com.vipsfin.competition.stat;

import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Jay Wu
 */
public class TaskService {

    public static List<Result2> run(String brandPath, String recordPath, int splitFileSize) throws IOException {
        BrandService.load(brandPath);

        BoundedPriorityQueue<Result2> resultQueue = RecordService.newQueue();

        for (File recordFile : RecordService.split(recordPath, splitFileSize)) {
            BoundedPriorityQueue<Result2> tempQueue = RecordService.sort(recordFile.getAbsolutePath());

            for (Result2 result2 : tempQueue.toList()) {
                resultQueue.offer(result2);
            }
        }

        return resultQueue.toList();
    }
}