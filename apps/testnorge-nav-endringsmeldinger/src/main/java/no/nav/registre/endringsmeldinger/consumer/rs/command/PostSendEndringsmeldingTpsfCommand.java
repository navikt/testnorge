package no.nav.registre.endringsmeldinger.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.endringsmeldinger.consumer.rs.requests.SendTilTpsRequest;
import no.nav.registre.endringsmeldinger.consumer.rs.responses.RsPureXmlMessageResponse;
import no.nav.registre.endringsmeldinger.util.WebClientFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class PostSendEndringsmeldingTpsfCommand implements Callable<RsPureXmlMessageResponse> {

    private final SendTilTpsRequest sendTilTpsRequest;
    private final String username;
    private final String password;
    private final WebClient webClient;

    @Override
    public RsPureXmlMessageResponse call() {

        return webClient
                .post()
                .uri("/api/v1/xmlmelding")
                .headers(httpHeaders -> httpHeaders.setBasicAuth(username, password))
                .body(BodyInserters.fromPublisher(Mono.just(sendTilTpsRequest), SendTilTpsRequest.class))
                .retrieve()
                .bodyToMono(RsPureXmlMessageResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}
