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
        System.out.println(RecordService.sort("/Users/fjay/Documents/work/vip/code/game/io/test/r/0.txt").toList());
    }
}