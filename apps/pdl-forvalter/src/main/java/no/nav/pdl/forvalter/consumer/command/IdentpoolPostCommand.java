package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class IdentpoolPostCommand implements Callable<String[]> {

    private final WebClient webClient;
    private final String url;
    private final String query;
    private final Object body;
    private final String token;

    @Override
    public String[] call() {

        return webClient
                .post()
                .uri(builder -> builder.path(url).query(query).build())
                .body(BodyInserters.fromValue(body))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String[].class)
                .block();
    }
}
