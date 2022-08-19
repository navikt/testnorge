package no.nav.testnav.apps.tpservice.consumer.command.hodejegeren;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetLevendeIdenterCommand implements Callable<List<String>> {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final Long avspillergruppeId;
    private final String miljoe;
    private final int antallIdenter;
    private final int minAlder;
    private final WebClient webClient;

    @Override
    public List<String> call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/api/v1/levende-identer/{avspillergruppeId}")
                                .queryParam("miljoe", miljoe)
                                .queryParam("antallIdenter", antallIdenter)
                                .queryParam("minimumAlder", minAlder)
                                .build(avspillergruppeId)
                )
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }
}
