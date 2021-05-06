package no.nav.registre.inntekt.consumer.rs.altinninntekt.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs.RsInntektsmelding;

@Slf4j
@RequiredArgsConstructor
public class GenererInntektsmeldingCommand implements Callable<String> {

    private final WebClient webClient;
    private final RsInntektsmelding dto;
    private final String token;

    @Override
    public String call() {
        var arbeidsgiver = dto.getArbeidsgiver() == null
                ? dto.getArbeidsgiverPrivat().getArbeidsgiverFnr()
                : dto.getArbeidsgiver().getVirksomhetsnummer();

        try {
            log.info("Gennerer inntektsmelding for arbeidsgiver {}...", arbeidsgiver);
            var response = webClient
                    .post()
                    .uri("/api/v2/inntektsmelding/2018/12/11")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(BodyInserters.fromPublisher(Mono.just(dto), RsInntektsmelding.class))
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                    .block();
            log.info("Inntektsmelding generert for arbeidsgiver {}.", arbeidsgiver);
            return response;

        } catch (WebClientResponseException e) {
            log.error(
                    "Feil ved innsendelse av inntektsmelding. Feilmelding: {}.",
                    e.getResponseBodyAsString()
            );
            throw e;
        }
    }
}
