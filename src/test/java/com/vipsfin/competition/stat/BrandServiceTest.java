package com.vipsfin.competition.stat;

import org.junit.Test;

import java.io.IOException;

public class BrandServiceTest {

    @Test
    public void load() throws IOException {
        try {
            BrandService.load(TestUtil.BRAND_FILE_PATH);
            System.out.println(BrandService.getOrder("A/E"));
        } finally {
            BrandService.close();
        }
    }
}