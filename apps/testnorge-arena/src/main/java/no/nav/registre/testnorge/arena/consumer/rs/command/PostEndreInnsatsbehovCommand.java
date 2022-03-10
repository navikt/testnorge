package no.nav.registre.testnorge.arena.consumer.rs.command;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.request.EndreInnsatsbehovRequest;
import no.nav.registre.testnorge.arena.consumer.rs.response.EndreInnsatsbehovResponse;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.CALL_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.CONSUMER_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.NAV_CALL_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.NAV_CONSUMER_ID;

@Slf4j
public class PostEndreInnsatsbehovCommand implements Callable<EndreInnsatsbehovResponse> {

    private final WebClient webClient;
    private final EndreInnsatsbehovRequest request;

    public PostEndreInnsatsbehovCommand(EndreInnsatsbehovRequest request, WebClient webClient) {
        this.webClient = webClient;
        this.request = request;
    }

    @Override
    public EndreInnsatsbehovResponse call() {
        try {
            return webClient.post()
                    .uri(builder ->
                            builder.path("/v1/endreInnsatsbehov")
                                    .build()
                    )
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromPublisher(Mono.just(request), EndreInnsatsbehovRequest.class))
                    .retrieve()
                    .bodyToMono(EndreInnsatsbehovResponse.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();
        } catch (Exception e) {
            log.error("Kunne ikke endre innsatsbehov i arena forvalteren.", e);
            return null;
        }
    }
}
