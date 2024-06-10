package no.nav.skattekortservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class SokosPostCommand implements Callable<Mono<String>> {

    private static final String SOKOS_URL = "/api/v1/skattekort/opprett";

    private final WebClient webClient;
    private final String request;
    private final String token;

    @Override
    public Mono<String> call() {

        log.info("Base64 encoded", Base64.getDecoder().decode(request));

        return webClient
                .post()
                .uri(SOKOS_URL)
                .header(AUTHORIZATION, "Bearer " + token)
                .header("korrelasjonsid", UUID.randomUUID().toString())
                .body(BodyInserters.fromValue(Base64.getDecoder().decode(request)))
                .retrieve()
                .bodyToMono(String.class);
    }
}