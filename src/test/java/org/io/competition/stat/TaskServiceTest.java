package org.io.competition.stat;

import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.io.IoUtil;
import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.util.CharsetUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import org.io.competition.stat.game.BrandService;
import org.io.competition.stat.game.one.Task1NewService;
import org.io.competition.stat.game.three.Task3NewService;
import org.io.competition.stat.game.two.Record2Service;
import org.io.competition.stat.game.two.Task2Service;
import org.io.competition.stat.util.Stopwatch;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

/**
 * @author Jay Wu
 */
public class TaskServiceTest {

    private BrandService brandService = new BrandService();
    private Record2Service record2Service = new Record2Service(brandService);

    @Test
    public void split2() {
        System.out.println(record2Service.split(TestUtil.RECORD_FILE_PATH, 10000, 30));
    }

    @Test
    public void run1() throws Exception {
        run(new Task1NewService(brandService));
    }


    @Test
    public void run2() throws Exception {
        run(new Task2Service(new AppConfig()
                .setSplitFileCount(16)
                .setWriterBufferLength(2048)
                , brandService));
    }

    @Test
    public void run3() throws Exception {
        run(new Task3NewService(new AppConfig()
                .setSplitFileCount(16)
                .setWriterBufferLength(2048)
                , brandService));
    }

    private void run(TaskService task2Service) throws Exception {
        brandService.load(TestUtil.BRAND_FILE_PATH);

        Stopwatch stopwatch = Stopwatch.create().start();

        List<String> result = task2Service.run(TestUtil.RECORD_FILE_PATH);
        System.out.println(result);

        stopwatch.stop();
        System.out.println(stopwatch.duration());
    }

    @Test
    public void filter() {
        String expectString = "lyzrjA cZLZzfvBNtR";

        String myString = "SVfZdBEdnmdrmfvRBBdrH";

        HashSet<String> x = new HashSet<String>();
        x.addAll(StrUtil.splitTrim(expectString, ","));
        x.addAll(StrUtil.splitTrim(myString, ","));

        BufferedWriter w = FileUtil.getWriter(FileUtil.file(TestUtil.RECORD_FILE_PATH + ".diff"), CharsetUtil.UTF_8, true);
        FileUtil.readUtf8Lines(FileUtil.file(TestUtil.RECORD_FILE_PATH + ".diff2"), (LineHandler) (String line) -> {
            String[] temp = line.split(" ");
            int pos = temp.length;
            String date = temp[--pos];
            Integer amount = Integer.valueOf(temp[--pos]);
            String location = temp[--pos];
            String desc = temp[--pos];

            StringBuilder brand = new StringBuilder();
            for (int i = 0; i < pos; i++) {
                brand.append(temp[i]);
                if (i < pos - 1) {
                    brand.append(" ");
                }
            }

            String brandKey = brand.toString();
            if (x.contains(brandKey)) {
                try {
                    w.append(brand + "," + date + "," + amount).append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        IoUtil.close(w);
    }

    @Test
    public void x() {
        String x = Integer.toString(20171010, 36);
        System.out.println(x);
        int y = Integer.valueOf(x, 36);
        System.out.println(y);
    }
}