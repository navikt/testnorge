package no.nav.registre.sdforvalter.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.registre.sdforvalter.consumer.rs.command.SkdStartAvspillingCommand.getMessage;

@Slf4j
@RequiredArgsConstructor
public class OpprettPersonerTpCommand implements Callable<Mono<List<String>>> {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final WebClient webClient;
    private final String token;
    private final List<String> fnrs;
    private final String miljoe;

    @Override
    public Mono<List<String>> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/orkestrering/opprettPersoner/{miljoe}")
                                .build(miljoe)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromValue(fnrs))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .onErrorResume(throwable -> {
                    log.error(getMessage(throwable));
                    return Mono.empty();
                });
    }
}

