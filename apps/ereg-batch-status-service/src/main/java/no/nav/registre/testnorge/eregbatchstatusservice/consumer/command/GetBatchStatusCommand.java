package no.nav.registre.testnorge.eregbatchstatusservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.eregbatchstatusservice.util.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetBatchStatusCommand implements Callable<Long> {
    private final WebClient webClient;
    private final Long id;

    @Override
    public Long call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/ereg/internal/batch/poll/{id}").build(id))
                .retrieve()
                .bodyToMono(Long.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}
