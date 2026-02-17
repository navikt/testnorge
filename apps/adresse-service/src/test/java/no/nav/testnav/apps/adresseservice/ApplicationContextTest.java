package no.nav.testnav.apps.adresseservice;

import no.nav.dolly.libs.test.DollyApplicationContextTest;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatchers;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@DollySpringBootTest
class ApplicationContextTest extends DollyApplicationContextTest {

    @MockitoBean
    private OpenSearchClient openSearchClient;

    @MockitoBean
    private TokenExchange tokenExchange;

    @BeforeEach
    void setUp() {
        when(tokenExchange.exchange(ArgumentMatchers.any(ServerProperties.class)))
                .thenReturn(Mono.just(new AccessToken("test-token")));
    }
}