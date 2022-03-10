package no.nav.registre.inst.consumer.rs.command;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.domain.Institusjonsopphold;
import no.nav.registre.inst.domain.InstitusjonsoppholdV2;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.registre.inst.properties.HttpRequestConstants.ACCEPT;
import static no.nav.registre.inst.properties.HttpRequestConstants.HEADER_NAV_CALL_ID;
import static no.nav.registre.inst.properties.HttpRequestConstants.HEADER_NAV_CONSUMER_ID;

@Slf4j
@RequiredArgsConstructor
public class PostInstitusjonsoppholdCommand implements Callable<OppholdResponse> {
    private static final String INSTITUSJONSOPPHOLD_PERSON = "/v1/institusjonsopphold/person";
    private static final String ENVIRONMENTS = "environments";

    private static final ParameterizedTypeReference<Object> RESPONSE_TYPE_OBJECT = new ParameterizedTypeReference<>() {
    };

    private final WebClient webClient;
    private final String inst2newServerUrl;
    private final String miljoe;
    private final InstitusjonsoppholdV2 institusjonsopphold;
    private final String callId;
    private final String consumerId;

    @SneakyThrows
    @Override
    public OppholdResponse call() {
        try {
            var response = webClient.post()
                    .uri(inst2newServerUrl, uriBuilder -> uriBuilder
                            .path(INSTITUSJONSOPPHOLD_PERSON)
                            .queryParam(ENVIRONMENTS, miljoe)
                            .build())
                    .header(ACCEPT, "application/json")
                    .header(HEADER_NAV_CALL_ID, callId)
                    .header(HEADER_NAV_CONSUMER_ID, consumerId)
                    .bodyValue(institusjonsopphold).retrieve().toEntity(RESPONSE_TYPE_OBJECT)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();

            Institusjonsopphold institusjonsoppholdResponse = new ObjectMapper().convertValue(response.getBody(), Institusjonsopphold.class);
            return OppholdResponse.builder()
                    .status(response.getStatusCode())
                    .institusjonsopphold(institusjonsoppholdResponse)
                    .build();
        } catch (WebClientResponseException e) {
            log.error("Kunne ikke legge til institusjonsopphold i inst2 på ident - {}", e.getResponseBodyAsString(), e);
            return OppholdResponse.builder()
                    .status(e.getStatusCode())
                    .feilmelding(e.getResponseBodyAsString())
                    .build();
        }
    }
}
