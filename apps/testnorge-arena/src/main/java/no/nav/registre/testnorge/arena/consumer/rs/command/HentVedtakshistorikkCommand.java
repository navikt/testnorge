package no.nav.registre.testnorge.arena.consumer.rs.command;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.util.WebClientFilter;
import no.nav.testnav.libs.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@AllArgsConstructor
public class HentVedtakshistorikkCommand implements Callable<List<Vedtakshistorikk>> {

    private static final ParameterizedTypeReference<List<String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<Vedtakshistorikk>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final WebClient webClient;
    private final List<String> oppstartsdatoer;
    private final String token;

    @Override
    public List<Vedtakshistorikk> call() {
        try {
            log.info("Henter vedtakshistorikk.");
            return webClient.post()
                    .uri(builder ->
                            builder.path("/api/v1/vedtakshistorikk")
                                    .build()
                    )
                    .header("Authorization", "Bearer " + token)
                    .body(BodyInserters.fromPublisher(Mono.just(oppstartsdatoer), REQUEST_TYPE))
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke hente vedtakshistorikk.", e);
            return Collections.emptyList();
        }
    }

}
