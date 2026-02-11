package no.nav.dolly.proxy.auth;

import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

class FakedingsService {

    private final TokenXService tokenXService;
    private final WebClient webClient;

    FakedingsService(TokenXService tokenXService, WebClient webClient, String url) {
        this.tokenXService = tokenXService;
        this.webClient = webClient
                .mutate()
                .baseUrl(url)
                .build();
    }

    Mono<AccessToken> exchange(String ident, ServerProperties serverProperties) {
        return new FakedingsCommand(webClient, ident)
                .call()
                .flatMap(faketoken -> tokenXService.exchange(serverProperties, faketoken));
    }

}