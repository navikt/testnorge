package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.AUTHORIZATION;

@Slf4j
@AllArgsConstructor
public class HentVedtakshistorikkCommand implements Callable<Mono<List<Vedtakshistorikk>>> {

    private final WebClient webClient;
    private final List<String> oppstartsdatoer;
    private final String token;

    private static final ParameterizedTypeReference<List<String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<Vedtakshistorikk>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public Mono<List<Vedtakshistorikk>> call() {
        try {
            log.info("Henter vedtakshistorikk.");
            return webClient.post()
                    .uri(builder ->
                            builder.path("/api/v1/vedtakshistorikk")
                                    .build()
                    )
                    .header(AUTHORIZATION, "Bearer " + token)
                    .body(BodyInserters.fromPublisher(Mono.just(oppstartsdatoer), REQUEST_TYPE))
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE);
        } catch (Exception e) {
            log.error("Klarte ikke hente vedtakshistorikk.", e);
            return Mono.empty();
        }
    }

}
