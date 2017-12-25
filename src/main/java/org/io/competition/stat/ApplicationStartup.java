package org.io.competition.stat;

import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import org.io.competition.stat.game.BrandService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

    private Log log = LogFactory.get();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        BrandService service = event.getApplicationContext().getBean(BrandService.class);
        AppConfig appConfig = event.getApplicationContext().getBean(AppConfig.class);
        try {
            service.load(appConfig.getBrandPath());
        } catch (Exception e) {
            log.error(e, e.getMessage());
        }
    }
}
