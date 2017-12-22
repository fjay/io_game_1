package org.io.competition.stat.game.three;

import org.io.competition.stat.AppConfig;
import org.io.competition.stat.game.BrandService;
import org.io.competition.stat.game.two.Task2Service;

/**
 * @author Jay Wu
 */
public class Task3NewService extends Task2Service {

    public Task3NewService(AppConfig appConfig, BrandService brandService) {
        super(appConfig, brandService);

        this.recordService = new Record3Service(brandService);
    }
}