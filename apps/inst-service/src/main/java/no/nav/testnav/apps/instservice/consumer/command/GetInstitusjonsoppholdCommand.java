package no.nav.testnav.apps.instservice.consumer.command;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.instservice.domain.InstitusjonResponse;
import no.nav.testnav.apps.instservice.exception.UgyldigIdentResponseException;
import no.nav.testnav.apps.instservice.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;
import static no.nav.testnav.apps.instservice.properties.HttpRequestConstants.ACCEPT;
import static no.nav.testnav.apps.instservice.properties.HttpRequestConstants.HEADER_NAV_CALL_ID;
import static no.nav.testnav.apps.instservice.properties.HttpRequestConstants.HEADER_NAV_CONSUMER_ID;

@Slf4j
@RequiredArgsConstructor
public class GetInstitusjonsoppholdCommand implements Callable<InstitusjonResponse> {
    private static final ParameterizedTypeReference<InstitusjonResponse> RESPONSE_TYPE_HENT_INSTITUSJONSOPPHOLD = new ParameterizedTypeReference<>() {
    };

    private final WebClient webClient;
    private final String miljoe;
    private final String ident;
    private final String callId;
    private final String consumerId;

    @SneakyThrows
    @Override
    public InstitusjonResponse call() {
        InstitusjonResponse response;
        try {
            ResponseEntity<InstitusjonResponse> listResponseEntity = webClient.get()
                    .uri(builder ->
                            builder.path("/api/v1/institusjonsopphold/person")
                                    .queryParam("environments", miljoe)
                                    .build()
                    )
                    .header(ACCEPT, "application/json")
                    .header(HEADER_NAV_CALL_ID, callId)
                    .header(HEADER_NAV_CONSUMER_ID, consumerId)
                    .header("norskident", ident)
                    .retrieve()
                    .toEntity(RESPONSE_TYPE_HENT_INSTITUSJONSOPPHOLD)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();
            response = nonNull(listResponseEntity)
                    ? listResponseEntity.getBody()
                    : new InstitusjonResponse();
            log.info("Hentet Inst2 opphold: " + response);
        } catch (WebClientResponseException e) {
            log.error("Kunne ikke hente ident fra inst2", e);
            throw new UgyldigIdentResponseException("Kunne ikke hente ident fra inst2", e);
        }
        return response;
    }
}
