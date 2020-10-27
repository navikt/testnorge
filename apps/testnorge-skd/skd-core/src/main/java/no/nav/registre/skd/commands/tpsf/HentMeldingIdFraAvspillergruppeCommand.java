package no.nav.registre.skd.commands.tpsf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class HentMeldingIdFraAvspillergruppeCommand implements Callable<List<Long>> {
    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {};
    private final WebClient webClient;
    private final Long avspillergruppeId;

    @Override
    public List<Long> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/endringsmelding/skd/meldinger/")
                        .pathSegment(avspillergruppeId.toString())
                        .build())
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();

    }
}
