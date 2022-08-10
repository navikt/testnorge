package no.nav.testnav.apps.skdservice.consumer.command;

import lombok.AllArgsConstructor;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class GetMeldingsIdsCommand implements Callable<Mono<List<Long>>> {

    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final Long gruppeId;
    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<List<Long>> call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/api/v1/endringsmelding/skd/meldinger/{gruppeId}")
                                .build(gruppeId)
                )
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
