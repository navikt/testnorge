package no.nav.testnav.identpool.providers.v1;

import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@DollySpringBootTest
class ApplicationContextTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @DynamicPropertySource
    static void dynamicPropertyRegistry(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", ApplicationContextTest::getR2dbcUrl);
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);
    }

    private static String getR2dbcUrl() {
        return postgreSQLContainer.getJdbcUrl().replace("jdbc", "r2dbc");
    }

    @Order(1)
    @Test
    @SuppressWarnings("java:S2699")
    void contextLoads() {
        // This test will pass if the application context loads successfully.
        // No assertions are needed here, as the test will fail if there are any issues with the context.
    }
}