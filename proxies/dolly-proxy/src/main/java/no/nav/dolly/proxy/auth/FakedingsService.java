package no.nav.dolly.proxy.auth;

import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.web.reactive.function.client.WebClient;

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

    GatewayFilter bearerAuthenticationHeaderFilter(ServerProperties serverProperties) {

        return (exchange, chain) -> {
            var ident = exchange
                    .getRequest()
                    .getHeaders()
                    .getFirst("fnr");
            return new FakedingsCommand(webClient, ident)
                    .call()
                    .flatMap(faketoken -> tokenXService
                            .exchange(serverProperties, faketoken)
                            .flatMap(token -> {
                                var mutatedExchange = exchange
                                        .mutate()
                                        .request(builder -> builder
                                                .headers(WebClientHeader.bearer(token.getTokenValue()))
                                                .build())
                                        .build();
                                return chain.filter(mutatedExchange);
                            }));
        };

    }

}