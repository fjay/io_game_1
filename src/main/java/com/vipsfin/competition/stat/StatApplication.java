package com.vipsfin.competition.stat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AppConfig.class)
@SpringBootApplication
public class StatApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatApplication.class, args);
    }
}
