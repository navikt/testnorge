package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class OrganisasjonBestillingIdsCommand implements Callable<Mono<String[]>> {

    private static final String STATUS_URL = "/api/v2/order/{uuid}/ids";

    private final WebClient webClient;
    private final String uuid;
    private final String token;

    @Override
    public Mono<String[]> call() {

        return webClient.get()
                .uri(STATUS_URL.replace("{uuid}", uuid))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String[].class)
                .doOnError(throwable ->
                        log.error(throwable instanceof WebClientResponseException ?
                                ((WebClientResponseException) throwable).getResponseBodyAsString() :
                                throwable.getMessage()));
    }
}