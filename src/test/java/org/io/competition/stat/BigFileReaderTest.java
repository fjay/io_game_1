package org.io.competition.stat;

import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.util.CharsetUtil;
import org.io.competition.stat.util.BigFileReader;
import org.io.competition.stat.util.Stopwatch;
import org.junit.Test;
import org.team4u.kit.core.lang.LongTimeThread;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jay Wu
 */
public class BigFileReaderTest {

    @Test
    public void read() {
        AtomicInteger counter = new AtomicInteger();
        BigFileReader.Builder builder = new BigFileReader.Builder(FileUtil.file(TestUtil.RECORD_FILE_PATH).getAbsolutePath(), line -> {
            counter.incrementAndGet();
        });

        builder.withTreahdSize(3)
                .withCharset(CharsetUtil.UTF_8)
                .withBufferSize(1024 * 1024);
        BigFileReader bigFileReader = builder.build();
        bigFileReader.start();
        bigFileReader.waitForDone();
    }

    @Test
    public void read2() {
        Stopwatch stopwatch = Stopwatch.create().start();
        AtomicInteger counter = new AtomicInteger();
        X[] x = new X[]{new X(), new X()};
        for (X x1 : x) {
            x1.start();
        }

        FileUtil.readUtf8Lines(FileUtil.file(TestUtil.BRAND_FILE_PATH), (LineHandler) line -> {
            x[counter.incrementAndGet() % 2].offer(line);
        });

        for (X x1 : x) {
            x1.close();
            x1.awaitTermination();
        }

        stopwatch.stop();
        System.out.println(stopwatch.duration() + "," + counter.get());

    }

    public static class X extends LongTimeThread {
        private TransferQueue<String> queue = new LinkedTransferQueue<String>();

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
            queue.poll();
        }
    }
}