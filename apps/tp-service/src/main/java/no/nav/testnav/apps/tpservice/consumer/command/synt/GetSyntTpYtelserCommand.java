package no.nav.testnav.apps.tpservice.consumer.command.synt;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpservice.database.models.TYtelse;
import no.nav.testnav.apps.tpservice.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetSyntTpYtelserCommand implements Callable<Mono<List<TYtelse>>> {

    private static final ParameterizedTypeReference<List<TYtelse>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final int antall;
    private final String token;
    private final WebClient webClient;

    @Override
    public Mono<List<TYtelse>> call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/api/v1/generate/tp/{num_to_generate}")
                                .build(antall)
                )
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
