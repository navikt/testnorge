package no.nav.registre.tp.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetLevendeIdenterCommand implements Callable<Mono<List<String>>> {
    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final Long avspillergruppeId;
    private final String token;
    private final WebClient webClient;

    @Override
    public Mono<List<String>> call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/api/v1/alle-levende-identer/{avspillergruppeId}")
                                .build(avspillergruppeId)
                )
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE);
    }
}
