package no.nav.registre.endringsmeldinger.consumer.rs.command;

import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class GetLevendeIdenterCommand implements Callable<List<String>> {

    private final Long avspillergruppeId;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<String> call() {
            return webClient.get()
                    .uri(builder ->
                            builder.path("/api/v1/alle-levende-identer/{avspillergruppeId}")
                                    .build(avspillergruppeId)
                    )
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE)
                    .block();
    }
}
