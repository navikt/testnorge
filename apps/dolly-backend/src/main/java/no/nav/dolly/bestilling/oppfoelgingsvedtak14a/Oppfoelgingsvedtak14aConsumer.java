package no.nav.dolly.bestilling.oppfoelgingsvedtak14a;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.oppfoelgingsvedtak14a.command.CreateOppfoelgingsvedtak14aCommand;
import no.nav.dolly.bestilling.oppfoelgingsvedtak14a.command.DeleteOppfoelgingsvedtak14aCommand;
import no.nav.dolly.bestilling.oppfoelgingsvedtak14a.command.StartOppfoelgingsperiodeCommand;
import no.nav.dolly.bestilling.oppfoelgingsvedtak14a.dto.Oppfoelgingsvedtak14aRequestDTO;
import no.nav.dolly.bestilling.oppfoelgingsvedtak14a.dto.ResponseStatusDTO;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class Oppfoelgingsvedtak14aConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public Oppfoelgingsvedtak14aConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient webClient) {

        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavDollyProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<ResponseStatusDTO> startOppfoelgingsperiode(String ident) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new StartOppfoelgingsperiodeCommand(webClient, ident, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Status fra DAB start oppfølgingsperiode for ident {} {}", ident, response));
    }

    public Mono<ResponseStatusDTO> opprettBistandVedtak(Oppfoelgingsvedtak14aRequestDTO request) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new CreateOppfoelgingsvedtak14aCommand(webClient, request, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Status fra OBO opprett bistandsvedtak {} er {}", request, response));
    }

    public Flux<ResponseStatusDTO> slettBistandsvedtak(List<String> identer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> Flux.fromIterable(identer)
                        .flatMap(ident -> new DeleteOppfoelgingsvedtak14aCommand(webClient, ident, token.getTokenValue()).call()));
    }
}
