package com.labrox.cron;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.labrox.common", "com.labrox.catalog", "com.labrox.diagnostic", "com.labrox.cron"})
@EntityScan(basePackages = {"com.labrox.common", "com.labrox.catalog.domain", "com.labrox.diagnostic.domain", "com.labrox.cron.domain"})
@EnableJpaRepositories(basePackages = {"com.labrox.catalog.repository", "com.labrox.diagnostic.repository", "com.labrox.cron.repository"})
public class CronOrchestratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CronOrchestratorApplication.class, args);
    }
}
