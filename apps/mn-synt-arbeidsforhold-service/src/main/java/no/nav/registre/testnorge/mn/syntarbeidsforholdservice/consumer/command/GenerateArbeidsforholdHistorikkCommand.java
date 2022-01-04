package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command;

import java.util.List;
import java.util.concurrent.Callable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.syntrest.v1.ArbeidsforholdRequest;
import no.nav.testnav.libs.dto.syntrest.v1.ArbeidsforholdResponse;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class GenerateArbeidsforholdHistorikkCommand implements Callable<List<ArbeidsforholdResponse>> {

    private final WebClient webClient;
    private final ArbeidsforholdRequest arbeidsforholdDTO;
    private final String token;

    @Override
    public List<ArbeidsforholdResponse> call() {
        log.info("Genererer arbeidsforhold historikk.");

        return webClient
                .post()
                .uri("/api/v1/arbeidsforhold")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromPublisher(Mono.just(arbeidsforholdDTO), ArbeidsforholdRequest.class))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ArbeidsforholdResponse>>(){})
                .block();
    }
}
