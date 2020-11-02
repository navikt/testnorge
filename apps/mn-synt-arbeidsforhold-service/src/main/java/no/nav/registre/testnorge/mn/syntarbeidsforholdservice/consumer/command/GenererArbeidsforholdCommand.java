package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdRequest;

@DependencyOn("syntrest")
@RequiredArgsConstructor
public class GenererArbeidsforholdCommand implements Callable<ArbeidsforholdRequest> {
    private final WebClient webClient;
    private final ArbeidsforholdRequest arbeidsforholdDTO;

    @Override
    public ArbeidsforholdRequest call() {
        return webClient
                .post()
                .uri("/api/v1/generate/arbeidsforhold/sklearn")
                .body(BodyInserters.fromPublisher(Mono.just(arbeidsforholdDTO), ArbeidsforholdRequest.class))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(ArbeidsforholdRequest.class)
                .block();
    }
}
