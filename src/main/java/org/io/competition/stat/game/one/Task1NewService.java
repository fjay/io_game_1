package org.io.competition.stat.game.one;

import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import org.io.competition.stat.TaskService;
import org.io.competition.stat.game.BrandService;

import java.util.ArrayList;
import java.util.List;

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
        BoundedPriorityQueue<Result1> result = record1Service.sort(recordPath);
        List<String> brandNames = new ArrayList<>();
        for (Result1 result1 : result) {
            brandNames.add(brandService.getName(result1.getOrder()));
        }
        return brandNames;
    }
}