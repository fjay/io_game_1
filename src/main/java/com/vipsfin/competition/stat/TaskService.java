package com.vipsfin.competition.stat;

import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Jay Wu
 */
public class TaskService {

    public static List<Result2> run(String brandPath, String recordPath, int splitFileSize) {
        List<File> brandFiles = BrandService.split(brandPath, splitFileSize);
        List<File> recordFiles = RecordService.split(recordPath, splitFileSize);

        BoundedPriorityQueue<Result2> resultQueue = RecordService.newQueue();

        for (int i = 0; i < brandFiles.size(); i++) {
            File brandFile = brandFiles.get(i);
            Trie<BigDecimal> brandTrie = null;//BrandService.load(brandFile.getAbsolutePath());
            BoundedPriorityQueue<Result2> tempQueue = RecordService.sort(recordFiles.get(i).getAbsolutePath());

            for (Result2 result2 : tempQueue.toList()) {
                resultQueue.offer(result2);
            }
        }

        return resultQueue.toList();
    }
}