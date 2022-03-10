package no.nav.registre.testnorge.arena.consumer.rs.command.pensjon;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.request.pensjon.PensjonTestdataPerson;
import no.nav.registre.testnorge.arena.consumer.rs.response.pensjon.PensjonTestdataResponse;
import no.nav.registre.testnorge.arena.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.Callable;

import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.AUTHORIZATION;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.CALL_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.CONSUMER_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.NAV_CALL_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.NAV_CONSUMER_ID;

@Slf4j
public class PostPensjonTestdataPersonCommand implements Callable<PensjonTestdataResponse> {

    private final WebClient webClient;
    private final PensjonTestdataPerson person;
    private final String idToken;

    public PostPensjonTestdataPersonCommand(WebClient webclient, PensjonTestdataPerson person, String idToken) {
        this.webClient = webclient;
        this.person = person;
        this.idToken = idToken;
    }

    @Override
    public PensjonTestdataResponse call() {
        try {
            log.info("Oppretter ny pensjon testdata person.");
            return webClient.post()
                    .uri(builder ->
                            builder.path("/v1/person").build()
                    )
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(AUTHORIZATION, idToken)
                    .body(BodyInserters.fromPublisher(Mono.just(person), PensjonTestdataPerson.class))
                    .retrieve()
                    .bodyToMono(PensjonTestdataResponse.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke å opprette pensjon testdata person.", e);
            return PensjonTestdataResponse.builder().status(Collections.emptyList()).build();
        }
    }
}
