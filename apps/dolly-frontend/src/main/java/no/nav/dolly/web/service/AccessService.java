package no.nav.dolly.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class AccessService {
    private static final List<String> SESSION_PROFILE = List.of("local", "idporten");
    private final TokenExchange tokenExchange;
    private final Environment environment;

    private final no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange sessionTokenExchange;

    public Mono<String> getAccessToken(ServerProperties serverProperties, ServerWebExchange exchange) {

        return Arrays.stream(environment.getActiveProfiles()).anyMatch(SESSION_PROFILE::contains)
                ? sessionTokenExchange
                .exchange(serverProperties, exchange)
                .map(AccessToken::getTokenValue)
                : tokenExchange
                .exchange(serverProperties)
                .map(AccessToken::getTokenValue);

    }
}
