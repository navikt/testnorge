package no.nav.registre.inst.consumer.rs.command;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.util.WebClientFilter;
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
public class DeleteInstitusjonsoppholdCommand implements Callable<ResponseEntity<Object>> {
    private static final String INSTITUSJONSOPPHOLD_PERSON = "/v1/institusjonsopphold/person";
    private static final String ENVIRONMENTS = "environments";
    private static final String NORSKIDENT = "norskident";

    private static final ParameterizedTypeReference<Object> RESPONSE_TYPE_OBJECT = new ParameterizedTypeReference<>() {
    };

    private final WebClient webClient;
    private final String inst2newServerUrl;
    private final String miljoe;
    private final String ident;
    private final String callId;
    private final String consumerId;

    @SneakyThrows
    @Override
    public ResponseEntity<Object> call() {
        try {
            var response = webClient.delete()
                    .uri(inst2newServerUrl, uriBuilder -> uriBuilder
                            .path(INSTITUSJONSOPPHOLD_PERSON)
                            .queryParam(ENVIRONMENTS, miljoe)
                            .build())
                    .header(ACCEPT, "application/json")
                    .header(HEADER_NAV_CALL_ID, callId)
                    .header(HEADER_NAV_CONSUMER_ID, consumerId)
                    .header(NORSKIDENT, ident)
                    .retrieve()
                    .toEntity(RESPONSE_TYPE_OBJECT)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();

            return nonNull(response)
                    ? ResponseEntity.status(response.getStatusCode()).body(response.getBody())
                    : ResponseEntity.notFound().build();
        } catch (WebClientResponseException e) {
            log.error("Kunne ikke slette institusjonsopphold - {}", e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }
}
