package no.nav.registre.testnorge.levendearbeidsforhold.consumers.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforhold.domain.v1.Arbeidsforhold;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class EndreArbeidsforholdCommand implements Callable<Mono<Arbeidsforhold>> {
    private final WebClient webClient;
    private final Arbeidsforhold requests;
    private final String token;
    private final ObjectMapper objectMapper;
    private final String navArbeidsforholdKilde = "Dolly-doedsfall-hendelse" ;

    private static String getNavArbeidsfoholdPeriode() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    @SneakyThrows
    @Override
    public Mono<Arbeidsforhold> call() {

        try{
            Mono<Arbeidsforhold> requets = webClient
                    .put()
                    .uri(builder -> builder.path("/q2/api/v1/arbeidsforhold/{navArbeidsforholdId}")
                            .build(requests.getNavArbeidsforholdId()))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header("Nav-Arbeidsforhold-Kildereferanse", navArbeidsforholdKilde)
                    .header("Nav-Arbeidsforhold-Periode", getNavArbeidsfoholdPeriode())
                    .body(BodyInserters.fromValue(requests))
                    .retrieve()
                    .bodyToMono(Arbeidsforhold.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .map(arbeidsforhold1 -> {
                        return Arbeidsforhold.builder().build();
                    });
            //Function<WebClient.ResponseSpec, String> toString = WebClient.ResponseSpec::toString;
            log.info(requests.toString());

            return requets;
        }

        catch (
                WebClientResponseException.NotFound e) {
            log.warn("Får ikke endret {} i miljø {}", requests.getNavArbeidsforholdId());
        } catch (WebClientResponseException e) {
            log.error(
                    "Klarer ikke å hente arbeidsforhold for navArbeidsforhold: {}. Feilmelding: {}.", requests.getNavArbeidsforholdId());
            e.getResponseBodyAsString();
            throw e;
        };
        return null;
    }
}


