package no.nav.registre.skd.consumer.command;

import java.util.List;
import java.util.concurrent.Callable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class PostTpIdenterCommand implements Callable<List<String>> {

    private final List<String> identer;
    private final String miljoe;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<String>> LIST_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<String> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/v1/orkestrering/opprettPersoner/{miljoe}")
                                .build(miljoe)
                )
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromPublisher(Mono.just(identer), LIST_TYPE))
                .retrieve()
                .bodyToMono(LIST_TYPE)
                .block();
    }
}
