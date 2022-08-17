package no.nav.registre.hodejegeren.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.hodejegeren.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetTpsIdenterCommand implements Callable<Set<String>> {

    private static final ParameterizedTypeReference<Set<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final WebClient webClient;
    private final String aarsakkode;
    private final String transaksjonstype;
    private final Long avspillergruppeId;

    @Override
    public Set<String> call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/endringsmelding/skd/identer/{avspillergruppeId}")
                        .queryParam("aarsakskode", aarsakkode)
                        .queryParam("transaksjonstype", transaksjonstype)
                        .build(avspillergruppeId)
                )
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();

    }
}
