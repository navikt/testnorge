package no.nav.registre.syntrest.consumer.command;

import java.util.List;
import java.util.concurrent.Callable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.aareg.amelding.Arbeidsforhold;
import reactor.core.publisher.Mono;

@Slf4j
public class PostArbeidsforholdStartCommand implements Callable<List<Arbeidsforhold>> {

    private final WebClient webClient;
    private final List<String> startdatoer;
    private final String syntAmeldingUrlPath;
    private final String queryString;

    private static final ParameterizedTypeReference<List<String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<Arbeidsforhold>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    public PostArbeidsforholdStartCommand(List<String> startdatoer, String syntAmeldingUrlPath, String queryString, WebClient webClient) {
        this.webClient = webClient;
        this.startdatoer = startdatoer;
        this.syntAmeldingUrlPath = syntAmeldingUrlPath;
        this.queryString = queryString;
    }

    @Override
    public List<Arbeidsforhold> call() {
        try {
            var body = BodyInserters.fromPublisher(Mono.just(startdatoer), REQUEST_TYPE);

            return webClient.post()
                    .uri(builder -> builder
                            .path(syntAmeldingUrlPath)
                            .query(queryString)
                            .build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE)
                    .block();
        } catch (Exception e) {
            log.error("Unexpected Rest Client Exception.", e);
            throw e;
        }
    }

}
