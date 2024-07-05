package no.nav.registre.testnorge.levendearbeidsforhold.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforhold.domain.v1.Arbeidsforhold;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class EndreArbeidsforholdCommand implements Callable<Mono<Arbeidsforhold>> {
    private final WebClient webClient;
    private final Arbeidsforhold requests;
    private final String token;
    private final String miljoe = "q2";
    private final String navArbeidsforholdKilde = "Dolly-doedsfall-hendelse";

    @SneakyThrows
    @Override
    public Mono<Arbeidsforhold> call() {

        return webClient
                .put()
                .uri(builder -> builder.path("/{miljoe}/api/v1/arbeidsforhold/{navArbeidsforholdId}")
                        .build(miljoe, requests.getNavArbeidsforholdId()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("Nav-Arbeidsforhold-Kildereferanse", navArbeidsforholdKilde)
                .header("Nav-Arbeidsforhold-Periode", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))
                .body(BodyInserters.fromValue(requests))
                .retrieve()
                .bodyToMono(Arbeidsforhold.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .map(arbeidsforhold1 -> Arbeidsforhold.builder().build());
    }
}


