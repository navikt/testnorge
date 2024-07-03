package no.nav.registre.testnorge.levendearbeidsforhold.consumers.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class EndreArbeidsforholdCommand implements Callable<Mono<Arbeidsforhold>> {
    private final WebClient webClient;
    private final Arbeidsforhold requests;
    private final String token;
    private final ObjectMapper objectMapper;
    private final String navArbeidsforholdKilde = "Dolly-doedsfall-hendelse" ;

    @SneakyThrows
    @Override
    public Mono<Arbeidsforhold> call() {

        return webClient
                .put()
                .uri("/api/v1/arbeidsforhold/{navArbeidsforholdId}",
                        requests.getArbeidsforholdId())
                .body(requests, Arbeidsforhold.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("Nav-Arbeidsforhold-Kildereferanse", navArbeidsforholdKilde)
                .header("Nav-Arbeidsforhold-Periode", String.valueOf(LocalDate.now()))
                .header("navArbeidsforholdId", String.valueOf(requests.getNavArbeidsforholdId()))
                .retrieve()
                .bodyToMono(Arbeidsforhold.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(error -> {
                    try {
                        log.error("Feil ved oppdatering av arbeidsforhold med body: {}.", objectMapper.writeValueAsString(requests), error);
                    } catch (JsonProcessingException e) {
                        log.error("Feil ved convertering av body til string.", e);
                    }
                });
    }
}


