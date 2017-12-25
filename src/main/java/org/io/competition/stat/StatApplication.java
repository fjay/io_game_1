package org.io.competition.stat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AppConfig.class)
@SpringBootApplication
public class StatApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(StatApplication.class);
        springApplication.addListeners(new ApplicationStartup());
        springApplication.run(args);
    }
}