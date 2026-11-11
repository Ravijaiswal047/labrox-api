package com.labrox.approval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.labrox.common", "com.labrox.catalog", "com.labrox.diagnostic", "com.labrox.approval"})
@EntityScan(basePackages = {"com.labrox.common", "com.labrox.catalog.domain", "com.labrox.diagnostic.domain", "com.labrox.approval.domain"})
@EnableJpaRepositories(basePackages = {"com.labrox.catalog.repository", "com.labrox.diagnostic.repository", "com.labrox.approval.repository"})
public class ApprovalServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApprovalServiceApplication.class, args);
    }
}
