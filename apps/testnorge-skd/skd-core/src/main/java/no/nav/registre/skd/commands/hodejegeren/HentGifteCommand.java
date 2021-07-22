package no.nav.registre.skd.commands.hodejegeren;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class HentGifteCommand implements Callable<List<String>> {
    private final WebClient webClient;
    private final Long avspillergruppeId;
    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {};

    @Override
    public List<String> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/gifte-identer/")
                        .pathSegment(avspillergruppeId.toString())
                        .build())
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }
}
