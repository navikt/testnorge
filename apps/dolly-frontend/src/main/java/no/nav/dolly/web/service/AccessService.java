package no.nav.dolly.web.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenXExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;


@Service
@RequiredArgsConstructor
public class AccessService {
    private static final List<String> SESSION_PROFILE = singletonList("local");
    private static final List<String> IDPORTEN_PROFILE = singletonList("idporten");
    private final TokenExchange tokenExchange;
    private final TokenXExchange tokenXExchange;

    private final Environment environment;

    private final no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange sessionTokenExchange;

    public Mono<String> getAccessToken(ServerProperties serverProperties, ServerWebExchange exchange) {

        if (Arrays.stream(environment.getActiveProfiles()).anyMatch(SESSION_PROFILE::contains)) {

            return sessionTokenExchange
                    .exchange(serverProperties, exchange)
                    .map(AccessToken::getTokenValue);
        } else if (Arrays.stream(environment.getActiveProfiles()).anyMatch(IDPORTEN_PROFILE::contains)) {

            return tokenXExchange
                    .exchange(serverProperties, exchange)
                    .map(AccessToken::getTokenValue);
        } else {
            return tokenExchange
                    .exchange(serverProperties)
                    .map(AccessToken::getTokenValue);
        }
    }
}
