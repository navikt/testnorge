package no.nav.dolly.bestilling;

import no.nav.dolly.config.TestDatabaseConfig;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
@ExtendWith(TestDatabaseConfig.class)
public abstract class AbstractConsumerTest {

    @MockitoBean
    protected TokenExchange tokenExchange;

    @MockitoBean
    private AccessToken accessToken;

    @MockitoBean
    private ElasticsearchOperations elasticsearchOperations;

    @BeforeEach
    void setUp() {

        when(tokenExchange.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }
}
