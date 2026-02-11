package no.nav.dolly.proxy.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureNavTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenCacheUnitTest {

    private AuthenticationFilterService authenticationFilterService;

    @Mock
    private AzureNavTokenService navTokenService;

    @Mock
    private AzureTrygdeetatenTokenService trygdeetatenTokenService;

    @Mock
    private FakedingsService fakedingsService;

    @Mock
    private GatewayFilterChain chain;

    @BeforeEach
    void setup() {
        authenticationFilterService = new AuthenticationFilterService(
                navTokenService,
                trygdeetatenTokenService,
                fakedingsService,
                new TokenCache(0)
        );
    }

    @Test
    void testTokenCacheNotTriggeringTwice() {

        when(navTokenService
                .exchange(any()))
                .thenReturn(Mono.just(new AccessToken(createJWT())));
        when(chain.filter(any()))
                .thenReturn(Mono.empty());

        var filter = authenticationFilterService.getNavAuthenticationFilter(
                "dev-gcp",
                "dolly",
                "some-app",
                "http://some-app"
        );

        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());

        // First call.
        filter
                .filter(exchange, chain)
                .block();

        // Second call.
        filter
                .filter(exchange, chain)
                .block();

        verify(navTokenService, times(1))
                .exchange(any()); // Only one call to underlying .exchange(...).
        verify(chain, times(2))
                .filter(any());

    }

    private static String createJWT() {
        return JWT
                .create()
                .withExpiresAt(Date.from(Instant.now().plusSeconds(3600)))
                .sign(Algorithm.none());
    }

}
