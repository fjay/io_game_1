package org.io.competition.stat;

import com.xiaoleilu.hutool.io.FileUtil;
import it.unimi.dsi.bits.TransformationStrategies;
import it.unimi.dsi.fastutil.io.BinIO;
import it.unimi.dsi.sux4j.mph.GOV4Function;
import org.io.competition.stat.game.BrandService;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jay Wu
 */
public class BrandServiceTest {

    private BrandService brandService = new BrandService();

    @Test
    public void load() throws Exception {
        try {
            brandService.load(TestUtil.BRAND_FILE_PATH);
            System.out.println(brandService.getOrder("QJeqChLhbyagEVxmuBq"));
        } finally {
            brandService.close();
        }
    }

    @Test
    public void hash() throws Exception {
        int[] keys = new int[1000000];
        List<String> stringKeys = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            keys[i] = i;
            stringKeys.add(i + "a");
        }
        GOV4Function<CharSequence> mph = new GOV4Function.Builder<CharSequence>()
                .keys(stringKeys)
                .transform(TransformationStrategies.utf16())
                .signed(32)
                .build();

        System.out.println(mph.getLong("0a"));
        System.out.println(mph.getLong((stringKeys.size() - 1) + "a"));
        System.out.println(mph.getLong(stringKeys.size() + "a"));
        BinIO.storeObject(mph, FileUtil.file(TestUtil.BRAND_FILE_PATH).getAbsolutePath() + ".index");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void hash2() throws IOException, ClassNotFoundException {
        GOV4Function<CharSequence> mph = (GOV4Function<CharSequence>) BinIO.loadObject(FileUtil.file(TestUtil.BRAND_FILE_PATH).getAbsolutePath() + ".index");
        System.out.println(mph.getLong("0a"));
        System.out.println(mph.getLong("999999a"));
        System.out.println(mph.getLong("1000000a"));
    }
}