package no.nav.dolly.web.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.consumers.command.GetBrukerCommand;
import no.nav.dolly.web.consumers.command.GetTokenCommand;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import no.nav.dolly.web.consumers.dto.BrukerDTO;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.TestnavBrukerServiceProperties;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class BrukerConsumer {
    private final WebClient webClient;
    private final TestnavBrukerServiceProperties serviceProperties;
    private final TokenExchange tokenExchange;

    public BrukerConsumer(TestnavBrukerServiceProperties serviceProperties, TokenExchange tokenExchange) {
        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    public Mono<BrukerDTO> getBruker(String orgnummer, ServerWebExchange serverWebExchange) {
        return tokenExchange.generateToken(serviceProperties, serverWebExchange)
                .flatMap(accessToken -> new GetBrukerCommand(webClient, accessToken.getTokenValue(), orgnummer).call());
    }

    public Mono<String> getToken(String id, ServerWebExchange serverWebExchange){
        return tokenExchange.generateToken(serviceProperties, serverWebExchange)
                .flatMap(accessToken -> new GetTokenCommand(webClient, accessToken.getTokenValue(), id).call());
    }

}