package no.nav.testnav.apps.oversiktfrontend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.oversiktfrontend.domain.Application;
import no.nav.testnav.libs.reactivesessionsecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesessionsecurity.domain.ServerProperties;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenExchange tokenExchange;

    // TODO Support tokenX
    public Mono<AccessToken> getMagicToken(ServerWebExchange exchange) {
        var TeamDollyLokalAppServerProperties = ServerProperties
                .builder()
                .name("team-dolly-lokal-app")
                .cluster("dev-fss")
                .namespace("dolly")
                .build();

        return tokenExchange.generateToken(TeamDollyLokalAppServerProperties, exchange);
    }

    public Mono<AccessToken> getToken(Application application, ServerWebExchange exchange) {
        return tokenExchange.generateToken(application.toServerProperties(), exchange);
    }
}
