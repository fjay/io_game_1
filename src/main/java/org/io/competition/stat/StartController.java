package org.io.competition.stat;

import org.io.competition.stat.game.BrandService;
import org.io.competition.stat.game.one.Task1Service;
import org.io.competition.stat.game.three.Task3NewService;
import org.io.competition.stat.game.two.Task2Service;
import com.xiaoleilu.hutool.collection.CollUtil;
import com.xiaoleilu.hutool.lang.Dict;
import org.springframework.beans.factory.InitializingBean;
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
public class StartController implements InitializingBean {

    private BrandService brandService = new BrandService();

    @Autowired
    private AppConfig appConfig;

    private Map<Integer, TaskService> taskServiceMap = new HashMap<>();

    @RequestMapping(value = "/loadBrand")
    public void loadBrand() throws IOException {
        brandService.load(appConfig.getBrandPath());
    }

    @RequestMapping(value = "/match")
    public Map<String, Object> start(String sign, String dataDisk, Integer dataCheckequence) throws Exception {
        List<String> result = taskServiceMap.get(dataCheckequence).run(dataDisk);
        return Dict.create().set("sign", sign).set("taskResult", CollUtil.join(result, ","));
    }

    @Override
    public void afterPropertiesSet() {
        taskServiceMap.put(1, new Task1Service(brandService));
        taskServiceMap.put(2, new Task2Service(appConfig, brandService));
        taskServiceMap.put(3, new Task3NewService(appConfig, brandService));
    }
}