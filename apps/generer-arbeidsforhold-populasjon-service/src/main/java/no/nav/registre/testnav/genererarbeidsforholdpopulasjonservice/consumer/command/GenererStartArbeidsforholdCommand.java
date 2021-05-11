package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdResponse;

@Slf4j
@RequiredArgsConstructor
public class GenererStartArbeidsforholdCommand implements Callable<ArbeidsforholdResponse> {
    private final WebClient webClient;
    private final LocalDate startdate;
    private final String token;

    @Override
    public ArbeidsforholdResponse call() {
        log.info("Generer nytt arbeidsforhold den {}.", startdate);

        ArbeidsforholdResponse[] response = webClient
                .post()
                .uri("/api/v1/generate/amelding/arbeidsforhold/start")
                .body(BodyInserters.fromPublisher(Mono.just(new LocalDate[]{startdate}), LocalDate[].class))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(ArbeidsforholdResponse[].class)
                .block();

        if (response == null || response.length < 1) {
            throw new RuntimeException("Fikk ikke generert start arbeidsforhold for dato " + startdate.toString());
        }

        return response[0];
    }
}
