package no.nav.registre.arena.core.consumer.rs.command;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.request.RettighetSyntRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.registre.arena.core.consumer.rs.util.Headers.CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.CONSUMER_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.NAV_CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.NAV_CONSUMER_ID;

@Slf4j
public class PostSyntTilleggRequestCommand implements Callable<List<NyttVedtakTillegg>> {

    private final WebClient webClient;
    private final List<RettighetSyntRequest> requests;
    private final String urlPath;


    private static final ParameterizedTypeReference<List<RettighetSyntRequest>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<NyttVedtakTillegg>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    public PostSyntTilleggRequestCommand(WebClient webClient, List<RettighetSyntRequest> requests, String urlPath) {
        this.webClient = webClient;
        this.requests = requests;
        this.urlPath = urlPath;
    }

    @Override
    public List<NyttVedtakTillegg> call() {
        List<NyttVedtakTillegg> response = Collections.emptyList();
        try {
            log.info("Henter syntetiske tilleggsstonad vedtak.");
            response = webClient.post()
                    .uri(builder ->
                            builder.path(urlPath)
                                    .build()
                    )
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromPublisher(Mono.just(requests), REQUEST_TYPE))
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE)
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke hente syntetiske tilleggsstonad vedtak.", e);
        }
        return response;
    }
}
