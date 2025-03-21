package no.nav.testnav.levendearbeidsforholdservice.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class EndreArbeidsforholdCommand implements Callable<Mono<ResponseEntity<Void>>> {

    private static final String NAV_ARBEIDSFORHOLD_KILDE = "Dolly-doedsfall-hendelse";
    private static final String MILJOE = "q2";

    private final WebClient webClient;
    private final Arbeidsforhold requests;
    private final String token;

    @SneakyThrows
    @Override
    public Mono<ResponseEntity<Void>> call() {
        return webClient
                .put()
                .uri(builder -> builder
                        .path("/{miljoe}/api/v1/arbeidsforhold/{navArbeidsforholdId}")
                        .build(MILJOE, requests.getNavArbeidsforholdId()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .header("Nav-Arbeidsforhold-Kildereferanse", NAV_ARBEIDSFORHOLD_KILDE)
                .header("Nav-Arbeidsforhold-Periode", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))
                .bodyValue(requests)
                .retrieve()
                .toBodilessEntity()
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log));
    }
}


