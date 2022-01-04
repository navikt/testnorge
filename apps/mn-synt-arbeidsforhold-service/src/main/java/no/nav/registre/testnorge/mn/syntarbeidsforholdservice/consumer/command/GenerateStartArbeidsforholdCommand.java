package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.concurrent.Callable;

import no.nav.testnav.libs.dto.syntrest.v1.ArbeidsforholdResponse;

@Slf4j
@RequiredArgsConstructor
public class GenerateStartArbeidsforholdCommand implements Callable<ArbeidsforholdResponse> {
    private final WebClient webClient;
    private final LocalDate startdate;
    private final String token;

    @Override
    public ArbeidsforholdResponse call() {
        log.info("Generer nytt arbeidsforhold.");

        try {
            ArbeidsforholdResponse[] array = webClient
                    .post()
                    .uri("/api/v1/arbeidsforhold/start")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromPublisher(Mono.just(new LocalDate[]{startdate}), LocalDate[].class))
                    .retrieve()
                    .bodyToMono(ArbeidsforholdResponse[].class)
                    .block();

            if (array == null || array.length < 1) {
                throw new RuntimeException("Fikk ikke generert start arbeidsforhold for dato " + startdate.toString());
            }

            return array[0];
        } catch (WebClientResponseException e) {
            log.error(
                    "Feil ved henting av start arbeidsforhold på dato {}. Feilmelding: {}",
                    startdate,
                    e.getResponseBodyAsString()
            );
            throw e;
        }
    }
}
