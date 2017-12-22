package org.io.competition.stat.game.two;

import org.io.competition.stat.AppConfig;
import org.io.competition.stat.TaskService;
import org.io.competition.stat.game.BrandService;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jay Wu
 */
public class Task2Service implements TaskService {

    private final Log log = LogFactory.get();

    private BrandService brandService;
    protected Record2Service recordService;

    private AppConfig appConfig;

    public Task2Service(AppConfig appConfig, BrandService brandService) {
        this.appConfig = appConfig;
        this.brandService = brandService;
        this.recordService = new Record2Service(brandService);
    }

    public List<String> run(String recordPath) {
        List<File> files = recordService.split(
                recordPath,
                appConfig.getWriterBufferLength(),
                appConfig.getSplitFileCount()
        );

        BoundedPriorityQueue<Result2> resultQueue = recordService.newQueue();

        for (File recordFile : files) {
            BoundedPriorityQueue<Result2> tempQueue = recordService.sort(recordFile.getAbsolutePath());

            for (Result2 result2 : tempQueue.toList()) {
                resultQueue.offer(result2);
            }
        }

        ArrayList<Result2> result2s = resultQueue.toList();
        log.info(result2s.toString());

        List<String> brandNames = new ArrayList<>();
        for (Result2 result2 : result2s) {
            brandNames.add(brandService.getName(result2.getOrder()));
        }
        return brandNames;
    }
}