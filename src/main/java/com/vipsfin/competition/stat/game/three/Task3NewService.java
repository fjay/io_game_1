package com.vipsfin.competition.stat.game.three;

import com.vipsfin.competition.stat.AppConfig;
import com.vipsfin.competition.stat.game.BrandService;
import com.vipsfin.competition.stat.game.two.Task2Service;

/**
 * @author Jay Wu
 */
public class Task3NewService extends Task2Service {

    public Task3NewService(AppConfig appConfig, BrandService brandService) {
        super(appConfig, brandService);

        this.recordService = new Record3Service(brandService);
    }
}