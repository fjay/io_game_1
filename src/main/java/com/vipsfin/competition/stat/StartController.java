package com.vipsfin.competition.stat;

import com.vipsfin.competition.stat.game.BrandService;
import com.vipsfin.competition.stat.game.two.Task2Service;
import com.xiaoleilu.hutool.collection.CollUtil;
import com.xiaoleilu.hutool.lang.Dict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jay Wu
 */
@RestController
public class StartController {

    private BrandService brandService = new BrandService();

    @Autowired
    private AppConfig appConfig;

    private Map<Integer, TaskService> taskServiceMap = new HashMap<Integer, TaskService>() {{
        put(1, null);
        put(2, new Task2Service(brandService));
        put(3, null);
    }};

    @RequestMapping(value = "/loadBrand")
    public void loadBrand() throws IOException {
        brandService.load(appConfig.getBranchPath());
    }

    @RequestMapping(value = "/start")
    public Map<String, Object> start(String sign, String dataDisk, Integer dataCheckequence) throws Exception {
        List<String> result = taskServiceMap.get(dataCheckequence).run(dataDisk, appConfig.getSplitFileCount());
        return Dict.create().set("sign", sign).set("taskResult", CollUtil.join(result, ","));
    }
}