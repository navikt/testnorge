package no.nav.dolly.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestDatabaseConfig implements BeforeAllCallback {

    public static final PostgreSQLContainer<?> POSTGRES =
        new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @Override
    public void beforeAll(ExtensionContext context) {
        POSTGRES.start();
        System.setProperty("spring.r2dbc.url", "r2dbc:postgresql://localhost:" + POSTGRES.getMappedPort(5432) + "/test");
        System.setProperty("spring.r2dbc.username", "test");
        System.setProperty("spring.r2dbc.password", "test");
        System.setProperty("spring.flyway.url", POSTGRES.getJdbcUrl());
        System.setProperty("spring.flyway.user", "test");
        System.setProperty("spring.flyway.password", "test");
    }
}