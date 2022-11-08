package no.nav.registre.sdforvalter.consumer.rs.tp.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class OpprettPersonerTpCommand implements Callable<Mono<List<String>>> {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final WebClient webClient;
    private final List<String> fnrs;
    private final String miljoe;

    protected static String getMessage(Throwable error) {

        return error instanceof WebClientResponseException webClientResponseException ?
                webClientResponseException.getResponseBodyAsString() :
                error.getMessage();
    }

    @Override
    public Mono<List<String>> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/orkestrering/opprettPersoner/{miljoe}")
                                .build(miljoe)
                )
                .body(BodyInserters.fromValue(fnrs))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .onErrorResume(throwable -> {
                    log.error(getMessage(throwable));
                    return Mono.empty();
                });
    }
}