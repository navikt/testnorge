package no.nav.registre.testnorge.arena.consumer.rs.command;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.NyBruker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils.EIER;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.CALL_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.CONSUMER_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.NAV_CALL_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.NAV_CONSUMER_ID;

@Slf4j
public class PostArenaBrukerCommand implements Callable<NyeBrukereResponse> {

    private final WebClient webClient;
    private final Map<String, List<NyBruker>> nyeBrukere;

    private static final ParameterizedTypeReference<Map<String, List<NyBruker>>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };

    public PostArenaBrukerCommand(List<NyBruker> nyeBrukere, WebClient webClient) {
        this.webClient = webClient;
        this.nyeBrukere = Collections.singletonMap("nyeBrukere", nyeBrukere);
    }

    @Override
    public NyeBrukereResponse call() {
        try {
            log.info("Sender inn ny(e) bruker(e) til Arena-forvalteren.");
            return webClient.post()
                    .uri(builder ->
                            builder.path("/v1/bruker")
                                    .queryParam("eier", EIER)
                                    .build()
                    )
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromPublisher(Mono.just(nyeBrukere), REQUEST_TYPE))
                    .retrieve()
                    .bodyToMono(NyeBrukereResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke å sende inn ny(e) bruker(e) til Arena-forvalteren.", e);
            throw e;
        }
    }
}
