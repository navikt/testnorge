package no.nav.dolly.proxy.auth;

import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

class FakedingsService {

    private final WebClient webClient;

    FakedingsService(WebClient webClient, String url) {
        this.webClient = webClient
                .mutate()
                .baseUrl(url)
                .build();
    }

    private Mono<String> getFakeToken(String ident) {
        return new FakedingsCommand(webClient, ident).call();
    }

    GatewayFilter bearerAuthenticationHeaderFilter(
            FakedingsService fakedings,
            TokenXService tokenx,
            ServerProperties serverProperties
    ) {

        return (exchange, chain) -> {
            var ident = exchange
                    .getRequest()
                    .getHeaders()
                    .getFirst("fnr");
            return fakedings
                    .getFakeToken(ident)
                    .flatMap(faketoken -> tokenx
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