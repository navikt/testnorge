package no.nav.registre.testnorge.batchbestillingservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.batchbestillingservice.credentials.DollyBackendDevServiceProperties;
import no.nav.registre.testnorge.batchbestillingservice.credentials.DollyBackendServiceProperties;
import no.nav.registre.testnorge.batchbestillingservice.request.RsDollyBestillingRequest;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class DollyBackendConsumer {

    private final String HEADER_NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serviceProperties;
    private final ServerProperties devServiceProperties;
    private final String HEADER_NAV_CALL_ID = "Nav-Call-Id";

    public DollyBackendConsumer(DollyBackendServiceProperties properties,
                                DollyBackendDevServiceProperties devProperties,
                                TokenExchange tokenService
    ) {

        this.serviceProperties = properties;
        this.devServiceProperties = devProperties;
        this.tokenService = tokenService;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public void postDollyBestilling(Long gruppeId, RsDollyBestillingRequest request, Long antall, Boolean sendToProd) {

        var callId = "Batch-service";
        var CONSUMER = "Batch-service";

        request.setAntall(antall.intValue());


        tokenService
                .exchange(sendToProd ? serviceProperties : devServiceProperties)
                .flatMap(token -> webClient.post()
                        .uri(builder ->
                                builder.path("/api/v1/bestilling/{gruppe}").build(gruppeId))
                        .header(AUTHORIZATION, token.getTokenValue())
                        .header(HEADER_NAV_CALL_ID, callId)
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .bodyValue(request)
                        .retrieve()
                        .toBodilessEntity()
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                                .filter(WebClientFilter::is5xxException))
                        .doOnError(throwable -> log.error(throwable.getMessage()))).block();
    }
}
