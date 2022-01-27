package no.nav.registre.syntrest.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.syntrest.domain.aareg.Arbeidsforholdsmelding;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class PostSyntAaregCommand implements Callable<List<Arbeidsforholdsmelding>> {

    private final List<String> fnrs;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<Arbeidsforholdsmelding>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<Arbeidsforholdsmelding> call() {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/generate_aareg")
                        .build())
                .body(Mono.just(fnrs), REQUEST_TYPE)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }
}
