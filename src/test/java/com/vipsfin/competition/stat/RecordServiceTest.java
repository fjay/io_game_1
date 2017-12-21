package com.vipsfin.competition.stat;

import org.junit.Test;

import java.util.List;

public class RecordServiceTest {

    private RecordService recordService = new RecordService();
    private BrandService brandService = new BrandService();

    @Test
    public void split() {
        System.out.println(recordService.split(TestUtil.RECORD_FILE_PATH, 100));
    }

    @Test
    public void run() throws Exception {
        brandService.load(TestUtil.BRAND_FILE_PATH);

        long a = System.currentTimeMillis();
        List<Result2> result = new TaskService(brandService, recordService)
                .run(TestUtil.RECORD_FILE_PATH, 100);
        System.out.println(result);

        long b = System.currentTimeMillis();
        System.out.println(b - a);
    }
}