package com.vipsfin.competition.stat;

import com.xiaoleilu.hutool.collection.CollUtil;
import com.xiaoleilu.hutool.lang.Dict;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Jay Wu
 */
@RestController
public class StartController {

    @RequestMapping(value = "/start")
    public Map<String, Object> start(String sign, String dataDisk, Integer dataCheckequence) {
        return Dict.create().set("sign", sign).set("taskResult", CollUtil.join(new ArrayList<>(), ","));
    }
}