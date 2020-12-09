package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdRequest;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdResponse;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdWithHistorikkRequest;

@Slf4j
@DependencyOn("syntrest")
@RequiredArgsConstructor
public class GenerateNextArbeidsforholdWithHistorikkCommand implements Callable<ArbeidsforholdResponse> {
    private final WebClient webClient;
    private final ArbeidsforholdWithHistorikkRequest arbeidsforholdDTO;

    @Override
    public ArbeidsforholdResponse call() {
        log.info("Generer neste arbeidsforhold.");
        return webClient
                .post()
                .uri("/api/v1/generate/amelding/arbeidsforhold")
                .body(BodyInserters.fromPublisher(Mono.just(arbeidsforholdDTO), ArbeidsforholdWithHistorikkRequest.class))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(ArbeidsforholdResponse.class)
                .block();
    }
}
