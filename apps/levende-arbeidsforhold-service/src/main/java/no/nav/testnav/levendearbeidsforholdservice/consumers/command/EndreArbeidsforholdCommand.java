package no.nav.testnav.levendearbeidsforholdservice.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class EndreArbeidsforholdCommand implements Callable<Mono<ResponseEntity<Void>>> {

    private static final String navArbeidsforholdKilde = "Dolly-doedsfall-hendelse" ;
    private static final String miljoe = "q2";

    private final WebClient webClient;
    private final Arbeidsforhold requests;
    private final String token;

    @SneakyThrows
    @Override
    public Mono<ResponseEntity<Void>> call() {

        return  webClient.put()
            .uri(builder -> builder.path("/{miljoe}/api/v1/arbeidsforhold/{navArbeidsforholdId}")
                    .build(miljoe, requests.getNavArbeidsforholdId()))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .header("Nav-Arbeidsforhold-Kildereferanse", navArbeidsforholdKilde)
            .header("Nav-Arbeidsforhold-Periode", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))
            .bodyValue(requests)
            .retrieve()
            .toBodilessEntity()
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                    .filter(WebClientFilter::is5xxException))
                    .doOnError(WebClientFilter::logErrorMessage);
    }
}


