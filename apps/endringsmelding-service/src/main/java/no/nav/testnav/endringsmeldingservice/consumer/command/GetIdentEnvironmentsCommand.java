package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.endringsmeldingservice.consumer.dto.StatusPaaIdenterDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetIdentEnvironmentsCommand implements Callable<Mono<Set<String>>> {
    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<Set<String>> call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/api/v1/testdata/tpsStatus")
                        .queryParam("identer", ident)
                        .queryParam("includeProd", false).build()
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(StatusPaaIdenterDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .map(value -> {
                    if (value.getStatusPaaIdenter().isEmpty() || value.getStatusPaaIdenter().get(0).getEnv().isEmpty()) {
                        return Collections.emptySet();
                    }
                    return value.getStatusPaaIdenter().get(0).getEnv();
                });
    }
}
