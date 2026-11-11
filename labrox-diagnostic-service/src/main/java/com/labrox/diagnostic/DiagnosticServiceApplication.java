package com.labrox.diagnostic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.labrox.common", "com.labrox.catalog", "com.labrox.diagnostic"})
public class DiagnosticServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiagnosticServiceApplication.class, args);
    }
}
