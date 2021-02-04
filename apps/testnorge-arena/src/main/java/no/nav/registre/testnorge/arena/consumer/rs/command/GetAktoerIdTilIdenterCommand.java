package no.nav.registre.testnorge.arena.consumer.rs.command;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.response.AktoerResponse;
import no.nav.registre.testnorge.arena.consumer.rs.util.Headers;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

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
                    .header(Headers.CALL_ID, Headers.NAV_CALL_ID)
                    .header(Headers.CONSUMER_ID, Headers.NAV_CONSUMER_ID)
                    .header(Headers.AUTHORIZATION, idToken)
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

