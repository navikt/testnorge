package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.aareg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.v1.Arbeidsforhold;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;
@Slf4j
@RequiredArgsConstructor
public class OpprettArbeidsforholdCommand implements Callable<ResponseEntity<Arbeidsforhold>> {
    private final WebClient webClient;
    private final Arbeidsforhold requests;
    private final String token;
    private final String miljoe = "q2";
    private final String navArbeidsforholdKilde = "Dolly-doedsfall-hendelse" ;

    @Override
    public ResponseEntity<Arbeidsforhold> call() {
        return webClient.post()
                .uri(builder -> builder.path("/{miljoe}/api/v1/arbeidsforhold")
                        .build(miljoe))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("Nav-Arbeidsforhold-Kildereferanse", navArbeidsforholdKilde)
                .body(BodyInserters.fromValue(requests))
                .retrieve()
                .toEntity(Arbeidsforhold.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(error -> log.error("Feil ved opprettelse av arbeidsforhold: {}", error.getMessage()))
                .block();
    }
}
