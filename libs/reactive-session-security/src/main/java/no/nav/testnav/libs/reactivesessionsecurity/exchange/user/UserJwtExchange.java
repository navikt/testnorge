package no.nav.testnav.libs.reactivesessionsecurity.exchange.user;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;

@Component
@Import(TestnavBrukerServiceProperties.class)
public class UserJwtExchange {
    private final WebClient webClient;
    private final TestnavBrukerServiceProperties serviceProperties;
    private final TokenExchange tokenExchange;

    public UserJwtExchange(TestnavBrukerServiceProperties serviceProperties, TokenExchange tokenExchange) {
        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }


    public Mono<String> generateJwt(String id, ServerWebExchange exchange) {
        return tokenExchange.exchange(serviceProperties, exchange)
                .flatMap(accessToken -> new GetTokenCommand(webClient, accessToken.getTokenValue(), id).call());
    }
}