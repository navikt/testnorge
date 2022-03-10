package no.nav.registre.inst.consumer.rs.command;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.domain.InstitusjonResponse;
import no.nav.registre.inst.exception.UgyldigIdentResponseException;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;
import static no.nav.registre.inst.properties.HttpRequestConstants.ACCEPT;
import static no.nav.registre.inst.properties.HttpRequestConstants.HEADER_NAV_CALL_ID;
import static no.nav.registre.inst.properties.HttpRequestConstants.HEADER_NAV_CONSUMER_ID;

@Slf4j
@RequiredArgsConstructor
public class GetInstitusjonsoppholdCommand implements Callable<InstitusjonResponse> {
    private static final String INSTITUSJONSOPPHOLD_PERSON = "/v1/institusjonsopphold/person";
    private static final String ENVIRONMENTS = "environments";
    private static final String NORSKIDENT = "norskident";

    private static final ParameterizedTypeReference<InstitusjonResponse> RESPONSE_TYPE_HENT_INSTITUSJONSOPPHOLD = new ParameterizedTypeReference<>() {
    };

    private final WebClient webClient;
    private final String inst2newServerUrl;
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
                    .uri(inst2newServerUrl,
                            uriBuilder -> uriBuilder
                                    .path(INSTITUSJONSOPPHOLD_PERSON)
                                    .queryParam(ENVIRONMENTS, miljoe)
                                    .build())
                    .header(ACCEPT, "application/json")
                    .header(HEADER_NAV_CALL_ID, callId)
                    .header(HEADER_NAV_CONSUMER_ID, consumerId)
                    .header(NORSKIDENT, ident)
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
