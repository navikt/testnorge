package no.nav.skattekortservice.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.Callable;

import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class SokosGetCommand implements Callable<Mono<String>> {

    private static final String SOKOS_URL = "/api/v1/skattekort/hent/{fnr}/{inntektsaar}";

    private final WebClient webClient;
    private final String ident;
    private final String inntektsaar;
    private final String token;

    @Override
    public Mono<String> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(SOKOS_URL)
                        .build(ident, inntektsaar))
                .header(AUTHORIZATION, "Bearer " + token)
                .header("korrelasjonsid", UUID.randomUUID().toString())
                .retrieve()
                .bodyToMono(String.class);
    }
}