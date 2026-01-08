package no.nav.dolly.bestilling;

import no.nav.dolly.config.TestDatabaseConfig;
import no.nav.dolly.config.TestOpenSearchConfig;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.testing.DollyWireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@DollySpringBootTest
@ExtendWith(DollyWireMockExtension.class)
@Import({TestDatabaseConfig.class, TestOpenSearchConfig.class})
public abstract class AbstractConsumerTest {

    @MockitoBean
    protected TokenExchange tokenExchange;
    @MockitoBean
    private AccessToken accessToken;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://localhost:" + TestDatabaseConfig.POSTGRES.getMappedPort(5432) + "/test");
        registry.add("spring.r2dbc.username", TestDatabaseConfig.POSTGRES::getUsername);
        registry.add("spring.r2dbc.password", TestDatabaseConfig.POSTGRES::getPassword);
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @BeforeEach
    void setUp() {

        when(tokenExchange.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }
}
