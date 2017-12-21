package com.vipsfin.competition.stat;

import org.junit.Test;

import java.io.IOException;

public class BrandServiceTest {

    @Test
    public void load() throws IOException {
        try {
            BrandService.load("/Users/fjay/Documents/work/vip/code/game/io/test/brand_name.txt");
            System.out.println(BrandService.getOrder("veZqNihZjBvuwRy"));
        } finally {
            BrandService.close();
        }
    }
}