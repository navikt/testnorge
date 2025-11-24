package no.nav.dolly.web.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.consumers.command.GetBrukerCommand;
import no.nav.dolly.web.consumers.command.GetTokenCommand;
import no.nav.dolly.web.consumers.dto.BrukerDTO;
import no.nav.dolly.web.service.AccessService;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.TestnavBrukerServiceProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BrukerConsumer {
    private final WebClient webClient;
    private final TestnavBrukerServiceProperties serviceProperties;
    private final AccessService accessService;

    public BrukerConsumer(
            TestnavBrukerServiceProperties serviceProperties,
            AccessService accessService,
            WebClient webClient
    ) {
        this.serviceProperties = serviceProperties;
        this.accessService = accessService;
        this.webClient = webClient
                .mutate()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    public Mono<BrukerDTO> getBruker(String orgnummer, ServerWebExchange serverWebExchange) {
        return accessService.getAccessToken(serviceProperties, serverWebExchange)
                .flatMap(accessToken -> new GetBrukerCommand(webClient, accessToken, orgnummer).call());
    }

    public Mono<String> getToken(String id, ServerWebExchange serverWebExchange) {
        return accessService.getAccessToken(serviceProperties, serverWebExchange)
                .flatMap(accessToken -> new GetTokenCommand(webClient, accessToken, id).call());
    }

}