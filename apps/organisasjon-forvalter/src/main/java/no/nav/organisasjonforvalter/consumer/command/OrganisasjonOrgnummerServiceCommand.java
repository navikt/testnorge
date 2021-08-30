package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class OrganisasjonOrgnummerServiceCommand implements Callable<Mono<String[]>> {

    private static final String NUMBER_URL = "/api/v1/orgnummer";

    private final WebClient webClient;
    private final Integer antall;
    private final String token;

    @Override
    public Mono<String[]> call() {

        return webClient.get()
                .uri(NUMBER_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("antall", antall.toString())
                .retrieve()
                .bodyToMono(String[].class);
    }
}
