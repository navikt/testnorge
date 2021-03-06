package no.nav.testnav.apps.personservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;
import java.util.concurrent.Callable;

import no.nav.testnav.apps.personservice.consumer.header.PdlHeaders;

@Slf4j
@RequiredArgsConstructor
public class GetTagsCommand implements Callable<Set<String>> {

    private static final ParameterizedTypeReference<Set<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Set<String> call() {
        log.info("Henter tags for ident {}", ident);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/bestilling/tags").build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(PdlHeaders.NAV_PERSONIDENT, ident)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }
}
