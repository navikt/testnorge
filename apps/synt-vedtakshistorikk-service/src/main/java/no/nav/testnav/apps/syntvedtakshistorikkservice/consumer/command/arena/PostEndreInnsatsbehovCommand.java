package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.arena;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.EndreInnsatsbehovRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.EndreInnsatsbehovResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.CALL_ID;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.CONSUMER_ID;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.NAV_CALL_ID;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.NAV_CONSUMER_ID;

@Slf4j
@AllArgsConstructor
public class PostEndreInnsatsbehovCommand implements Callable<Mono<EndreInnsatsbehovResponse>> {

    private final EndreInnsatsbehovRequest request;
    private final String token;
    private final WebClient webClient;

    @Override
    public Mono<EndreInnsatsbehovResponse> call() {
        try {
            return webClient.post()
                    .uri(builder ->
                            builder.path("/api/v1/endreInnsatsbehov")
                                    .build()
                    )
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromPublisher(Mono.just(request), EndreInnsatsbehovRequest.class))
                    .retrieve()
                    .bodyToMono(EndreInnsatsbehovResponse.class);
        } catch (Exception e) {
            log.error("Kunne ikke endre innsatsbehov i arena forvalteren.", e);
            return Mono.empty();
        }
    }
}
