package no.nav.registre.testnorge.batchbestillingservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.batchbestillingservice.command.GetAktiveBestillingerCommand;
import no.nav.registre.testnorge.batchbestillingservice.command.PostBestillingCommand;
import no.nav.registre.testnorge.batchbestillingservice.credentials.DollyBackendServiceProperties;
import no.nav.registre.testnorge.batchbestillingservice.request.RsDollyBestillingRequest;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
public class DollyBackendConsumer {


    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serviceProperties;

    public DollyBackendConsumer(DollyBackendServiceProperties properties,
                                TokenExchange tokenService,
                                ExchangeFilterFunction metricsWebClientFilterFunction
    ) {

        this.serviceProperties = properties;
        this.tokenService = tokenService;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public void postDollyBestilling(Long gruppeId, RsDollyBestillingRequest request, Long antall) {

        request.setAntall(antall.intValue());

        tokenService
                .exchange(serviceProperties)
                .map(token -> new PostBestillingCommand(webClient, token.getTokenValue(), gruppeId, request).call())
                .doOnError(error -> log.error("Bestilling feilet for gruppe {}", gruppeId, error))
                .doOnSuccess(response -> log.info("Bestilling med {} identer startet i backend for gruppe {}", antall, gruppeId))
                .subscribe();
    }

    public List<Object> getAktiveBestillinger(Long gruppeId) {

        return tokenService
                .exchange(serviceProperties)
                .map(token -> new GetAktiveBestillingerCommand(webClient, token.getTokenValue(), gruppeId).call())
                .doOnError(error -> log.error("Henting av aktive bestillinger feilet for gruppe {}", gruppeId, error))
                .onErrorReturn(Flux.empty())
                .block()
                .collectList()
                .block();
    }
}
