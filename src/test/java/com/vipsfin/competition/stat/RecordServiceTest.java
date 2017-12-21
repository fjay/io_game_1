package com.vipsfin.competition.stat;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class RecordServiceTest {

    @Test
    public void split() throws IOException {
        BrandService.load(TestUtil.BRAND_FILE_PATH);
        System.out.println(RecordService.split(TestUtil.RECORD_FILE_PATH, 50));
    }

    @Test
    public void run() throws IOException {
        long a = System.currentTimeMillis();
        List<Result2> result = TaskService.run(TestUtil.BRAND_FILE_PATH, TestUtil.RECORD_FILE_PATH, 25);
        System.out.println(result);

        long b = System.currentTimeMillis();
        System.out.println(b - a);
    }
}