package no.nav.dolly.bestilling.bistandsbehov;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.bistandsbehov.command.OpprettBistandsvedtakCommand;
import no.nav.dolly.bestilling.bistandsbehov.command.StartOppfoelgingsperiodeCommand;
import no.nav.dolly.bestilling.bistandsbehov.dto.BistandVedtakRequestDTO;
import no.nav.dolly.bestilling.bistandsbehov.dto.ResponseStatusDTO;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class BistandsbehovConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public BistandsbehovConsumer(
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

    public Mono<ResponseStatusDTO> opprettBistandVedtak(BistandVedtakRequestDTO request) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new OpprettBistandsvedtakCommand(webClient, request, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Status fra OBO opprett bistandsvedtak for ident {} {}", request, response));
    }
}
