package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class OrganisasjonForvalterCommand implements Callable<Map<String, Map<String, String>>> {

    private final WebClient webClient;
    private final String url;
    private final String query;
    private final String token;

    @Override
    public Map<String, Map<String, String>> call() {

        return webClient
                .get()
                .uri(builder -> builder.path(url).query(query).build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
