package no.nav.testnav.apps.oversiktfrontend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.oversiktfrontend.domain.Application;
import no.nav.testnav.libs.reactivesessionsecurity.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.ClientRegistrationIdResolver;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenExchange tokenExchange;
    private final ClientRegistrationIdResolver clientRegistrationIdResolver;

    public Mono<AccessToken> getMagicToken(ServerWebExchange exchange) {
        return clientRegistrationIdResolver
                .getClientRegistrationId().flatMap(clientId -> {
                    if (clientId.equals("idporten")) {
                        return tokenExchange.generateToken(
                                ServerProperties
                                        .builder()
                                        .name("app-2")
                                        .cluster("dev-gcp")
                                        .namespace("plattformsikkerhet")
                                        .build(),
                                exchange
                        );
                    }
                    return tokenExchange.generateToken(ServerProperties
                                    .builder()
                                    .name("team-dolly-lokal-app")
                                    .cluster("dev-fss")
                                    .namespace("dolly")
                                    .build(),
                            exchange
                    );
                });
    }

    public Mono<AccessToken> getToken(Application application, ServerWebExchange exchange) {
        return tokenExchange.generateToken(application.toServerProperties(), exchange);
    }
}
