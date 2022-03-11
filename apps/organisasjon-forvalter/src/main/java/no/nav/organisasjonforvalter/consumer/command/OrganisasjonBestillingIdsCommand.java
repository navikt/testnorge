package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.jpa.entity.Status;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class OrganisasjonBestillingIdsCommand implements Callable<Mono<Status>> {

    private static final String STATUS_URL = "/api/v2/order/{uuid}/ids";

    private final WebClient webClient;
    private final Status status;
    private final String token;

    @Override
    public Mono<Status> call() {

        return webClient.get()
                .uri(STATUS_URL.replace("{uuid}", status.getUuid()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String[].class)
                .flatMap(value -> {
                    status.setBestId(Stream.of(value).findFirst().orElse(null));
                    return Mono.just(status);
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(throwable ->
                        log.error(throwable instanceof WebClientResponseException ?
                                ((WebClientResponseException) throwable).getResponseBodyAsString() :
                                throwable.getMessage()));
    }
}