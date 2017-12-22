package org.io.competition.stat;

import org.io.competition.stat.game.BrandService;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Jay Wu
 */
public class BrandServiceTest {

    private BrandService brandService = new BrandService();

    @Test
    public void load() throws IOException {
        try {
            brandService.load(TestUtil.BRAND_FILE_PATH);
            System.out.println(brandService.getSize());
        } finally {
            brandService.close();
        }
    }
}