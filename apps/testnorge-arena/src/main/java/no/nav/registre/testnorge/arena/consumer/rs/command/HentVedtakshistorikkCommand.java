package no.nav.registre.testnorge.arena.consumer.rs.command;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.CALL_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.CONSUMER_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.NAV_CALL_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.NAV_CONSUMER_ID;

@Slf4j
public class HentVedtakshistorikkCommand implements Callable<List<Vedtakshistorikk>> {

    private final List<String> oppstartsdatoer;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<Vedtakshistorikk>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    public HentVedtakshistorikkCommand(WebClient webClient, List<String> oppstartsdatoer) {
        this.webClient = webClient;
        this.oppstartsdatoer = oppstartsdatoer;
    }

    @Override
    public List<Vedtakshistorikk> call() {
        List<Vedtakshistorikk> response = Collections.emptyList();
        try {
            log.info("Henter vedtakshistorikk.");
            response = webClient.post()
                    .uri(builder ->
                            builder.path("/v1/arena/vedtakshistorikk")
                                    .build()
                    )
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromPublisher(Mono.just(oppstartsdatoer), REQUEST_TYPE))
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE)
                    .block();
        } catch (Exception | Error e) {
            log.error("Klarte ikke hente vedtakshistorikk.", e);
        }
        return response;
    }

}
