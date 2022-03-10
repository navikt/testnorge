package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.syntrest.v1.ArbeidsforholdRequest;
import no.nav.testnav.libs.dto.syntrest.v1.ArbeidsforholdResponse;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

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
                .uri("/api/v1/arbeidsforhold/historikk")
                .body(BodyInserters.fromPublisher(Mono.just(body), ArbeidsforholdRequest[].class))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<List<ArbeidsforholdResponse>>>() {
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(error -> {
                    try {
                        log.error("Feil ved opprettelse av historikk med body: {}.", objectMapper.writeValueAsString(body), error);
                    } catch (JsonProcessingException e) {
                        log.error("Feil ved convertering av body til string.", e);
                    }
                });
    }
}
