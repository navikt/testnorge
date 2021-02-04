package no.nav.registre.testnorge.arena.consumer.rs.command;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.request.PensjonTestdataPerson;
import no.nav.registre.testnorge.arena.consumer.rs.response.PensjonTestdataResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.arena.consumer.rs.util.Headers;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.concurrent.Callable;

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
                    .header(Headers.CALL_ID, Headers.NAV_CALL_ID)
                    .header(Headers.CONSUMER_ID, Headers.NAV_CONSUMER_ID)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(Headers.AUTHORIZATION, idToken)
                    .body(BodyInserters.fromPublisher(Mono.just(person), PensjonTestdataPerson.class))
                    .retrieve()
                    .bodyToMono(PensjonTestdataResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke å opprette pensjon testdata person.", e);
            return PensjonTestdataResponse.builder().status(Collections.emptyList()).build();
        }
    }
}
