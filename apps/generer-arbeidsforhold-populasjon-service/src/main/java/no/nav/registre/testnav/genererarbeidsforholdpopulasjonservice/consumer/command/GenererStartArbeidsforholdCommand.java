package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.Value;
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
import java.util.List;
import java.util.concurrent.Callable;

import no.nav.testnav.libs.dto.syntrest.v1.ArbeidsforholdResponse;

@Slf4j
@RequiredArgsConstructor
public class GenererStartArbeidsforholdCommand implements Callable<Mono<List<ArbeidsforholdResponse>>> {
    private final WebClient webClient;
    private final LocalDate startdate;
    private final String token;


    @Value
    private class Request {
        LocalDate startdato;
    }

    @Override
    public Mono<List<ArbeidsforholdResponse>> call() {
        log.info("Generer nytt arbeidsforhold den {}.", startdate);
        return webClient
                .post()
                .uri("/api/v1/arbeidsforhold/new")
                .body(BodyInserters.fromPublisher(Mono.just(new Request(startdate)), Request.class))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ArbeidsforholdResponse>>() {
                }).retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1))
                        .filter(throwable -> !(throwable instanceof WebClientResponseException.NotFound))
                ).map(value -> {
                    log.info("Nytt arbeidsforhold generert. (Antall: {})", value.size());
                    return value;
                });
    }
}
