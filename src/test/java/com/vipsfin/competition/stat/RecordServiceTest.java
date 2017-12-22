package com.vipsfin.competition.stat;

import com.vipsfin.competition.stat.game.BrandService;
import com.vipsfin.competition.stat.game.two.Record2Service;
import com.vipsfin.competition.stat.game.two.Task2Service;
import org.junit.Test;

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
                        .setSplitFileCount(30)
                        .setWriterBufferLength(50000)
                , brandService)
                .run(TestUtil.RECORD_FILE_PATH);
        System.out.println(result);

        long b = System.currentTimeMillis();
        System.out.println(b - a);
    }
}