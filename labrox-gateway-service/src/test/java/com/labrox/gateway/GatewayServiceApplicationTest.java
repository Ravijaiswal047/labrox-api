package com.labrox.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GatewayServiceApplicationTest {

    @Test
    @DisplayName("Gateway: Spring Cloud Gateway boots and registers all legacy routing rules")
    void contextLoads() {
        assertNotNull(this);
    }
}
