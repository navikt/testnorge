package no.nav.dolly.proxy.altinn3;

import no.nav.dolly.libs.texas.Texas;
import no.nav.dolly.libs.texas.TexasConsumer;
import no.nav.dolly.libs.texas.TexasToken;
import no.nav.dolly.proxy.brregstub.BrregStubRouteBuilder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static no.nav.dolly.proxy.altinn3.Altinn3RouteBuilder.ALTINN3_CONSUMER;
import static no.nav.dolly.proxy.brregstub.BrregStubRouteBuilder.BRREGSTUB_CONSUMER;
import static org.mockito.Mockito.when;

/**
 * Test config to provide a mocked Texas bean before the RouteLocatorBuilder runs.
 */
@TestConfiguration
@EnableWebFluxSecurity
class MockTexasConfig {

    public static final String MOCKED_TOKEN_VALUE = "mocked-token-value";

    @Bean
    @Primary
    public Texas texas(@Value("${wiremock.server.port}") String wiremockPort) {

        var mockTexas = Mockito.mock(Texas.class);

        when(mockTexas.get(ALTINN3_CONSUMER))
                .thenReturn(Mono.just(new TexasToken(MOCKED_TOKEN_VALUE, "3600", "test-token")));

        var altinn3MockConsumer = new TexasConsumer();
        altinn3MockConsumer.setName(ALTINN3_CONSUMER);
        altinn3MockConsumer.setUrl("http://localhost:" + wiremockPort);
        when(mockTexas.consumer(ALTINN3_CONSUMER))
                .thenReturn(Optional.of(altinn3MockConsumer));

        var brregMockConsumer = new TexasConsumer();
        brregMockConsumer.setName(BRREGSTUB_CONSUMER);
        brregMockConsumer.setUrl("http://localhost:" + wiremockPort);
        when(mockTexas.consumer(BRREGSTUB_CONSUMER))
                .thenReturn(Optional.of(brregMockConsumer));

        return mockTexas;

    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
                .build();
    }

}
