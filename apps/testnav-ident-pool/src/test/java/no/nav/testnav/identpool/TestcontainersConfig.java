package no.nav.testnav.identpool;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

//    @Testcontainers
//    @TestConfiguration
    public class TestcontainersConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Container
        private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.9")
                .withDatabaseName("test")
                .withUsername("test")
                .withPassword("test");

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                    "spring.r2dbc.url=" + postgres.getJdbcUrl().replace("jdbc","r2dbc"),
                    "spring.r2dbc.username=" + postgres.getUsername(),
                    "spring.r2dbc.password=" + postgres.getPassword()
            ).applyTo(applicationContext);
        }
    }