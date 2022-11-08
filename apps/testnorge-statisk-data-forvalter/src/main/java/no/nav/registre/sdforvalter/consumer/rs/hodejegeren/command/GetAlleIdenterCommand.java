package no.nav.registre.sdforvalter.consumer.rs.hodejegeren.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.sdforvalter.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetAlleIdenterCommand implements Callable<List<String>> {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final Long avspillergruppeId;
    private final WebClient webClient;

    @Override
    public List<String> call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/api/v1/alle-identer/{avspillergruppeId}")
                                .build(avspillergruppeId)
                )
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .doOnError(WebClientFilter::logErrorMessage)
                .block();
    }
}