package no.nav.dolly.web.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;


@Service
@RequiredArgsConstructor
public class AccessService {
    private final TokenExchange tokenExchange;

    private final Environment environment;

    private final no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange localClientExchange;

    public Mono<String> getAccessToken(ServerProperties serverProperties, ServerWebExchange exchange) {
        return Arrays.asList(environment.getActiveProfiles()).contains("local")
                ? localClientExchange
                .exchange(serverProperties, exchange)
                .map(AccessToken::getTokenValue)
                : tokenExchange
                .exchange(serverProperties)
                .map(AccessToken::getTokenValue);
    }
}
