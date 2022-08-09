package no.nav.testnav.apps.instservice.consumer.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.instservice.domain.Institusjonsopphold;
import no.nav.testnav.apps.instservice.domain.InstitusjonsoppholdV2;
import no.nav.testnav.apps.instservice.provider.responses.OppholdResponse;
import no.nav.testnav.apps.instservice.util.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;
import static no.nav.testnav.apps.instservice.properties.HttpRequestConstants.ACCEPT;
import static no.nav.testnav.apps.instservice.properties.HttpRequestConstants.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class PostInstitusjonsoppholdCommand implements Callable<OppholdResponse> {
    private final WebClient webClient;
    private final String token;
    private final String miljoe;
    private final InstitusjonsoppholdV2 institusjonsopphold;

    @SneakyThrows
    @Override
    public OppholdResponse call() {
        try {
            var response = webClient.post()
                    .uri(builder ->
                            builder.path("/api/v1/institusjonsopphold/person")
                                    .queryParam("environments", miljoe)
                                    .build()
                    )
                    .header(ACCEPT, "application/json")
                    .header(AUTHORIZATION, "Bearer " + token)
                    .bodyValue(institusjonsopphold)
                    .retrieve()
                    .toEntity(Object.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();

            if (nonNull(response)){
                var institusjonsoppholdResponse = new ObjectMapper().convertValue(response.getBody(), Institusjonsopphold.class);
                return OppholdResponse.builder()
                        .status(response.getStatusCode())
                        .institusjonsopphold(institusjonsoppholdResponse)
                        .build();
            } else {
                return new OppholdResponse();
            }
        } catch (WebClientResponseException e) {
            log.error("Kunne ikke legge til institusjonsopphold i inst2 på ident - {}", e.getResponseBodyAsString(), e);
            return OppholdResponse.builder()
                    .status(e.getStatusCode())
                    .feilmelding(e.getResponseBodyAsString())
                    .build();
        }
    }
}
