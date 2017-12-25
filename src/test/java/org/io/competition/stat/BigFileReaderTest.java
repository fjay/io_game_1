package org.io.competition.stat;

import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.util.CharsetUtil;
import org.io.competition.stat.util.BigFileReader;
import org.io.competition.stat.util.Stopwatch;
import org.junit.Test;

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

        FileUtil.readUtf8Lines(FileUtil.file(TestUtil.RECORD_FILE_PATH), (LineHandler) line -> {
            counter.incrementAndGet();
        });

        stopwatch.stop();
        System.out.println(stopwatch.duration() + "," + counter.get());
    }
}