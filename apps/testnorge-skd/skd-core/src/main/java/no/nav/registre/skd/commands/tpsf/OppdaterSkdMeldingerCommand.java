package no.nav.registre.skd.commands.tpsf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class OppdaterSkdMeldingerCommand implements Callable<List<Long>> {
    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {};
    private final WebClient webClient;
    private final List<RsMeldingstype> meldinger;

    @Override
    public List<Long> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/endringsmelding/skd/updatemeldinger")
                        .build())
                .body(BodyInserters.fromValue(meldinger))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }
}
