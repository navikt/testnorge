package no.nav.registre.testnorge.arena.consumer.rs.command;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetSyntRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.CALL_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.CONSUMER_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.NAV_CALL_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.NAV_CONSUMER_ID;

@Slf4j
public class PostSyntAapRequestCommand implements Callable<List<NyttVedtakAap>> {

    private final WebClient webClient;
    private final List<RettighetSyntRequest> requests;
    private final String urlPath;


    private static final ParameterizedTypeReference<List<RettighetSyntRequest>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<NyttVedtakAap>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    public PostSyntAapRequestCommand(WebClient webClient, List<RettighetSyntRequest> requests, String urlPath ){
        this.webClient = webClient;
        this.requests = requests;
        this.urlPath = urlPath;
    }

    @Override
    public List<NyttVedtakAap> call(){
        List<NyttVedtakAap> response = Collections.emptyList();
        try {
            log.info("Henter syntetiske AAP vedtak.");
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
            log.error("Klarte ikke hente syntetiske AAP vedtak.", e);
        }
        return response;
    }
}
