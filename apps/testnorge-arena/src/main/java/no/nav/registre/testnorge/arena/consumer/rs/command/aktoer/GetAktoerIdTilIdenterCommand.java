package no.nav.registre.testnorge.arena.consumer.rs.command.aktoer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.response.aktoer.AktoerResponse;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.AUTHORIZATION;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.CALL_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.CONSUMER_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.NAV_CALL_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.NAV_CONSUMER_ID;

@Slf4j
public class GetAktoerIdTilIdenterCommand implements Callable<Map<String, AktoerResponse>> {

    private final WebClient webClient;
    private final List<String> identer;
    private final String baseUrl;
    private final String idToken;

    private static final ParameterizedTypeReference<Map<String, AktoerResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    public GetAktoerIdTilIdenterCommand(List<String> identer, String baseUrl, String idToken, WebClient webClient) {
        this.webClient = webClient;
        this.identer = identer;
        this.baseUrl = baseUrl;
        this.idToken = idToken;
    }

    @Override
    public Map<String, AktoerResponse> call() {
        try {
            return webClient.get()
                    .uri(baseUrl + "/v1/identer?identgruppe=AktoerId&gjeldende=true")
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .header(AUTHORIZATION, idToken)
                    .header("Nav-Personidenter", identer.toString().substring(1, identer.toString().length() - 1))
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE)
                    .block();
        } catch (Exception e) {
            log.error("Kunne ikke hente aktør id for identer: {}", identer.toString().replaceAll("[\r\n]", ""), e);
            return Collections.emptyMap();
        }
    }

}

