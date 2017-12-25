package org.io.competition.stat.game.two;

import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import org.io.competition.stat.AppConfig;
import org.io.competition.stat.TaskService;
import org.io.competition.stat.game.BrandService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Jay Wu
 */
public class Task2Service implements TaskService {

    private final Log log = LogFactory.get();

    private ExecutorService es = Executors.newFixedThreadPool(2);
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

        // 尝试并行处理
        List<Future<BoundedPriorityQueue<Result2>>> list = new ArrayList<>();
        for (File recordFile : files) {
            Future<BoundedPriorityQueue<Result2>> future = this.es.submit(new RecordSortTask(recordService, recordFile));
            list.add(future);
        }

        for (Future<BoundedPriorityQueue<Result2>> f : list) {
            BoundedPriorityQueue<Result2> tempQueue = null;
            try {
                tempQueue = f.get();
                for (Result2 result2 : tempQueue.toList()) {
                    resultQueue.offer(result2);
                }
            } catch (Exception e) {
                log.error(e, e.getMessage());
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