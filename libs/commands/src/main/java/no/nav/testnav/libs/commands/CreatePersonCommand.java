package no.nav.testnav.libs.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.person.v1.PersonDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class CreatePersonCommand implements Callable<Mono<Void>> {
    private final WebClient webClient;
    private final PersonDTO person;
    private final String accessToken;
    private final String kilde;

    @Override
    public Mono<Void> call() {
        log.info("Oppretter {}...", person.getIdent());
        return webClient
                .post()
                .uri(builder -> builder.path("/api/v1/personer").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("kilde", kilde)
                .body(BodyInserters.fromPublisher(Mono.just(person), PersonDTO.class))
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnSuccess(value -> log.info("Person {} opprettet.", person.getIdent()));
    }
}