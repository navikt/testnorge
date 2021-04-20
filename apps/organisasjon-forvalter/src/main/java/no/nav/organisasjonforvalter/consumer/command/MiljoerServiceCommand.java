package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class MiljoerServiceCommand implements Callable<String[]> {

    private static final String MILJOER_URL = "/api/v1/miljoer";

    private final WebClient webClient;
    private final String token;

    @Override
    public String[] call() {

        return webClient.get()
                .uri(MILJOER_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String[].class)
                .block();
    }
}
