package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdRequest;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdResponse;

@Slf4j
@RequiredArgsConstructor
public class GenererArbeidsforholdHistorikkCommand implements Callable<List<ArbeidsforholdResponse>> {
    private final WebClient webClient;
    private final ArbeidsforholdRequest request;
    private final String token;

    @Override
    public List<ArbeidsforholdResponse> call() {
        log.info("Genererer arbeidsforhold historikk.");
        return webClient
                .post()
                .uri("/api/v1/generate/amelding/arbeidsforhold")
                .body(BodyInserters.fromPublisher(Mono.just(request), ArbeidsforholdRequest.class))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ArbeidsforholdResponse>>(){})
                .block();
    }
}
