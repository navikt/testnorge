package no.nav.testnav.libs.reactivesessionsecurity.exchange.user;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import no.nav.testnav.libs.reactivesessionsecurity.domain.TestnavBrukerServiceServiceProperties;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;

@Component
public class UserJwtExchange {
    private final WebClient webClient;
    private final TestnavBrukerServiceServiceProperties serviceProperties;
    private final TokenExchange tokenExchange;

    public UserJwtExchange(TestnavBrukerServiceServiceProperties serviceProperties, TokenExchange tokenExchange) {
        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }


    public Mono<String> generateJwt(String id, ServerWebExchange exchange) {
        return tokenExchange.generateToken(serviceProperties, exchange)
                .flatMap(accessToken -> new GetTokenCommand(webClient, accessToken.getTokenValue(), id).call());
    }

}
