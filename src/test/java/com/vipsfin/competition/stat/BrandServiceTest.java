package com.vipsfin.competition.stat;

import org.junit.Test;

import java.io.IOException;

public class BrandServiceTest {

    private BrandService brandService = new BrandService();

    @Test
    public void load() throws IOException {
        try {
            brandService.load(TestUtil.BRAND_FILE_PATH);
            System.out.println(brandService.getOrder("A/E"));
        } finally {
            brandService.close();
        }
    }
}