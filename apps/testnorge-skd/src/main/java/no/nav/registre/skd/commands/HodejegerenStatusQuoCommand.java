package no.nav.registre.skd.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class HodejegerenStatusQuoCommand implements Callable<Map<String, String>> {
    private final WebClient webClient;
    private final String ident;
    private final String endringskode;
    private final String miljoe;
    private static final ParameterizedTypeReference<Map<String, String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {};

    @Override
    public Map<String, String> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/status-quo")
                        .queryParam("endringskode", endringskode)
                        .queryParam("miljoe", miljoe)
                        .queryParam("fnr", ident)
                        .build())
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }
}
