package no.nav.registre.bisys.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.bisys.util.WebClientFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import no.nav.registre.bisys.consumer.response.SyntetisertBidragsmelding;
import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public class OpprettBidragsmeldingCommand implements Runnable {
    private final WebClient webClient;
    private final SyntetisertBidragsmelding bidragsmelding;

    @Override
    public void run() {
        log.info("Oppretting av bidragsmeldinger ikke ferdig implementert.");
//        webClient
//                .post()
//                .uri("/api/v1/dummy")
//                .body(BodyInserters.fromPublisher(Mono.just(bidragsmelding), SyntetisertBidragsmelding.class))
//                .retrieve()
//                .bodyToMono(Void.class)
//                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
//                        .filter(WebClientFilter::is5xxException))
//                .block();
    }
}
