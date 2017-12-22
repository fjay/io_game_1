package com.vipsfin.competition.stat;

import com.vipsfin.competition.stat.game.BrandService;
import com.vipsfin.competition.stat.game.two.Record2Service;
import com.vipsfin.competition.stat.game.two.Task2Service;
import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.io.IoUtil;
import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.util.CharsetUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class RecordServiceTest {

    private BrandService brandService = new BrandService();
    private Record2Service recordService = new Record2Service(brandService);

    @Test
    public void split() {
        System.out.println(recordService.split(TestUtil.RECORD_FILE_PATH, 10000, 30));
    }

    @Test
    public void run() throws Exception {
        brandService.load(TestUtil.BRAND_FILE_PATH);

        long a = System.currentTimeMillis();

        List<String> result = new Task2Service(
                new AppConfig()
                        .setSplitFileCount(1)
                        .setWriterBufferLength(4000)
                , brandService)
                .run(TestUtil.RECORD_FILE_PATH + ".diff");
        System.out.println(result);

        long b = System.currentTimeMillis();
        System.out.println(b - a);
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
}