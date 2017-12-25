package org.io.competition.stat.game.two;

import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;

import java.io.File;
import java.util.concurrent.Callable;

public class RecordSortTask implements Callable<BoundedPriorityQueue<Result2>> {

    private Record2Service recordService;
    private File recordFile;

    public RecordSortTask(Record2Service recordService, File recordFile) {
        this.recordFile =recordFile;
        this.recordService = recordService;
    }

    @Override
    public BoundedPriorityQueue<Result2> call() {
        return this.recordService.sort(this.recordFile.getAbsolutePath());
    }
}