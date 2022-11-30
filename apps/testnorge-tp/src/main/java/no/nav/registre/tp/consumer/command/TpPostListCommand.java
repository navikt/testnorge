package no.nav.registre.tp.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.tp.util.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class TpPostListCommand<T> implements Callable<Mono<T>> {

    private final String url;
    private final Class<T> bodyType;
    private final T emptyValue;
    private final List<String> fnrs;
    private final WebClient webClient;

    public Mono<T> call() {
        return webClient.post()
                .uri(builder -> builder.path(url).build())
                .bodyValue(fnrs)
                .retrieve()

                .bodyToMono(bodyType)
                .defaultIfEmpty(emptyValue)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(10))
                        .filter(WebClientFilter::is5xxException));
    }

}
