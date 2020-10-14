package no.nav.registre.skd.commands.tpsf;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class HentMeldingerCommand implements Callable<List<RsMeldingstype>> {
    private static final ParameterizedTypeReference<List<RsMeldingstype>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {};
    private static final int PAGE_SIZE = 80;
    private final WebClient webClient;
    private final List<String> ider;

    @Override
    public List<RsMeldingstype> call() {
        log.info("Henter {} meldinger fra TPSF.", ider.size());
        List<RsMeldingstype> response = new ArrayList<>(ider.size());
        Lists.partition(ider, PAGE_SIZE).forEach(partisjon -> response.addAll(
                Objects.requireNonNull(webClient
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/v1/endringsmelding/skd/meldinger")
                                .queryParam("ids", String.join(",", partisjon))
                                .build())
                        .retrieve()
                        .bodyToMono(RESPONSE_TYPE)
                        .block())));
        log.info("{}/{} meldinger ble hentet fra TPSF.", response.size(), ider.size());
        return response;
    }
}
