package com.vipsfin.competition.stat;

import org.junit.Test;

import java.util.List;

public class RecordServiceTest {

    @Test
    public void split() throws Exception {
        BrandService.load(TestUtil.BRAND_FILE_PATH);
        System.out.println(RecordService.split(TestUtil.RECORD_FILE_PATH, 100));
    }

    @Test
    public void run() throws Exception {
        long a = System.currentTimeMillis();
        List<Result2> result = TaskService.run(TestUtil.BRAND_FILE_PATH, TestUtil.RECORD_FILE_PATH, 100);
        System.out.println(result);

        long b = System.currentTimeMillis();
        System.out.println(b - a);
    }
}