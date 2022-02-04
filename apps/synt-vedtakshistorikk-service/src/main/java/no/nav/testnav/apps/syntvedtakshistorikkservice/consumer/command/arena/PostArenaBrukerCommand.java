package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.arena;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.NyBruker;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
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

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.AUTHORIZATION;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.CALL_ID;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.CONSUMER_ID;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.NAV_CALL_ID;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.NAV_CONSUMER_ID;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.EIER;

@Slf4j
public class PostArenaBrukerCommand implements Callable<Mono<NyeBrukereResponse>> {

    private final WebClient webClient;
    private final Map<String, List<NyBruker>> nyeBrukere;
    private final String token;

    private static final ParameterizedTypeReference<Map<String, List<NyBruker>>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };

    public PostArenaBrukerCommand(List<NyBruker> nyeBrukere, String token,  WebClient webClient) {
        this.webClient = webClient;
        this.nyeBrukere = Collections.singletonMap("nyeBrukere", nyeBrukere);
        this.token = token;
    }

    @Override
    public Mono<NyeBrukereResponse> call() {
        try {
            log.info("Sender inn ny(e) bruker(e) til Arena-forvalteren.");
            return webClient.post()
                    .uri(builder ->
                            builder.path("/api/v1/bruker")
                                    .queryParam("eier", EIER)
                                    .build()
                    )
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .header(AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromPublisher(Mono.just(nyeBrukere), REQUEST_TYPE))
                    .retrieve()
                    .bodyToMono(NyeBrukereResponse.class);
        } catch (Exception e) {
            log.error("Klarte ikke å sende inn ny(e) bruker(e) til Arena-forvalteren.", e);
            throw e;
        }
    }
}
