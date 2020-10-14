package no.nav.registre.skd.commands.tpsf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class LagreSkdEndringseldingerITpsfCommand implements Callable<List<Long>> {
    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {};
    private final WebClient webClient;
    private final Long avspillergruppeId;
    private final List<RsMeldingstype> skdmeldinger;

    @Override
    public List<Long> call() {
        log.info("Lagrer {} SKD endringsmeldinger i tps-forvalteren med gruppe ID: {}", skdmeldinger.size(), avspillergruppeId);
        List<Long> response = webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/endringsmelding/skd/save/")
                        .pathSegment(avspillergruppeId.toString())
                        .build())
                .body(BodyInserters.fromValue(skdmeldinger))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();

        if (response != null) {
            log.info("{} rader ble lagret i TPSF.", response.get(0));
        } else {
            throw new RuntimeException("Feil ved lagring til TPSF.");
        }
        return response;
    }
}
