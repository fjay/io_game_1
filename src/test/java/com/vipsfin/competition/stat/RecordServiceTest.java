package com.vipsfin.competition.stat;

import org.junit.Test;

import java.io.IOException;

public class RecordServiceTest {

    @Test
    public void split() throws IOException {
        BrandService.load("/Users/fjay/Documents/work/vip/code/game/io/test/brand_name.txt");
        System.out.println(RecordService.split("/Users/fjay/Documents/work/vip/code/game/io/test/matchs.txt", 50));
    }

    @Test
    public void x() {
        long a = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            RecordService.sort("/Users/fjay/Documents/work/vip/code/game/io/test/r/" + i + ".txt");
        }
        long b = System.currentTimeMillis();
        System.out.println(b - a);
    }
}