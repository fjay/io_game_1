package org.io.competition.stat.game.one;

import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import org.io.competition.stat.TaskService;
import org.io.competition.stat.game.BrandService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jay Wu
 */
public class Task1NewService implements TaskService {

    private BrandService brandService;
    private Record1Service record1Service;

    public Task1NewService(BrandService brandService) {
        this.brandService = brandService;
        this.record1Service = new Record1Service(brandService);
    }

    @Override
    public List<String> run(String recordPath) throws Exception {
        BoundedPriorityQueue<Map.Entry<Integer, BigDecimal>> result = record1Service.sort(recordPath);
        List<String> brandNames = new ArrayList<>();
        for (Map.Entry<Integer, BigDecimal> result1 : result) {
            brandNames.add(brandService.getName(result1.getKey()));
        }
        return brandNames;
    }
}