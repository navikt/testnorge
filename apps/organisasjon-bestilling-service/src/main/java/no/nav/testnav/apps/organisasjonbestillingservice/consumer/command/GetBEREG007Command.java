package no.nav.testnav.apps.organisasjonbestillingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.organisasjonbestillingservice.consumer.dto.BuildDTO;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetBEREG007Command implements Callable<Mono<BuildDTO>> {
    private final WebClient webClient;
    private final String token;
    private final Long buildId;

    @Override
    public Mono<BuildDTO> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("view/All/job/Start_BEREG007/{buildId}/api/json").build(buildId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(BuildDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(
                        throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty()
                );
    }
}
