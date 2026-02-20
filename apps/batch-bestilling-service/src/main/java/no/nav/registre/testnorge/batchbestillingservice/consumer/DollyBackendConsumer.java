package no.nav.registre.testnorge.batchbestillingservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.batchbestillingservice.command.GetAktiveBestillingerCommand;
import no.nav.registre.testnorge.batchbestillingservice.command.PostBestillingCommand;
import no.nav.registre.testnorge.batchbestillingservice.config.Consumers;
import no.nav.registre.testnorge.batchbestillingservice.request.RsDollyBestillingRequest;
import no.nav.registre.testnorge.batchbestillingservice.response.AktivBestillingResponse;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DollyBackendConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public DollyBackendConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getDollyBackend();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<Void> postDollyBestilling(Long gruppeId, RsDollyBestillingRequest request, Long antall) {
        request.setAntall(antall.intValue());

        return tokenExchange
                .exchange(serverProperties)
                .flatMap(token -> new PostBestillingCommand(webClient, token.getTokenValue(), gruppeId, request).execute())
                .doOnError(error -> log.error("Bestilling feilet for gruppe {}", gruppeId, error))
                .doOnSuccess(response -> log.info("Bestilling med {} identer startet i backend for gruppe {}", antall, gruppeId));
    }

    public Flux<AktivBestillingResponse> getAktiveBestillinger(Long gruppeId) {
        return tokenExchange
                .exchange(serverProperties)
                .flatMapMany(token -> new GetAktiveBestillingerCommand(webClient, token.getTokenValue(), gruppeId).execute())
                .doOnError(error -> log.error("Henting av aktive bestillinger feilet for gruppe {}", gruppeId, error))
                .onErrorResume(WebClientResponseException.NotFound.class, throwable -> Flux.empty());
    }

}
