package no.nav.testnav.apps.oversiktfrontend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.oversiktfrontend.domain.Application;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.AzureAdTokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.ClientRegistrationIdResolver;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenExchange tokenExchange;
    private final ClientRegistrationIdResolver clientRegistrationIdResolver;
    private final AzureAdTokenExchange azureAdTokenExchange;

    public Mono<AccessToken> getMagicToken(ServerWebExchange exchange) {
        return clientRegistrationIdResolver
                .getClientRegistrationId().flatMap(clientId -> {
                    if (clientId.equals("idporten")) {
                        return tokenExchange.exchange(
                                ServerProperties
                                        .builder()
                                        .name("app-2")
                                        .cluster("dev-gcp")
                                        .namespace("plattformsikkerhet")
                                        .build(),
                                exchange
                        );
                    }
                    return tokenExchange.exchange(ServerProperties
                                    .builder()
                                    .name("team-dolly-lokal-app")
                                    .cluster("dev-fss")
                                    .namespace("dolly")
                                    .build(),
                            exchange
                    );
                });
    }

    public Mono<AccessToken> getToken(Application application, Boolean clientCredentials, ServerWebExchange exchange) {
        if (clientCredentials) {
            return azureAdTokenExchange.generateClientCredentialAccessToken(application.toServerProperties());
        }
        return tokenExchange.exchange(application.toServerProperties(), exchange);
    }
}
