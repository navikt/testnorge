package no.nav.testnav.apps.tpsmessagingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class TestmiljoerServiceCommand implements Callable<Mono<String[]>> {

    private static final String MILJOER_URL = "/api/v1/miljoer";

    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<String[]> call() {

        return webClient.get()
                .uri(builder -> builder.path(MILJOER_URL).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String[].class);
    }
}
