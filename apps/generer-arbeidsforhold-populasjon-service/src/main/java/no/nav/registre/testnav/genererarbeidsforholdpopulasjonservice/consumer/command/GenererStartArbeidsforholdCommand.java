package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdResponse;

@Slf4j
@RequiredArgsConstructor
public class GenererStartArbeidsforholdCommand implements Callable<Mono<ArbeidsforholdResponse>> {
    private final WebClient webClient;
    private final LocalDate startdate;
    private final String token;

    @Override
    public Mono<ArbeidsforholdResponse> call() {
        log.info("Generer nytt arbeidsforhold den {}.", startdate);
        return webClient
                .post()
                .uri("/api/v1/generate/amelding/arbeidsforhold/start")
                .body(BodyInserters.fromPublisher(Mono.just(new LocalDate[]{startdate}), LocalDate[].class))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ArrayList<ArbeidsforholdResponse>>() {
                }).retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1))
                        .filter(throwable -> !(throwable instanceof WebClientResponseException.NotFound))
                ).map(value -> {
                    log.info("Nytt arbeidsforhold generert.");
                    return value.get(0);
                });
    }
}
