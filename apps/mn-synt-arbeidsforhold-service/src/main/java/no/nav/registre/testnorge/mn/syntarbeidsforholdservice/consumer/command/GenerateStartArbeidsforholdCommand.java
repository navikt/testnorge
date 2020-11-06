package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdResponse;

@DependencyOn("syntrest")
@RequiredArgsConstructor
public class GenerateStartArbeidsforholdCommand implements Callable<ArbeidsforholdResponse> {
    private final WebClient webClient;
    private final LocalDate startdate;

    @Override
    public ArbeidsforholdResponse call() {
        ArbeidsforholdResponse[] array = webClient
                .post()
                .uri("/api/v1/generate/amelding/arbeidsforhold/start")
                .body(BodyInserters.fromPublisher(Mono.just(new LocalDate[]{startdate}), LocalDate[].class))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(ArbeidsforholdResponse[].class)
                .block();
        return array[0];
    }
}
