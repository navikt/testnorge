package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdRequest;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdResponse;

@Slf4j
@RequiredArgsConstructor
public class GenererArbeidsforholdHistorikkCommand implements Callable<Mono<List<List<ArbeidsforholdResponse>>>> {
    private final WebClient webClient;
    private final List<ArbeidsforholdRequest> requests;
    private final String token;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Mono<List<List<ArbeidsforholdResponse>>> call() {
        log.info("Genererer arbeidsforhold historikk.");
        var body = requests.toArray(new ArbeidsforholdRequest[requests.size()]);
        return webClient
                .post()
                .uri("/api/v1/generate/amelding/arbeidsforhold/historikk")
                .body(BodyInserters.fromPublisher(Mono.just(body), ArbeidsforholdRequest[].class))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<List<ArbeidsforholdResponse>>>() {
                }).retryWhen(Retry.max(1)
                        .filter(throwable -> !(
                                throwable instanceof WebClientResponseException.NotFound
                                        || throwable instanceof WebClientResponseException.BadRequest
                        ))
                ).doOnError(error -> {
                    try {
                        log.error("Feil ved opprettelse av historikk med body: {}.", objectMapper.writeValueAsString(body), error);
                    } catch (JsonProcessingException e) {
                        log.error("Feil ved convertering av body til string.", e);
                    }
                });
    }
}
