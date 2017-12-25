package org.io.competition.stat.util;

import com.xiaoleilu.hutool.io.IoUtil;
import com.xiaoleilu.hutool.io.LineHandler;
import org.team4u.kit.core.lang.LongTimeThread;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

/**
 * @author Jay Wu
 */
public class AsynFileReader extends LongTimeThread {

    private TransferQueue<String> queue = new LinkedTransferQueue<>();

    private LineHandler lineHandler;

    public AsynFileReader(LineHandler lineHandler) {
        this.lineHandler = lineHandler;
    }

    public static AsynFileReader[] start(int size, LineHandler lineHandler) {
        AsynFileReader[] asynFileReaders = new AsynFileReader[size];
        for (int i = 0; i < size; i++) {
            asynFileReaders[i] = new AsynFileReader(lineHandler);
            asynFileReaders[i].start();
        }
        return asynFileReaders;
    }

    public static void close(AsynFileReader[] asynFileReaders) {
        for (AsynFileReader asynFileReader : asynFileReaders) {
            IoUtil.close(asynFileReader);
            asynFileReader.awaitTermination();
        }
    }

    public void offer(String line) {
        try {
            queue.transfer(line);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!isClosed() || !queue.isEmpty()) {
            try {
                onRun();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onRun() {
        try {
            String line = queue.poll(100, TimeUnit.MILLISECONDS);
            if (line == null) {
                return;
            }

            lineHandler.handle(line);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}